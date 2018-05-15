package three.server;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import three.clients.GIPCClient;
import three.monitors.ConcensusMonitor;
import three.monitors.MonitorFactory;
import inputport.rpc.duplex.GIPCRemoteException;

public class BasicBeauGIPCServer implements BeauGIPCServer {
	private GIPCRegistry serverRegistry;
	private List<GIPCClient> clientlist;
	private boolean waitingForBroadcastConcensus;
	private boolean waitingForIPCConcensus;
	private boolean synchronizedReply;
	
	public BasicBeauGIPCServer(String serverName) throws GIPCRemoteException {
		this.clientlist = new ArrayList<GIPCClient>();
		this.serverRegistry = GIPCLocateRegistry.createRegistry(REGISTRY_PORT_NAME);	
		this.serverRegistry.rebind(serverName, this);
		this.waitingForBroadcastConcensus = false;
		this.waitingForIPCConcensus = false;
	}
	
	public BasicBeauGIPCServer(String serverName,int port) throws GIPCRemoteException {
		this.clientlist = new ArrayList<GIPCClient>();
		this.serverRegistry = GIPCLocateRegistry.createRegistry(port);	
		this.serverRegistry.rebind(serverName, this);
		this.waitingForBroadcastConcensus = false;
		this.waitingForIPCConcensus = false;
	}

	
	public void join(GIPCClient toAdd) throws GIPCRemoteException {
		this.clientlist.add(toAdd);
		
	}

	
	public void EchoString(String sourceClientName, String toEcho) throws GIPCRemoteException {
			if (this.synchronizedReply) {
				MonitorFactory.getRaceLock().lock(this);
			}
			
			for (int i = 0; i < this.clientlist.size(); i++) {
				GIPCClient current = this.clientlist.get(i);
				current.processCommand(sourceClientName, toEcho);
			}
			if (this.synchronizedReply) {
				MonitorFactory.getRaceLock().unlock();
			}
		
	}

	
	public void setClientModes(String mode) throws GIPCRemoteException {
		System.out.println("Changing Modes");
		for (int i =0; i < this.clientlist.size();i++) {
			GIPCClient currentClient = this.clientlist.get(i);
			currentClient.setClientMode(mode);
		}
		
	}

	public void setClientModesConcensus(String clientName, String mode) throws GIPCRemoteException {
		if (this.waitingForBroadcastConcensus) {
			return;
		}
		this.waitingForBroadcastConcensus = true;
		for (int i =0; i < this.clientlist.size();i++) {
			GIPCClient currentClient = this.clientlist.get(i);
			if (currentClient.getName().equals(clientName)){
				//do nothing
			} else {
				currentClient.setClientModeConcensus(mode);
			}
		}
		for (int i =0; i < this.clientlist.size();i++) {
			GIPCClient currentClient = this.clientlist.get(i);
			currentClient.concensusAchieved();
		}
		this.waitingForBroadcastConcensus = false;
	}

	public void setClientIPCModes(String mode) {
		for (int i =0; i < this.clientlist.size();i++) {
			GIPCClient currentClient = this.clientlist.get(i);
			currentClient.setIPCMode(mode);
		}
		
	}


	@Override
	public void setClientIPCModesConcensus(String clientName, String modeRequest) {
		if(this.waitingForIPCConcensus) {return;}
		this.waitingForIPCConcensus = true;
		for (int i =0; i < this.clientlist.size();i++) {
			GIPCClient currentClient = this.clientlist.get(i);
			if (currentClient.getName().equals(clientName)){
				//do nothing
			} else {
				currentClient.setClientIPCModeConcensus(modeRequest);
			}
		}
		for (int i =0; i < this.clientlist.size();i++) {
			GIPCClient currentClient = this.clientlist.get(i);
			currentClient.concensusAchievedIPC();
		}
		this.waitingForIPCConcensus = false;
		
	}


	public void setSynchronizedReply(boolean toSet) throws RemoteException {
		this.synchronizedReply = toSet;
		
	}
	

}
