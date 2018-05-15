package three.clients;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

import inputport.rpc.duplex.GIPCRemoteException;

public interface RMIClient extends Remote{
	static String REGISTRY_HOST_NAME = "localhost";
	static int REGISTRY_PORT_NAME = 4998;
	public void processCommand(String Source,String toExecute) throws RemoteException;
	public void sendCommand(String toExecute) throws RemoteException;
	public void setClientMode(String mode) throws RemoteException;
	public boolean setClientModeConcensus(String mode) throws RemoteException;
	public String getName() throws RemoteException;
	public void registerWithServer(String serverName) throws RemoteException;
	public void sendMode(String mode) throws RemoteException;
	public void setConcensusModeOn(boolean set) throws RemoteException;
	public void concensusAchieved() throws RemoteException;
}
