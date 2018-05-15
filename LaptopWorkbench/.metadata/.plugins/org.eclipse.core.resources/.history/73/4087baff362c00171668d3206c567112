package three.clients;
import java.rmi.Remote;
import java.rmi.RemoteException;

import inputport.rpc.duplex.GIPCRemoteException;

public interface GIPCClient extends Remote{
	static String REGISTRY_HOST_NAME = "localhost";
	static int REGISTRY_PORT_NAME = 4999;
	public void processCommand(String Source,String toExecute) throws GIPCRemoteException;
	public void sendCommand(String toExecute) throws GIPCRemoteException;
	
	public void setClientMode(String mode) throws GIPCRemoteException;
	public void setIPCMode(String mode) throws GIPCRemoteException;
	
	public boolean setClientModeConcensus(String mode) throws GIPCRemoteException;
	public boolean setClientIPCModeConcensus(String mode) throws GIPCRemoteException;
	
	public String getName() throws GIPCRemoteException;
	public void registerWithServer(String serverName) throws GIPCRemoteException;
	public void sendMode(String mode) throws GIPCRemoteException;
	public void sendModeConcensus(String clientName, String modeRequest) throws GIPCRemoteException;
	
	public void sendIPCMode(String mode) throws GIPCRemoteException;
	public void sendIPCModeConcensus(String clientName, String modeRequest) throws GIPCRemoteException;
	
	public void setConcensusMode(boolean set);
	public void setConcensusIPCMode(boolean set);
	public void concensusAchieved() throws GIPCRemoteException;
	public void concensusAchievedIPC() throws GIPCRemoteException;
}
