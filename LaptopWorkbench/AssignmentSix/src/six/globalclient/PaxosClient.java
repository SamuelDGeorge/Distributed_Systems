package six.globalclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import StringProcessors.HalloweenCommandProcessor;
import consensus.ConcurrencyKind;
import consensus.ConsensusMechanism;
import consensus.ConsensusMechanismFactory;
import consensus.ProposalState;
import consensus.ReplicationSynchrony;
import consensus.asynchronous.sequential.AnAsynchronousConsensusMechanismFactory;
import consensus.paxos.sequential.ASequentialPaxosConsensusMechanismFactory;
import consensus.sessionport.AConsensusMemberLauncher;
import examples.gipc.consensus.ExampleMember;
import examples.gipc.consensus.paxos.APaxosMemberLauncher;
import three.clients.GlobalClient;
import util.misc.ThreadSupport;

public class PaxosClient extends AConsensusMemberLauncher implements SimulationMember,PropertyChangeListener {
	public static long INIT_TIME = 6000;
	public static long RE_PROPOSE_TIME = 10000;
	protected  ConsensusMechanism<Integer> modeMechanism;
	protected  ConsensusMechanism<String> commandMechanism;
	protected HalloweenCommandProcessor simulation;
	protected boolean overrideRetry;
	
	public PaxosClient(String aLocalName, int aPortNumber, HalloweenCommandProcessor sim) {
		super(aLocalName, aPortNumber);
		this.simulation = sim;
		addListenersAndVetoersToConsensusMechanisms();
		this.simulateCentralizedAsynchronous();
	}
	
	@Override
	protected void initConsensusMechanisms(short anId) {
		setFactories();
		createConsensusMechanisms(anId);
		customizeConsensusMechanisms();
		setThreads();
	}

	public void proposeCommand(String aValue) {
		while (true) {
			if (commandMechanism.someProposalIsPending()) {
				//System.out.println("Waiting for pending proposal");
				commandMechanism.waitForConsensus(commandMechanism
						.lastProposalNumber());
			}
			//System.out.println("Making proposal of:" + aValue);
			float aMeaningOfLifeProposal = commandMechanism.propose(aValue);
			ProposalState aState = commandMechanism.waitForConsensus(
					aMeaningOfLifeProposal, reProposeTime());
			if (aState == null) {
				System.out.println("timed out waiting for proposal:"
						+ aMeaningOfLifeProposal);
			} 
			if (!retry(aState)) {
				break;
			}
			if (aState != null) { // did not time our
				ThreadSupport.sleep(reProposeTime());
			}
			System.out.println("Retrying proposal");
		}
	}
	
	public void proposeMode(Integer aValue) {
		while (true) {
			if (modeMechanism.someProposalIsPending()) {
				System.out.println("Waiting for pending proposal");
				modeMechanism.waitForConsensus(modeMechanism
						.lastProposalNumber());
			}
			//System.out.println("Making proposal of:" + aValue);
			float aMeaningOfLifeProposal = modeMechanism.propose(aValue);
			ProposalState aState = modeMechanism.waitForConsensus(
					aMeaningOfLifeProposal, reProposeTime());
			if (aState == null) {
				System.out.println("timed out waiting for proposal:"
						+ aMeaningOfLifeProposal);
			} 
			if (!retry(aState)) {
				break;
			}
			if (aState != null) { // did not time our
				ThreadSupport.sleep(reProposeTime());
			}
			System.out.println("Retrying proposal");
		}
	}
	
	@Override
	protected short numMembersToWaitFor() {
		return 3;
	}
	
	protected ConsensusMechanismFactory<Integer> modeConsensusMechanismFactory() {
		return new ASequentialPaxosConsensusMechanismFactory();
	}
	
	protected  ConsensusMechanismFactory<String> commandConsensusMechanismFactory() {
		return new ASequentialPaxosConsensusMechanismFactory();
	}
	
