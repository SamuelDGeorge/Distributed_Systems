package three.util;

import java.nio.channels.SocketChannel;

import three.server.ABroadcastingNioServer;

public class ServerDataEvent {
	public ABroadcastingNioServer server;
	public SocketChannel socket;
	public byte[] data;
	
	public ServerDataEvent(ABroadcastingNioServer server, SocketChannel socket, byte[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}
}
