package three.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import inputport.rpc.duplex.GIPCRemoteException;
import three.clients.GIPCClient;

public interface BeauGIPCServer extends Remote{
	static int REGISTRY_PORT_NAME = 4999;
	public void join(GIPCClient toAdd) throws GIPCRemoteException;
	public void EchoString(String sourceClientName,String toEcho) throws GIPCRemoteException;
	public void setClientModes(String mode) throws GIPCRemoteException;
	public void setClientModesConcensus(String clientName, String mode) throws GIPCRemoteException;
	public void setClientIPCModes(String mode);
	public void setClientIPCModesConcensus(String clientName, String modeRequest);
	public void setSynchronizedReply(boolean toSet) throws RemoteException;
}
