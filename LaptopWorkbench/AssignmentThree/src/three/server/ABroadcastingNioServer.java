package three.server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import port.trace.nio.SocketChannelInterestOp;
import port.trace.nio.SocketChannelRead;
import port.trace.nio.SocketChannelWritten;
import three.monitors.MonitorFactory;
import three.util.ChangeRequest;

public class ABroadcastingNioServer implements BroadcastingNioServer,Runnable {
	private boolean synchronizedReply;
	
	// The host:port combination to listen on
	private int port;

	// The channel on which we'll accept connections
	private ServerSocketChannel serverChannel;

	// The selector we'll be monitoring
	private Selector selector;

	// The buffer into which we'll read data when it's available
	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

	// A list of PendingChange instances
	private List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();

	// Maps a SocketChannel to a list of ByteBuffer instances
	private Map<SocketChannel,List<ByteBuffer>> pendingData = new HashMap<SocketChannel,List<ByteBuffer>>();
	
	private List<SocketChannel> openSockets = new ArrayList<SocketChannel>();

	public ABroadcastingNioServer(int port) throws IOException {
		this.port = port;
		this.selector = this.initSelector();
	}

	public void sendToAll(List<SocketChannel> sockets, byte[] data) {
		synchronized (this.pendingChanges) {
			// Indicate we want the interest operations set changed
			//make the change for each of the sockeets which currently have a connection
			for(int i = 0; i < sockets.size(); i++) {
				if (sockets.get(i).isConnected()) {
					this.pendingChanges.add(new ChangeRequest(sockets.get(i), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

					// And queue the data we want written
					synchronized (this.pendingData) {
						List<ByteBuffer> queue = (List<ByteBuffer>) this.pendingData.get(sockets.get(i));
						if (queue == null) {
							queue = new ArrayList<ByteBuffer>();
							this.pendingData.put(sockets.get(i), queue);
						}
						queue.add(ByteBuffer.wrap(data));
					}	
				}
			}
			
		}
		// Finally, wake up our selecting thread so it can make the required changes
		this.selector.wakeup();
	}

	public void run() {
		while (true) {
			try {
				// Process any pending changes
				synchronized (this.pendingChanges) {
					Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
					while (changes.hasNext()) {
						ChangeRequest change = (ChangeRequest) changes.next();
						switch (change.type) {
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
							SocketChannelInterestOp.newCase(this, key, change.ops);
						}
					}
					this.pendingChanges.clear();
				}

				// Wait for an event one of the registered channels
				
				this.selector.select();
				if (this.synchronizedReply) {
					MonitorFactory.getRaceLock().lock(this);
				}
				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}

					// Check what event is available and deal with it
					if (key.isAcceptable()) {
						this.accept(key);
					} else if (key.isReadable()) {
						this.read(key);
					} else if (key.isWritable()) {
						this.write(key);
					}
				}
				if (this.synchronizedReply){
					MonitorFactory.getRaceLock().unlock();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		//Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(this.selector, SelectionKey.OP_READ);
		synchronized (this.openSockets){
			if (this.openSockets.contains(socketChannel)) {
				//do nothing
			} else {
				this.openSockets.add(socketChannel);
			}
		}
	}

	private synchronized void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try {
			
			numRead = socketChannel.read(this.readBuffer);
			SocketChannelRead.newCase(this, socketChannel, this.readBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			key.channel().close();
			key.cancel();
			return;
		}

		// Hand the data off to our worker thread
		processData(this, this.openSockets, this.readBuffer.array(), numRead);
	}
	
	private void processData(ABroadcastingNioServer server, List<SocketChannel> sockets, byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		sendToAll(this.openSockets,dataCopy);
	
	}

	private synchronized void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingData) {
			List<ByteBuffer> queue = (List<ByteBuffer>) this.pendingData.get(socketChannel);
			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
				socketChannel.write(buf);
				SocketChannelWritten.newCase(this, socketChannel, buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	private Selector initSelector() throws IOException {
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();

		// Create a new non-blocking server socket channel
		this.serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(this.port);
		serverChannel.socket().bind(isa);
		// Register the server socket channel, indicating an interest in 
		// accepting new connections
		serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		return socketSelector;
	}

	public void setSynchronizedReply(boolean toSet) {
		this.synchronizedReply = toSet;
		
	}
}
