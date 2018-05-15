package three.server;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import inputport.rpc.duplex.GIPCRemoteException;
import three.clients.GIPCClient;
import three.clients.RMIClient;
import three.monitors.MonitorFactory;


public class BasicBeauRMIServer implements BeauRMIServer {
	private Registry serverRegistry;
	private List<RMIClient> clientlist;
	private boolean synchronizedReply;
	
	public BasicBeauRMIServer(String serverName, int port) {
		this.clientlist = new ArrayList<RMIClient>();
		try {
			this.serverRegistry = LocateRegistry.createRegistry(port);	
			UnicastRemoteObject.exportObject(this, 0);
			this.serverRegistry.rebind(serverName, this);
		} catch (RemoteException e) {
			System.out.println("Failed to create server!");
			e.printStackTrace();
		}

	}

	
	public void join(String clientName, String clientAddress) throws RemoteException {
		Registry clientRegistry = LocateRegistry.getRegistry(clientAddress, 4998);
		try {
			RMIClient client = (RMIClient) clientRegistry.lookup(clientName);

			this.clientlist.add(client);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//this.clientlist.add(e);
	}

	
	public void EchoString(String sourceClientName, String toEcho) throws RemoteException {
		if (this.synchronizedReply) {
			MonitorFactory.getRaceLock().lock(this);
		}
		for (int i = 0; i < this.clientlist.size(); i++) {
				RMIClient current = this.clientlist.get(i);
				try {
					current.processCommand(sourceClientName, toEcho);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if (this.synchronizedReply) {
			MonitorFactory.getRaceLock().unlock();
		}
		
		
	}

	
	public void setClientModes(String mode) throws RemoteException {
		for (int i =0; i < this.clientlist.size();i++) {
			RMIClient currentClient = this.clientlist.get(i);
			currentClient.setClientMode(mode);
		}
		
	}

	public void setClientModesConcensus(String clientName, String mode) throws RemoteException {
		for (int i =0; i < this.clientlist.size();i++) {
			RMIClient currentClient = this.clientlist.get(i);
			if (currentClient.getName().equals(clientName)){
				//do nothing
			} else {
				currentClient.setClientModeConcensus(mode);
			}
		}
		for (int i =0; i < this.clientlist.size();i++) {
			RMIClient currentClient = this.clientlist.get(i);
			currentClient.concensusAchieved();
		}
	}

	public void setSynchronizedReply(boolean toSet) throws RemoteException{
		this.synchronizedReply = toSet;
		
	}

	
	
	
}
