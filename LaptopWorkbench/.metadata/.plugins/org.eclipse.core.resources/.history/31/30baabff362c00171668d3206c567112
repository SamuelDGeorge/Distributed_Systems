package three.clients;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import StringProcessors.AHalloweenCommandProcessor;
import StringProcessors.HalloweenCommandProcessor;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import main.BeauAndersonFinalProject;
import three.monitors.ConcensusMonitor;
import three.monitors.MonitorFactory;
import three.server.BeauGIPCServer;
import three.ui.ABeauController;
import three.ui.BeauController;
import inputport.rpc.duplex.GIPCRemoteException;

public class AGIPCClient implements GIPCClient,PropertyChangeListener {
	private static int SIMULATION_COMMAND_Y_OFFSET = 0;
	private static int SIMULATION_WIDTH = 400;
	private static int SIMULATION_HEIGHT = 765;
	private HalloweenCommandProcessor simulation;
	private boolean local;
	private boolean nonAtomic;
	private boolean atomic;
	private String name;
	private BeauGIPCServer proxyOfServer;
	private BlockingQueue<PropertyChangeEvent> eventQueue;
	private boolean setConcensusMode; 
	private boolean waitingForBroadcastModeConsensus;
	private boolean setConcensusIPCMode;
	private boolean waitingForIPCMechanismConcensus;
	private GlobalClient global;
	private String host;
	private int port;
	
