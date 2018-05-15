package six.runners;

import java.util.Scanner;

import inputport.datacomm.simplex.buffer.nio.AScatterGatherSelectionManager;
import three.clients.GlobalClient;
import three.monitors.MonitorFactory;

public class PerformanceTesterAll {
	public static void performanceSetup(GlobalClient toManipulateOne) {
		//turn on concensus for all
		toManipulateOne.setConcensusModeOnForBroadcast(true);
		toManipulateOne.setConcensusModeOnForMechanism(true);
		
	}
	
	public static void performanceExperiments(GlobalClient toManipulateOne) {
		//set up conditions for the experiment
		performanceSetup(toManipulateOne);
		
		System.out.println("Running all performance Experiments:");	
		
		System.out.println("Running all experiment for IPC: GIPC");
		
		testAllPaxos(toManipulateOne);
		//runAllIPC(toManipulateOne);
		
		
	}
	
	public static void testAllPaxos(GlobalClient toManipulateOne) {
		System.out.println("Testing PAXOS");
		toManipulateOne.sendIPCMode("paxos");
		MonitorFactory.getIPCConcensusMonitor().waitForConcensus();
		runAllPaxos(toManipulateOne);
	}
	
	private static void runAllIPC(GlobalClient toManipulateOne) {
		System.out.println("Testing NIO");
		toManipulateOne.sendIPCMode("nio");
		MonitorFactory.getIPCConcensusMonitor().waitForConcensus();
		runAllModes(toManipulateOne);
		
		System.out.println("Testing RMI");
		toManipulateOne.sendIPCMode("rmi");
		MonitorFactory.getIPCConcensusMonitor().waitForConcensus();
		runAllModes(toManipulateOne);
		
		System.out.println("Testing GIPC");
		toManipulateOne.sendIPCMode("gipc");
		MonitorFactory.getIPCConcensusMonitor().waitForConcensus();
		runAllModes(toManipulateOne);
		
		System.out.println("Testing PAXOS");
		toManipulateOne.sendIPCMode("paxos");
		MonitorFactory.getIPCConcensusMonitor().waitForConcensus();
		runAllPaxos(toManipulateOne);
		
	}

	private static void runAllPaxos(GlobalClient toManipulateOne) {
		System.out.println("Testing: NonAtomicAsynchronous");
		toManipulateOne.setPaxosMode(1);
		long startTime = System.nanoTime();
		runCommands(toManipulateOne);
		long endTime = System.nanoTime();
		long timeTaken = (endTime-startTime)/1000000;
		System.out.println("NonAtomicAsynchronous: " + timeTaken );
		
		System.out.println("Testing: NonAtomicSynchronous");
		toManipulateOne.setPaxosMode(2);
		startTime = System.nanoTime();
		runCommands(toManipulateOne);
		endTime = System.nanoTime();
		timeTaken = (endTime-startTime)/1000000;
		System.out.println("NonAtomicAsynchronous: " + timeTaken );
		
		System.out.println("Testing: CentralizedAsynchronous");
		toManipulateOne.setPaxosMode(3);
		startTime = System.nanoTime();
		runCommands(toManipulateOne);
		endTime = System.nanoTime();
		timeTaken = (endTime-startTime)/1000000;
		System.out.println("NonAtomicAsynchronous: " + timeTaken );
		
		System.out.println("Testing: CentralizedSynchronous");
		toManipulateOne.setPaxosMode(4);
		startTime = System.nanoTime();
		runCommands(toManipulateOne);
		endTime = System.nanoTime();
		timeTaken = (endTime-startTime)/1000000;
		System.out.println("NonAtomicAsynchronous: " + timeTaken );
		
		System.out.println("Testing: SequentialPaxos");
		toManipulateOne.setPaxosMode(5);
		startTime = System.nanoTime();
		runCommands(toManipulateOne);
		endTime = System.nanoTime();
		timeTaken = (endTime-startTime)/1000000;
		System.out.println("NonAtomicAsynchronous: " + timeTaken );
		
		
		
		
		
	}
	
	private static void runAllModes(GlobalClient toManipulateOne) {
		
		
		System.out.println("Testing: Local");
		toManipulateOne.sendMode("local");
		MonitorFactory.getBroadcastModeConcensusMonitor().waitForConcensus();
		long startTime = System.nanoTime();
		runCommands(toManipulateOne);
		long endTime = System.nanoTime();
		long timeTaken = (endTime-startTime)/1000000;
		System.out.println("Local Took: " + timeTaken );
		
		
		System.out.println("Testing: Non-Atomic");
		toManipulateOne.sendMode("non-atomic");
		MonitorFactory.getBroadcastModeConcensusMonitor().waitForConcensus();
		startTime = System.nanoTime();
		runCommands(toManipulateOne);
		endTime = System.nanoTime();
		timeTaken = (endTime-startTime)/1000000;
		System.out.println("Non-Atomic Took: " + timeTaken );
		
		
		
		System.out.println("Testing: Atomic");
		toManipulateOne.sendMode("atomic");
		MonitorFactory.getBroadcastModeConcensusMonitor().waitForConcensus();
		startTime = System.nanoTime();
		runCommands(toManipulateOne);
		endTime = System.nanoTime();
		timeTaken = (endTime-startTime)/1000000;
		System.out.println("Atomic Took: " + timeTaken );
		
		
		
	}

	private static void runCommands(GlobalClient toManipulateOne) {
		int backAndForth = 0;
		
		while (backAndForth < 5) {
			for (int i = 0; i < 25; i++) {
				toManipulateOne.sendCommand("move 2 0");
				toManipulateOne.sendCommand("move 2 0");
				
			}
	
			for (int i = 0; i < 25; i++) {
				toManipulateOne.sendCommand("move -2 0");
				toManipulateOne.sendCommand("move -2 0");
			}
			
			backAndForth++;
		}
		
	}
}
