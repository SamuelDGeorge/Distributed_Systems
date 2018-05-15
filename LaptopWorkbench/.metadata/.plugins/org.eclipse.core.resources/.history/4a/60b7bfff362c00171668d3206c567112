package three.assignment;

import java.util.Scanner;

import inputport.datacomm.simplex.buffer.nio.AScatterGatherSelectionManager;
import three.clients.GlobalClient;
import three.monitors.MonitorFactory;

public class PerformanceTester {
	public static void performanceSetup(GlobalClient toManipulateOne) {
		//turn on concensus for all
		toManipulateOne.setConcensusModeOnForBroadcast(true);
		toManipulateOne.setConcensusModeOnForMechanism(true);
		
	}
	
	public static void performanceExperiments(GlobalClient toManipulateOne) {
		//set up conditions for the experiment
		performanceSetup(toManipulateOne);
		
		
		
		Object lock = new Object();
		synchronized(lock){try {
		lock.wait(5000);} catch (InterruptedException e) {e.printStackTrace();}}
		
		System.out.println("Running all performance Experiments:");
		
		System.out.println("Running all experiment for IPC: NIO");
		toManipulateOne.sendIPCMode("nio");
		MonitorFactory.getIPCConcensusMonitor().waitForConcensus();
		runAllModes(toManipulateOne);
		
		
		System.out.println("Running all experiment for IPC: RMI");
		toManipulateOne.sendIPCMode("rmi");
		MonitorFactory.getIPCConcensusMonitor().waitForConcensus();
		runAllModes(toManipulateOne);
		
		
		System.out.println("Running all experiment for IPC: GIPC");
		toManipulateOne.sendIPCMode("gipc");
		MonitorFactory.getIPCConcensusMonitor().waitForConcensus();
		runAllModes(toManipulateOne);
		
		
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
