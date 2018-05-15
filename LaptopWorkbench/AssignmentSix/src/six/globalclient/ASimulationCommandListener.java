package six.globalclient;

import StringProcessors.HalloweenCommandProcessor;
import consensus.ConsensusListener;
import consensus.ProposalState;
import three.clients.GlobalClient;

public class ASimulationCommandListener implements ConsensusListener<String> {
	private HalloweenCommandProcessor simulation;
	
	
	ASimulationCommandListener(HalloweenCommandProcessor sim) {
		this.simulation = sim;
	}
	
	@Override
	public void newLocalProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newRemoteProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newConsensusState(String aState) {
		//System.out.println("Command Recieved: " + aState);
		this.simulation.processCommand(aState);
		
	}

}
