package three.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import inputport.datacomm.simplex.buffer.nio.AScatterGatherSelectionManager;

public class AGlobalServer implements GlobalServer {
	private BeauGIPCServer gipc;
	private BeauRMIServer rmi;
	private BroadcastingNioServer nio;
	private String serveraddress;
	
	public AGlobalServer() throws UnknownHostException, IOException {
		this.gipc = new BasicBeauGIPCServer("server");
		this.rmi = new BasicBeauRMIServer("server",4998);
		startNioServer(9090);
		System.out.println(InetAddress.getByName("localhost"));
	}
	
	public AGlobalServer(String name) {
		this.gipc = new BasicBeauGIPCServer(name,4999);
		this.rmi = new BasicBeauRMIServer(name,4998);
		
		try {
			startNioServer(9091);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public AGlobalServer(String name,int nioport, int rmiport, int gipcport) {
		this.gipc = new BasicBeauGIPCServer(name,gipcport);
		this.rmi = new BasicBeauRMIServer(name,rmiport);
		
		try {
			startNioServer(nioport);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startNioServer(int port) throws IOException {
		this.nio = new ABroadcastingNioServer(port);
		Thread serverThread = new Thread( (Runnable) this.nio);
		serverThread.setName("NIOServerThread");
		serverThread.start();
	}

	public void setSynchronizedResponse(boolean value) {
		try {
			this.gipc.setSynchronizedReply(value);
			this.rmi.setSynchronizedReply(value);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.nio.setSynchronizedReply(value);
		
	}
	
	
}
