package three.clients;

public interface GlobalClient {
	public void sendCommand(String CommandToSend);
	public void sendMode(String modeForclients);
	public void sendIPCMode(String modeForclients);
	public void setConcensusModeOnForBroadcast(boolean concensusMode);
	public void setConcensusModeOnForMechanism(boolean globalConcensus);
	public void setIPCMode(String broadcastType);
	public void setBroadcastMode(String broadcastMode);
	public void setBroadcastModeForAllMechanisms(String mode);
	public void setWaitingForBroadcastConcensus(boolean waiting);
	public void setWaitingForIPCConcensus(boolean waiting);
	public void setPaxosMode(Integer mode);
}