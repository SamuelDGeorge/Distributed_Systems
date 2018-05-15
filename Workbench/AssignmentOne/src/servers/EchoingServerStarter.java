package servers;

import java.io.IOException;
import java.net.InetAddress;
import nioextend.BroadcastingNioServer;

public class EchoingServerStarter {
	private InetAddress address;
	private int port;
	
	public EchoingServerStarter(InetAddress hostAddress, int port) {
		this.address = hostAddress;
		this.port = port;
	}
	
	public void startServer() throws IOException {
		System.out.println("Server Starting");
		BroadcastingNioServer server = new BroadcastingNioServer(this.address,this.port);
		Thread serverThread = new Thread(server);
		serverThread.setName("ServerThread");
		serverThread.start();
		System.out.println("Server Started");
	}
	
}
