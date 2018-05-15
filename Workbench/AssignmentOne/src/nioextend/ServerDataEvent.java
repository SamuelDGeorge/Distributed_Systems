package nioextend;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
	public BroadcastingNioServer server;
	public SocketChannel socket;
	public byte[] data;
	
	public ServerDataEvent(BroadcastingNioServer server, SocketChannel socket, byte[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}
}
