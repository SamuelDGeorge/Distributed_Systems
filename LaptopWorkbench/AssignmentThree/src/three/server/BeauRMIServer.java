package three.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import inputport.rpc.duplex.GIPCRemoteException;
import three.clients.GIPCClient;
import three.clients.RMIClient;

public interface BeauRMIServer extends Remote{
	static int REGISTRY_PORT_NAME = 4998;
	public void join(String name, String address) throws RemoteException;
	public void EchoString(String sourceClientName,String toEcho) throws RemoteException;
	public void setClientModes(String mode) throws RemoteException;
	public void setClientModesConcensus(String clientName, String mode) throws RemoteException;
	public void setSynchronizedReply(boolean toSet) throws RemoteException;
}
