package three.server;

import java.nio.channels.SocketChannel;
import java.util.List;

public interface BroadcastingNioServer {
	public void sendToAll(List<SocketChannel> sockets, byte[] data);
	public void setSynchronizedReply(boolean toSet);
}