	protected void createCommandConsensusMechanism() {
		modeMechanism = modeConsensusMechanismFactory().createConsensusMechanism(
				SESSION_MANAGER_HOST,
				EXAMPLE_SESSION,
				memberId, portNumber, 
				MODE_CONSENSUS_MECHANISM_NAME, 
				sessionChoice, 
				numMembersToWaitFor());
	}
	protected void createModeConsensusMechanism() {
		commandMechanism = commandConsensusMechanismFactory().createConsensusMechanism(
				EXAMPLE_SESSION, memberId, COMMAND_CONSENSUS_MECHANISM_NAME);
	}

	@Override
	protected void createConsensusMechanisms(short anId) {
		createCommandConsensusMechanism();
		createModeConsensusMechanism();
		
	}

	@Override
	protected void addListenersAndVetoersToConsensusMechanisms() {
		this.commandMechanism.addConsensusListener(new ASimulationCommandListener(this.simulation));
		this.modeMechanism.addConsensusListener(new ASimulationModeListener(this));
	}
	
	protected void simulateNonAtomicAsynchronous() {
		commandMechanism.setAcceptSynchrony(ReplicationSynchrony.ASYNCHRONOUS);
		commandMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
		modeMechanism.setAcceptSynchrony(ReplicationSynchrony.ASYNCHRONOUS);
		modeMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
	}
	protected void simulateNonAtomicSynchronous() {
		commandMechanism.setAcceptSynchrony(ReplicationSynchrony.ALL_SYNCHRONOUS);
		commandMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
		modeMechanism.setAcceptSynchrony(ReplicationSynchrony.ALL_SYNCHRONOUS);
		modeMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
	}
	protected void simulateCentralized() {
		commandMechanism.setCentralized(true);
		modeMechanism.setCentralized(true);
	}
	protected void simulateCentralizedSynchronous() {
		simulateNonAtomicSynchronous();
		simulateCentralized();
	}
	protected void simulateCentralizedAsynchronous() {
		simulateNonAtomicAsynchronous();
		simulateCentralized();
	}
	protected void simulateBasicPaxos() {
		overrideRetry = true;
		commandMechanism.setCentralized(false);
		commandMechanism.setConcurrencyKind(ConcurrencyKind.SERIALIZABLE);
		commandMechanism
				.setPrepareSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		commandMechanism
				.setAcceptSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		
		modeMechanism.setCentralized(false);
		modeMechanism.setConcurrencyKind(ConcurrencyKind.SERIALIZABLE);
		modeMechanism
				.setPrepareSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		modeMechanism
				.setAcceptSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
	}
	protected void simulateSequentialPaxos() {
		simulateBasicPaxos();
		commandMechanism.setSequentialAccess(true);
		modeMechanism.setSequentialAccess(true);
		overrideRetry = false;
	}

	protected boolean retryHigh(ProposalState aState) {
		return aState == ProposalState.PROPOSAL_CONCURRENT_OPERATION
				|| aState == ProposalState.CENTRAL_SERVER_DIED;
	}
	
	protected Long reProposeTime() {
		return overrideRetry?null:RE_PROPOSE_TIME;
	}
	
	
	protected boolean retry(ProposalState aState) {

		return !overrideRetry && 
				(aState == null | retryHigh(aState)
				|| aState == ProposalState.PROPOSAL_AGGREGATE_DENIAL);
	}

	@Override
	protected void customizeConsensusMechanisms() {
		simulateSequentialPaxos();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) {return;} 
		String newCommand = (String) anEvent.getNewValue();
		proposeCommand(newCommand);

	}
	
	protected void setThreads() {
		commandMechanism.setAcceptedInSeparareThread(true);
		commandMechanism.setAcceptInSeparateThread(true);
		commandMechanism.setPrepareInSeparateThread(true);
		
		modeMechanism.setAcceptedInSeparareThread(true);
		modeMechanism.setAcceptInSeparateThread(true);
		modeMechanism.setPrepareInSeparateThread(true);
	}


}
