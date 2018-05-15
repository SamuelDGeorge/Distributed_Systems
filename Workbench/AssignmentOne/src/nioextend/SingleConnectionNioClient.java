package nioextend;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SingleConnectionNioClient implements Runnable {
	// The host:port combination to connect to
	protected InetAddress hostAddress;
	protected int port;
	protected boolean connected;
	
	// The selector we'll be monitoring
	protected Selector selector;
	
	
	//make a string for the latest response
	protected String currentResponse;
	
	protected SocketChannel currentSocket;

	// The buffer into which we'll read data when it's available
	protected ByteBuffer readBuffer = ByteBuffer.allocate(8192);

	// A list of PendingChange instances
	protected BlockingQueue<ChangeRequest> pendingChanges = new LinkedBlockingQueue<ChangeRequest>();

	// Maps a SocketChannel to a list of ByteBuffer instances
	protected Map<SocketChannel,List<ByteBuffer>> pendingData = new HashMap<SocketChannel,List<ByteBuffer>>();
	
	public SingleConnectionNioClient(InetAddress hostAddress, int port) throws IOException {
		this.hostAddress = hostAddress;
		this.port = port;
		this.selector = this.initSelector();
		this.connected = false;
	}

	public void openConnection() throws IOException {
		// Start a new connection
		SocketChannel socket = this.initiateConnection();
		this.currentSocket = socket;
		
		// Finally, wake up our selecting thread so it can make the required changes
		this.selector.wakeup();
	}
	
	public void closeConnection() throws IOException {
		this.currentSocket.close();
		this.currentSocket.keyFor(this.selector).cancel();
		this.currentSocket = null;
	}
	
	public void sendData(byte[] data) throws IOException {
		if (this.connected) {
			this.pendingChanges.add(new ChangeRequest(this.currentSocket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
			
			synchronized (this.pendingData) {
				List<ByteBuffer> queue = (List<ByteBuffer>) this.pendingData.get(this.currentSocket);
				if (queue == null) {
					queue = new ArrayList<ByteBuffer>();
					this.pendingData.put(this.currentSocket, queue);
				}
				queue.add(ByteBuffer.wrap(data));
				queue.add(ByteBuffer.wrap(",".getBytes()));
				
			}
			
			this.selector.wakeup();
		} else {
			System.out.println("No Connection");
		}
		
	}
	
	public boolean getConnected() {
		return this.connected;
	}
	
	public String getResponse() {
		return this.currentResponse;
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
							break;
						case ChangeRequest.REGISTER:
							change.socket.register(this.selector, change.ops);
							break;
						}
					}
					this.pendingChanges.clear();
				}

				// Wait for an event one of the registered channels
				this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}

					// Check what event is available and deal with it
					if (key.isConnectable()) {
						this.finishConnection(key);
					} else if (key.isReadable()) {
						this.read(key);
					} else if (key.isWritable()) {
						this.write(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(this.readBuffer);
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

		// Handle the response
		this.handleResponse(socketChannel, this.readBuffer.array(), numRead);
	}

	protected abstract void handleResponse(SocketChannel socketChannel, byte[] data, int numRead) throws IOException;

	protected void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingData) {
			List<ByteBuffer> queue = (List<ByteBuffer>) this.pendingData.get(socketChannel);
			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
				socketChannel.write(buf);
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

	protected void finishConnection(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
	
		// Finish the connection. If the ion operation failed
		// this will raise an IOException.
		try {
			socketChannel.finishConnect();
			this.connected = true;
		} catch (IOException e) {
			// Cancel the channel's registration with our selector
			System.out.println("Connection Failed. Putting in local mode.");
			key.cancel();
			return;
		}
	
		// We are now open, we must register an interest in reading what is on the channel
		key.interestOps(SelectionKey.OP_READ);
	}

	protected SocketChannel initiateConnection() throws IOException {
		// Create a non-blocking socket channel
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
	
		// Kick off connection establishment
		socketChannel.connect(new InetSocketAddress(this.hostAddress, this.port));
		//SocketChannelConnectInitiated.newCase()
		//SocketChannelConnectInitiated.newCase(this,socketChannel, new InetSocketAddress(this.hostAddress, this.port));
	
		// Queue a channel registration since the caller is not the 
		// selecting thread. As part of the registration we'll register
		// an interest in connection events. These are raised when a channel
		// is ready to complete connection establishment.
		synchronized(this.pendingChanges) {
			this.pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
		}
		
		return socketChannel;
	}

	protected Selector initSelector() throws IOException {
		// Create a new selector
		return SelectorProvider.provider().openSelector();
	}
	
	
}

