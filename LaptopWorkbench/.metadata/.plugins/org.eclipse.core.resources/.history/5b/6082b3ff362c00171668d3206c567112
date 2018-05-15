package three.clients;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import StringProcessors.AHalloweenCommandProcessor;
import StringProcessors.HalloweenCommandProcessor;
import inputport.rpc.duplex.GIPCRemoteException;
import main.BeauAndersonFinalProject;
import three.monitors.MonitorFactory;
import three.server.BeauRMIServer;
import three.ui.ABeauController;

public class ARMIClient implements RMIClient,PropertyChangeListener {
	private static int SIMULATION_COMMAND_Y_OFFSET = 0;
	private static int SIMULATION_WIDTH = 400;
	private static int SIMULATION_HEIGHT = 765;
	private HalloweenCommandProcessor simulation;
	private boolean local;
	private boolean nonAtomic;
	private boolean atomic;
	private String name;
	private BeauRMIServer proxyOfServer;
	private boolean waitingForBroadcastModeConsensus;
	private boolean setConcensusMode;
	private String host;
	private int port;
	private Registry clientRegistry;
	
	public ARMIClient(String clientName) throws RemoteException, NotBoundException {
		this.simulation = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, SIMULATION_COMMAND_Y_OFFSET, SIMULATION_WIDTH, SIMULATION_HEIGHT, 0, 0);
		this.local = true;
		this.nonAtomic = false;
		this.atomic = false;
		this.name = clientName;
		this.simulation.addPropertyChangeListener(this);
	}
	
	public ARMIClient(String clientName, HalloweenCommandProcessor sim) throws RemoteException, NotBoundException {
		this.simulation = sim;
		this.local = false;
		this.nonAtomic = false;
		this.atomic = true;
		this.name = clientName;
	}
	
	public ARMIClient(String clientName, HalloweenCommandProcessor sim, String host, int port) throws RemoteException, NotBoundException {
		this.simulation = sim;
		this.local = false;
		this.nonAtomic = false;
		this.atomic = true;
		this.name = clientName;
		this.host = host;
		this.port = port;
		this.clientRegistry = LocateRegistry.createRegistry(4998);	
		UnicastRemoteObject.exportObject(this, 0);
		this.clientRegistry.rebind(this.name, this);
	}
	
	

	
	public void setModeLocal(){
		this.local = true;
		this.nonAtomic = false;
		this.atomic = false;
		this.simulation.setConnectedToSimulation(true);
		//return true;
	}
	
	public boolean setClientModeConcensus(String mode) throws RemoteException {
		setClientMode(mode);
		this.waitingForBroadcastModeConsensus = true;
		System.out.println(this.getName() + ": Waiting for Concensus");
		return true;
	}

	public void setClientMode(String mode) {
		if ("local".equals(mode)) {
			this.setModeLocal();
		} else if ("non-atomic".equals(mode)) {
			this.setModeNonAtomic();
		} else if ("atomic".equals(mode)) {
			this.setModeAtomic();
		}
	}
	
	private void setModeNonAtomic() {
		this.local = false;
		this.nonAtomic = true;
		this.atomic = false;
		this.simulation.setConnectedToSimulation(true);
		//return true;
	}

	
	private boolean setModeAtomic(){
		this.local = false;
		this.nonAtomic = false;
		this.atomic = true;
		this.simulation.setConnectedToSimulation(false);
		return true;
		
	}
	
	public void processCommand(String source,String toExecute) throws RemoteException{
		if (this.local) {
			//ignore
		} else if (this.nonAtomic) {
			if(this.name.equals(source)) {
				//do nothing
			} else {
				this.simulation.processCommand(toExecute);
			}
		} else {
			this.simulation.processCommand(toExecute);
		}
	}


	
	public void sendCommand(String toExecute) throws RemoteException {
		try {
			this.proxyOfServer.EchoString(this.getName(), toExecute);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	
	public String getName() {
		return this.name;
	}




	
	public void registerWithServer(String serverName) throws RemoteException {
		Registry server = LocateRegistry.getRegistry(this.host, this.port);
		try {
			this.proxyOfServer = (BeauRMIServer) server.lookup(serverName);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.proxyOfServer.join(this.name,"128.196.64.186");
		
		/*Registry registry = LocateRegistry.getRegistry("localhost", 4998);
		try {
			this.proxyOfServer = (BeauRMIServer) registry.lookup(serverName);
			UnicastRemoteObject.exportObject(this,0);
			registry.rebind(this.name, this);
			this.proxyOfServer.join(this);
		} catch (NotBoundException e) {
		}*/
			
			
		
	}

	
	public void sendMode(String mode) throws RemoteException {
		try {
		if (this.proxyOfServer == null) {
			setModeLocal();
		} else {
			try {
				this.proxyOfServer.setClientModes(mode);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} 
			//do nothing
		} catch (GIPCRemoteException e) {
			e.printStackTrace();
		}
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (this.setConcensusMode && this.waitingForBroadcastModeConsensus) {
			//do nothing
			//ignore all commands
		} else {
			try {
				if (evt.getPropertyName().equals("InputString")){
				try {
					processCommand(evt);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				} else if (evt.getPropertyName().equals("ChangeMode")) {
					try {
						processChange(evt);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} else {}
			} catch (GIPCRemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private void processCommand(PropertyChangeEvent evt) throws RemoteException{
		if (evt.getSource() instanceof AHalloweenCommandProcessor || evt.getSource() instanceof GlobalClient) {
			processHalloween((String)evt.getNewValue());
		} else if (evt.getSource() instanceof ABeauController) {
			processController((String)evt.getNewValue());
		} else {
			//do nothing
		}
		
	}

	private void processController(String newValue) {
		try {
			if (this.local) {
				this.simulation.processCommand(newValue);
			} else if (this.nonAtomic) {
				this.simulation.processCommand(newValue);
				try {
					this.sendCommand(newValue);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else {
				try {
					this.sendCommand(newValue);
				} catch (RemoteException e) {

					e.printStackTrace();
				}
			}
		} catch (GIPCRemoteException e) {
			e.printStackTrace();
		}
		
	}

	private void processHalloween(String newValue) {
		try {
			if (this.local) {
				//do nothing
			}else {
				try {
					this.sendCommand(newValue);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} catch (GIPCRemoteException e) {
			e.printStackTrace();
		}
		
	}

	private void processChange(PropertyChangeEvent evt) throws RemoteException{
		if(this.setConcensusMode) {
			this.sendModeConcensus(this.name,(String)evt.getNewValue());
		} else {
			try {
				this.sendMode((String)evt.getNewValue());
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}
			
	}
		

	public void sendModeConcensus(String clientName, String modeRequest) throws RemoteException {
		try {
			this.proxyOfServer.setClientModesConcensus(clientName, modeRequest);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.setClientMode(modeRequest);
		this.waitingForBroadcastModeConsensus = true;
		this.simulation.setConnectedToSimulation(false);
		MonitorFactory.getBroadcastModeConcensusMonitor().setconcensusAchieved(false);
		System.out.println(this.getName() + ": Waiting for Concensus");
	}

	public void setConcensusModeOn(boolean set) throws RemoteException {
		this.setConcensusMode = set;
	}

	public void concensusAchieved() throws RemoteException {
		
		this.waitingForBroadcastModeConsensus = false;
		if(this.local || this.nonAtomic) {
			this.simulation.setConnectedToSimulation(true);
		}
		System.out.println(this.getName() + ": Concensus Achieved");
		MonitorFactory.getBroadcastModeConcensusMonitor().setconcensusAchieved(true);
	}



}
