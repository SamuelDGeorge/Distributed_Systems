package six.runners;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.util.Scanner;

import examples.gipc.consensus.Member1;
import examples.gipc.consensus.Member3;
import inputport.datacomm.simplex.buffer.nio.AScatterGatherSelectionManager;
import port.trace.nio.NIOTraceUtility;
import six.globalclient.AGlobalClient;
import three.clients.GlobalClient;
import three.monitors.MonitorFactory;
import util.trace.Tracer;

public class ClientThree implements Member3 {
	public static void main(String[] args) throws UnknownHostException, IOException, NotBoundException {
		Tracer.showWarnings(false);
		//Show traceables
		//Tracer.showInfo(true);
		//NIOTraceUtility.setTracing();
		AScatterGatherSelectionManager.setMaxOutstandingWrites(900);
		GlobalClient test = new AGlobalClient("ClientThree", MY_NAME, MY_PORT_NUMBER);
		test.setConcensusModeOnForBroadcast(true);
		test.setConcensusModeOnForMechanism(true);
		test.sendIPCMode("paxos");
		//PerformanceTester.performanceExperiments(test);
		startConsole(test);
		
	}
	
	public static void startConsole(GlobalClient test) {
		String current;
		Scanner input = new Scanner(System.in);
		System.out.println("Please enter a command.");
		while (true) {
			current = input.nextLine();
			if ("command".equals(current)) {
				System.out.println("What to send?");
				current = input.nextLine();
				test.sendCommand(current);
			} else if ("broadcast".equals(current)) {
				System.out.println("What mode would you like to be in?");
				current = input.nextLine();
				test.sendMode(current);
			}else if ("ipc".equals(current)) {
				System.out.println("What IPC mechanism do you want to use?");
				current = input.nextLine();
				test.sendIPCMode(current);
			} else if ("cbroadcast".equals(current)) {
				System.out.println("Concensus Mode for Broadcast On(Y) or Off(N)");
				current = input.nextLine();
				if ("Y".equals(current)) {
					test.setConcensusModeOnForBroadcast(true);
				} else {
					test.setConcensusModeOnForBroadcast(false);
				}
			} else if ("cipc".equals(current)) {
				System.out.println("Concensus Mode for IPC On(Y) or Off(N)");
				current = input.nextLine();
				if ("Y".equals(current)) {
					test.setConcensusModeOnForMechanism(true);
				} else {
					test.setConcensusModeOnForMechanism(false);
				}
			}  else if ("performancemode".equals(current)) {
				PerformanceTesterAll.performanceSetup(test);
			}  else if ("performancetest".equals(current)) {
				PerformanceTesterAll.performanceExperiments(test);
			}  else if ("paxosmode".equals(current)) {
				System.out.println("What Mode would you like to be in? 1-5");
				current = input.nextLine();
				int mode = Integer.parseInt(current);
				test.setPaxosMode(mode);
			} else if ("exit".equals(current)) {
				System.out.println("Thanks for using Client!");
				System.exit(0);
			} else {
				System.out.println("Not a valid Command");
			}
			System.out.println("Please enter another command.");
		}
	}
	
	
}
