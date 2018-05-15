package six.globalclient;

import StringProcessors.HalloweenCommandProcessor;
import consensus.ConsensusListener;
import consensus.ProposalState;

public class ASimulationModeListener implements ConsensusListener<Integer> {
	PaxosClient client;
	
	
	ASimulationModeListener(PaxosClient toManipulate) {
		this.client = toManipulate;
		
	}

	@Override
	public void newLocalProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newRemoteProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newConsensusState(Integer aState) {
		if (aState == 1) {
			this.client.simulateNonAtomicAsynchronous();
			System.out.println("Paxos Mode Changed to: " + "NonAtomicAsynchronous");
		} else if (aState == 2) {
			this.client.simulateNonAtomicSynchronous();
			System.out.println("Paxos Mode Changed to: " + "NonAtomicSynchronous");
		} else if (aState == 3) {
			this.client.simulateCentralizedAsynchronous();
			System.out.println("Paxos Mode Changed to: " + "CentralizedAsynchronous");
		} else if (aState ==4) {
			this.client.simulateCentralizedSynchronous();
			System.out.println("Paxos Mode Changed to: " + "Centralizedsynchronous");
		} else if (aState == 5) {
			this.client.simulateSequentialPaxos();
			System.out.println("Paxos Mode Changed to: " + "SequentialPaxos");
		} else {
			System.out.println("Invalid State Proposed: " + aState);
		}
		
	}
	

	


}
