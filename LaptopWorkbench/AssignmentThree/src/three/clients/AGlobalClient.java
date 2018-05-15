package three.clients;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import StringProcessors.HalloweenCommandProcessor;
import main.BeauAndersonFinalProject;
import three.util.APropertyListenerSupport;
import three.util.PropertyListenerRegisterer;
import three.util.PropertyListenerSupport;
import util.annotations.EditablePropertyNames;

public class AGlobalClient implements PropertyChangeListener,GlobalClient{
	private static int SIMULATION_COMMAND_Y_OFFSET = 0;
	private static int SIMULATION_WIDTH = 400;
	private static int SIMULATION_HEIGHT = 765;
	private HalloweenCommandProcessor simulation;
	private String name;
	private BeauNIOClient nioClient;
	private RMIClient rmiClient;
	private GIPCClient gipcClient;
	private String ipcMode;
	private String broadcastMode;
	private boolean concensusModeMechanism;
	private boolean concensusModeClient;
	private boolean waitingForBroadcastConcensus;
	private boolean waitingForIPCConcensus;
	
	public AGlobalClient(String name) throws UnknownHostException, IOException, NotBoundException {
		this.name = name;
		this.concensusModeMechanism = false;
		this.concensusModeClient = false;
		this.simulation = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, SIMULATION_COMMAND_Y_OFFSET, SIMULATION_WIDTH, SIMULATION_HEIGHT, 0, 0);
		this.simulation.addPropertyChangeListener(this);
		this.nioClient = new ABeauNIOClient(InetAddress.getByName("localhost"),9090,this.simulation);
		this.rmiClient = new ARMIClient(name,this.simulation);
		this.rmiClient.registerWithServer("server");
		this.gipcClient = new AGIPCClient(name,this.simulation,this);
		this.gipcClient.registerWithServer("server");
		this.setIPCMode("gipc");
		this.setBroadcastMode("atomic");
		this.waitingForBroadcastConcensus = false;
		this.waitingForIPCConcensus = false;
		
	}
	
	public AGlobalClient(String name, String address,int nioport, int rmiport, int gipcport) throws UnknownHostException, IOException, NotBoundException {
		this.name = name;
		this.concensusModeMechanism = false;
		this.concensusModeClient = false;
		this.simulation = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, SIMULATION_COMMAND_Y_OFFSET, SIMULATION_WIDTH, SIMULATION_HEIGHT, 0, 0);
		this.simulation.addPropertyChangeListener(this);
		this.nioClient = new ABeauNIOClient(InetAddress.getByName(address),nioport,this.simulation);
		this.rmiClient = new ARMIClient(name,this.simulation,address, rmiport);
		this.rmiClient.registerWithServer("server");
		this.gipcClient = new AGIPCClient(name,this.simulation,this,address,gipcport);
		this.gipcClient.registerWithServer("server");
		this.setIPCMode("gipc");
		this.setBroadcastMode("atomic");
		this.waitingForBroadcastConcensus = false;
		this.waitingForIPCConcensus = false;
		
	}

	public void sendCommand(String CommandToSend) {
		if ("local".equals(this.broadcastMode) || "non-atomic".equals(this.broadcastMode)) {
			this.simulation.setInputString(CommandToSend);
		}
		this.propertyChange(new PropertyChangeEvent(this,"InputString",null,CommandToSend));
	}

	public void sendMode(String modeForclients) {
		((PropertyChangeListener) this.gipcClient).propertyChange(new PropertyChangeEvent(this,"ChangeMode",null,modeForclients));
		
	}
	
	public void sendIPCMode(String modeForclients) {
		if (this.concensusModeMechanism) {
			this.gipcClient.sendIPCModeConcensus(this.name, modeForclients);
		} else {
			this.gipcClient.sendIPCMode(modeForclients);
		}
	}
	
	public void setBroadcastModeForAllMechanisms(String mode) {
		this.setBroadcastMode(mode);
		try {
			this.rmiClient.setClientMode(mode);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			this.nioClient.setBroadcastMode(mode);
		
	}

	public void setConcensusModeOnForBroadcast(boolean concensusMode) {
		this.concensusModeClient = concensusMode;
		this.gipcClient.setConcensusMode(concensusMode);
	}

	public void setConcensusModeOnForMechanism(boolean globalConcensus) {
		this.concensusModeMechanism = globalConcensus;
		this.gipcClient.setConcensusIPCMode(globalConcensus);
		
	}

	public void setIPCMode(String broadcastType) {
		if ("nio".equals(broadcastType)) {
			this.ipcMode = broadcastType;
		} else if ("rmi".equals(broadcastType)) {
			this.ipcMode = broadcastType;
		} else if ("gipc".equals(broadcastType)) {
			this.ipcMode = broadcastType;
		} else {
			System.out.println("Unknown Mode");
		}
	
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (this.waitingForBroadcastConcensus || this.waitingForIPCConcensus) {
			//System.out.println("In Waiting mode, event ignored: " + evt);
			return;}
		if("nio".equals(this.ipcMode)) {
			((PropertyChangeListener) this.nioClient).propertyChange(evt);
		} else if ("rmi".equals(this.ipcMode)) {
			((PropertyChangeListener) this.rmiClient).propertyChange(evt);
		} else if ("gipc".equals(this.ipcMode)) {
			((PropertyChangeListener) this.gipcClient).propertyChange(evt);
		}
	}

	public void setWaitingForBroadcastConcensus(boolean waiting) {
		this.waitingForBroadcastConcensus = waiting;
		
	}

	public void setWaitingForIPCConcensus(boolean waiting) {
		this.waitingForIPCConcensus = waiting;
		
	}

	public void setBroadcastMode(String broadcastMode) {
		this.broadcastMode = broadcastMode;
		
	}

	@Override
	public void setPaxosMode(Integer mode) {
		// TODO Auto-generated method stub
		
	}


}
