package three.clients;
import java.rmi.Remote;
import java.rmi.RemoteException;

import inputport.rpc.duplex.GIPCRemoteException;

public interface Client extends Remote{
	static String REGISTRY_HOST_NAME = "localhost";
	static int REGISTRY_PORT_NAME = 4999;
	public void processCommand(String Source,String toExecute) throws GIPCRemoteException;
	public void sendCommand(String toExecute) throws GIPCRemoteException;
	public void setModeLocal() throws GIPCRemoteException;
	public void setModeNonAtomic() throws GIPCRemoteException;
	public void setModeAtomic() throws GIPCRemoteException;
	public String getName() throws GIPCRemoteException;
	public void registerWithServer(String serverName) throws GIPCRemoteException;
	public void sendMode(String mode) throws GIPCRemoteException;
	public void concensusAchieved() throws GIPCRemoteException;
	public void setConcensusMode(boolean mode);
	public void putInWaitMode() throws GIPCRemoteException;
	public boolean isLocal();
	public boolean isNonAtomic();
}
