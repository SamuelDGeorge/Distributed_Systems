package three.clients;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import three.util.RemoteOperations;
import StringProcessors.HalloweenCommandProcessor;
import main.BeauAndersonFinalProject;
import port.trace.nio.SocketChannelConnectFinished;
import three.util.ChangeRequest;



public class ABeauNIOClient extends SingleConnectionNioClient implements PropertyChangeListener,BeauNIOClient,Runnable{
	private HalloweenCommandProcessor observingSimulation;
	public static final String SIMULATION1_PREFIX = "1:";
	public static final String SIMULATION2_PREFIX = "2:";
	public static  final int COUPLED_SIMULATION_X_OFFSET = 250;
	public static int SIMULATION_COMMAND_Y_OFFSET = 0;
	public static int SIMULATION_WIDTH = 400;
	public static int SIMULATION_HEIGHT = 765;
	private BlockingQueue<String> queue;
	private String clientMode;
	private boolean source;
	
	public ABeauNIOClient(InetAddress hostAddress, int port) throws IOException {
		super(hostAddress, port);
		
		this.observingSimulation = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, SIMULATION_COMMAND_Y_OFFSET, SIMULATION_WIDTH, SIMULATION_HEIGHT, 0, 0);
		this.observingSimulation.setConnectedToSimulation(false);
		this.observingSimulation.addPropertyChangeListener(this);
		
		queue = new LinkedBlockingQueue<String>();
		Thread selector = new Thread(this);
		selector.setDaemon(true);
		selector.setName("SelectorThread");
		selector.start();
		
		
		Thread remoteOperations = new Thread(new RemoteOperations(this.observingSimulation, this.queue));
		remoteOperations.setName("RemoteOperationsThread");
		remoteOperations.start();
		this.openConnection();
		this.clientMode = "atomic";
		
	}
	
	public ABeauNIOClient(InetAddress hostAddress, int port, HalloweenCommandProcessor sim) throws IOException {
		super(hostAddress, port);
		
		this.observingSimulation = sim;
		this.observingSimulation.setConnectedToSimulation(false);
		
		queue = new LinkedBlockingQueue<String>();
		Thread selector = new Thread(this);
		selector.setDaemon(true);
		selector.setName("SelectorThread");
		selector.start();
		
		
		Thread remoteOperations = new Thread(new RemoteOperations(this.observingSimulation, this.queue));
		remoteOperations.setName("RemoteOperationsThread");
		remoteOperations.start();
		this.openConnection();
		
	}
	
	@Override
	protected void finishConnection(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
	
		// Finish the connection. If the ion operation failed
		// this will raise an IOException.
		try {
			socketChannel.finishConnect();
			SocketChannelConnectFinished.newCase(this,socketChannel);
			this.connected = true;
		} catch (IOException e) {
			// Cancel the channel's registration with our selector
			System.out.println("Connection Failed. Putting in local mode.");
			this.clientMode = "local";
			key.cancel();
			return;
		}
	
		// We are now open, we must register an interest in reading what is on the channel
		key.interestOps(SelectionKey.OP_READ);
	}

	
	protected void handleResponse(SocketChannel socketChannel, byte[] data, int numRead) throws IOException {
		// Make a correctly sized copy of the data before handing it
		// to the client
		byte[] rspData = new byte[numRead];
		System.arraycopy(data, 0, rspData, 0, numRead);
		if (!this.source) {
			this.queue.add(new String(rspData));
		}
		this.source = false;
		this.pendingChanges.add(new ChangeRequest(this.currentSocket, ChangeRequest.CHANGEOPS, SelectionKey.OP_READ));
		this.selector.wakeup();
			
	}
	
	public void doCommand(String command) throws IOException {
		if ("local".equals(this.clientMode)) {
			//do nothing
		} else if ("non-atomic".equals(this.clientMode)){
			this.source = true;
			//do nothing
			this.sendData(command.getBytes());
		} else {
			this.sendData(command.getBytes());
		}	
	}

	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) {return;} 
		String newCommand = (String) anEvent.getNewValue();
		try {
			doCommand(newCommand);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public void setBroadcastMode(String input) {
		if ("local".equals(input)) {
			this.observingSimulation.setConnectedToSimulation(true);
			this.clientMode = input;
		} else if ("non-atomic".equals(input)) {
			this.observingSimulation.setConnectedToSimulation(true);
			this.clientMode = input;
		} else {
			this.observingSimulation.setConnectedToSimulation(false);
			this.clientMode = input;
		}
		
	}


}