	public AGIPCClient(String clientName) throws GIPCRemoteException, NotBoundException {
		this.simulation = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, SIMULATION_COMMAND_Y_OFFSET, SIMULATION_WIDTH, SIMULATION_HEIGHT, 0, 0);
		this.name = clientName;
		this.host = this.REGISTRY_HOST_NAME;
		this.port = this.REGISTRY_PORT_NAME;
		this.eventQueue = new LinkedBlockingQueue<PropertyChangeEvent>();
		this.simulation.addPropertyChangeListener(this);
		this.setConcensusMode = true;
		this.setModeAtomic();
	}
	
	public AGIPCClient(String clientName, HalloweenCommandProcessor sim) throws RemoteException, NotBoundException {
		this.simulation = sim;
		this.local = false;
		this.nonAtomic = false;
		this.atomic = true;
		this.name = clientName;
		this.setConcensusMode = true;
		this.host = this.REGISTRY_HOST_NAME;
		this.port = this.REGISTRY_PORT_NAME;
	}
	
	public AGIPCClient(String clientName, HalloweenCommandProcessor sim, GlobalClient client) throws RemoteException, NotBoundException {
		this(clientName,sim);
		this.global = client;
	}
	
	public AGIPCClient(String clientName, HalloweenCommandProcessor sim, GlobalClient client,String host, int port) throws RemoteException, NotBoundException {
		this(clientName,sim);
		this.global = client;
		this.host = host;
		this.port = port;
	}
	

	
	public void setModeLocal() throws GIPCRemoteException {
		this.local = true;
		this.nonAtomic = false;
		this.atomic = false;
		this.simulation.setConnectedToSimulation(true);
		if (this.global != null) {
			this.global.setBroadcastModeForAllMechanisms("local");
		}
		System.out.println(this.getName() + ": Local Mode");
	}
	
	public boolean setClientModeConcensus(String mode) throws GIPCRemoteException {
		setClientMode(mode);
		this.simulation.setConnectedToSimulation(false);
		this.waitingForBroadcastModeConsensus = true;
		if (this.global != null) {
			this.global.setWaitingForBroadcastConcensus(true);
		}
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
	
	private void setModeNonAtomic() throws GIPCRemoteException {
		this.local = false;
		this.nonAtomic = true;
		this.atomic = false;
		this.simulation.setConnectedToSimulation(true);
		if (this.global != null) {
			this.global.setBroadcastModeForAllMechanisms("non-atomic");
		}
		System.out.println(this.getName() + ": Non-Atomic Mode");
	}

	
	private boolean setModeAtomic() throws GIPCRemoteException {
		this.local = false;
		this.nonAtomic = false;
		this.atomic = true;
		this.simulation.setConnectedToSimulation(false);
		if (this.global != null) {
			this.global.setBroadcastModeForAllMechanisms("atomic");
		}
		System.out.println(this.getName() + ":  Atomic Mode");
		return true;
		
	}
	
	public void processCommand(String source,String toExecute) throws GIPCRemoteException{
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


	
	public void sendCommand(String toExecute) throws GIPCRemoteException {
		this.proxyOfServer.EchoString(this.getName(), toExecute);
		//System.out.println("Command Sent");
	}

	
	public String getName() {
		return this.name;
	}




	
	public void registerWithServer(String serverName) throws GIPCRemoteException {
		try {
			GIPCRegistry rmiRegistry = GIPCLocateRegistry.getRegistry(this.host, this.port,this.name);
			rmiRegistry.rebind(this.name, this);
			this.proxyOfServer = (BeauGIPCServer) rmiRegistry.lookup(BeauGIPCServer.class,serverName);
			this.proxyOfServer.join(this);
		} catch (Exception e) {
			System.out.println("Unable to Find Server");
			e.printStackTrace();
		}
		
	}

	
	public void sendMode(String mode) throws GIPCRemoteException {
		try {
		if (this.proxyOfServer == null) {
			setModeLocal();
		} else {
			this.proxyOfServer.setClientModes(mode);
		} 
			//do nothing
		} catch (GIPCRemoteException e) {
			e.printStackTrace();
		}
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (this.setConcensusMode && this.waitingForBroadcastModeConsensus) {
			//System.out.println("Event Ignored: " + evt);
		} else {
			try {
				if (evt.getPropertyName().equals("InputString")){
				processCommand(evt);
				} else if (evt.getPropertyName().equals("ChangeMode")) {
					processChange(evt);
				} else {}
			} catch (GIPCRemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private void processCommand(PropertyChangeEvent evt) throws GIPCRemoteException{
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
				this.sendCommand(newValue);
			} else {
				this.sendCommand(newValue);
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
				this.sendCommand(newValue);
			}
		} catch (GIPCRemoteException e) {
			e.printStackTrace();
		}
		
	}

	private void processChange(PropertyChangeEvent evt) throws GIPCRemoteException{
		if(this.setConcensusMode) {
			MonitorFactory.getBroadcastModeConcensusMonitor().setconcensusAchieved(false);
			this.sendModeConcensus(this.name,(String)evt.getNewValue());
		} else {
			this.sendMode((String)evt.getNewValue());	
		}
			
	}
		

	public void sendModeConcensus(String clientName, String modeRequest) throws GIPCRemoteException {
		this.setClientMode(modeRequest);
		this.waitingForBroadcastModeConsensus = true;
		if (this.global != null) {
			this.global.setWaitingForBroadcastConcensus(true);
		}
		this.simulation.setConnectedToSimulation(false);
		this.proxyOfServer.setClientModesConcensus(clientName, modeRequest);
		//System.out.println(this.getName() + ": Waiting for Concensus");
	}

	public void setConcensusMode(boolean set) {
		this.setConcensusMode = set;
	}
	
	public void setConcensusIPCMode(boolean set) {
		this.setConcensusIPCMode = set;
	}

	public void concensusAchieved() throws GIPCRemoteException {
		this.waitingForBroadcastModeConsensus = false;
		if(this.local || this.nonAtomic) {
			this.simulation.setConnectedToSimulation(true);
		}
		if (this.global != null) {
			this.global.setWaitingForBroadcastConcensus(false);
		}
		//System.out.println(this.getName() + ": Concensus Achieved");
		MonitorFactory.getBroadcastModeConcensusMonitor().setconcensusAchieved(true);
	}
	
	public void concensusAchievedIPC() throws GIPCRemoteException {
		this.waitingForIPCMechanismConcensus = false;
		if(this.local || this.nonAtomic) {
			this.simulation.setConnectedToSimulation(true);
		}
		if (this.global != null) {
			this.global.setWaitingForIPCConcensus(false);
		}
		//System.out.println(this.getName() + ": IPC Concensus Achieved");
		MonitorFactory.getIPCConcensusMonitor().setconcensusAchieved(true);
	}

	public void sendIPCMode(String mode) throws GIPCRemoteException {
		try {
			if (this.proxyOfServer == null) {
				this.setIPCGIPC();
			} else {
				this.proxyOfServer.setClientIPCModes(mode);
			} 
				//do nothing
			} catch (GIPCRemoteException e) {
				e.printStackTrace();
			}
		
	}

	public void setIPCMode(String mode) throws GIPCRemoteException {
		if ("nio".equals(mode)) {
			this.setIPCNIO();
		} else if ("rmi".equals(mode)) {
			this.setIPCRMI();
		} else if ("gipc".equals(mode)) {
			this.setIPCGIPC();
		}
		
	}

	private void setIPCGIPC() {
		if(this.global != null) {
			this.global.setIPCMode("gipc");
		}
		
	}

	private void setIPCRMI() {
		if(this.global != null) {
			this.global.setIPCMode("rmi");
		}
		
		
	}

	private void setIPCNIO() {
		if(this.global != null) {
			this.global.setIPCMode("nio");
		}
		
		
	}

	@Override
	public void sendIPCModeConcensus(String clientName, String modeRequest) throws GIPCRemoteException {
		MonitorFactory.getIPCConcensusMonitor().setconcensusAchieved(false);
		this.setIPCMode(modeRequest);
		this.waitingForIPCMechanismConcensus = true;
		this.simulation.setConnectedToSimulation(false);
		if (this.global != null) {
			this.global.setWaitingForIPCConcensus(true);
		}
		this.proxyOfServer.setClientIPCModesConcensus(clientName, modeRequest);
		//System.out.println(this.getName() + ": Waiting for Concensus");
		
	}

	@Override
	public boolean setClientIPCModeConcensus(String mode) throws GIPCRemoteException {
		this.setIPCMode(mode);
		this.waitingForIPCMechanismConcensus = true;
		if (this.global != null) {
			this.global.setWaitingForIPCConcensus(true);
		}
		//System.out.println(this.getName() + ": Waiting for Concensus");
		return true;
	}




}
