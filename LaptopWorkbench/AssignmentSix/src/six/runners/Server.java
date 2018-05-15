package six.runners;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.util.Scanner;

import inputport.datacomm.simplex.buffer.nio.AScatterGatherSelectionManager;
import port.trace.nio.NIOTraceUtility;
import three.server.AGlobalServer;
import three.server.GlobalServer;
import util.trace.Tracer;


public class Server {
	
	public static void main(String[] args) throws NotBoundException, UnknownHostException, IOException {
		Tracer.showWarnings(false);
		//Tracer.showInfo(true);
		//NIOTraceUtility.setTracing();
		AScatterGatherSelectionManager.setMaxOutstandingWrites(1000);
		GlobalServer server  = new AGlobalServer();
		server.setSynchronizedResponse(true);
		
		
		serverController(server);
	}

	private static void serverController(GlobalServer server) {
		Scanner input = new Scanner(System.in);
		String current;
		while (true) {
			System.out.println("Please enter a valid command:");
			current = input.nextLine();
			if("race".equals(current)) {
				System.out.println("Would you like race condition checking On(Y) or Off(N)");
				current = input.nextLine();
				if("Y".equals(current)) {
					server.setSynchronizedResponse(true);
				} else {
					server.setSynchronizedResponse(false);
				}
			} else if ("exit".equals(current)) {
				System.out.println("Server shutting down!");
				System.exit(0);
			} else {
				System.out.println("Not a valid command");
			}
		}
		
	}
	
	

}
