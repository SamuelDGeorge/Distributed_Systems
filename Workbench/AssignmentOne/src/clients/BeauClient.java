package clients;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import CommandObjects.RemoteOperations;
import StringProcessors.HalloweenCommandProcessor;
import main.BeauAndersonFinalProject;
import nioextend.ChangeRequest;
import nioextend.SingleConnectionNioClient;
import util.trace.Tracer;


public class BeauClient extends SingleConnectionNioClient implements PropertyChangeListener{
	private HalloweenCommandProcessor observingSimulation;
	public static final String SIMULATION1_PREFIX = "1:";
	public static final String SIMULATION2_PREFIX = "2:";
	public static  final int COUPLED_SIMULATION_X_OFFSET = 250;
	public static int SIMULATION_COMMAND_Y_OFFSET = 0;
	public static int SIMULATION_WIDTH = 400;
	public static int SIMULATION_HEIGHT = 765;
	private BlockingQueue<String> queue;
	private boolean local;
	
	public BeauClient(InetAddress hostAddress, int port) throws IOException {
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
		
	}
	
	@Override
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
			this.local = true;
			key.cancel();
			return;
		}
	
		// We are now open, we must register an interest in reading what is on the channel
		key.interestOps(SelectionKey.OP_READ);
	}
	
	public void setLocalMode(boolean input) {
		if (this.local == false && input) {
			this.local = true;
			try {
				this.closeConnection();
			} catch (IOException e) {
				System.out.println("Already in local mode");
			}
		} else if (this.local == true  && input ) {
			//do nothing
		} else if (this.local == false && input == false) {
			//do nothing
		} else {
			try {
				this.openConnection();
				this.local = false;
			} catch (ConnectException e) {System.out.println("Unable to connect to Server! Staying in local mode");} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	protected void handleResponse(SocketChannel socketChannel, byte[] data, int numRead) throws IOException {
		// Make a correctly sized copy of the data before handing it
		// to the client
		byte[] rspData = new byte[numRead];
		System.arraycopy(data, 0, rspData, 0, numRead);
		this.queue.add(new String(rspData));
		this.pendingChanges.add(new ChangeRequest(this.currentSocket, ChangeRequest.CHANGEOPS, SelectionKey.OP_READ));
		this.selector.wakeup();
			
	}

	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) {return;} 
		String newCommand = (String) anEvent.getNewValue();
		System.out.println("Received command:" + newCommand);
		if (!this.local) {	
			try {
				this.sendData(newCommand.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			this.observingSimulation.processCommand(newCommand);
		}	
	}
	
	

}
