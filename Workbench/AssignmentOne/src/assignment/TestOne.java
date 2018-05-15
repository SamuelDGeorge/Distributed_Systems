package assignment;

import java.io.IOException;
import java.net.InetAddress;

import CommandObjects.RemoteOperations;
import clients.BeauClient;
import nioextend.BroadcastingNioServer;
import servers.EchoingServerStarter;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class TestOne {
	public static void main(String[] args) throws IOException, InterruptedException {
		//Set up trace settings
		//Do Not show warings in OE
		Tracer.showWarnings(false);
		//Show traceables
		Tracer.showInfo(true);
		
		//Set up the Tracer to show status when it is in any of these classes, went with the client and the server. 
		//rest is housekeeping
		Tracer.setKeywordPrintStatus(BeauClient.class, true);
		Tracer.setKeywordPrintStatus(BroadcastingNioServer.class, true);
		Tracer.setKeywordPrintStatus(RemoteOperations.class, true);
		Tracer.setDisplayThreadName(true);
		TraceableInfo.setPrintTraceable(true);
		TraceableInfo.setPrintTime(true);
		
		//Begin by starting up a server. 
		EchoingServerStarter server = new EchoingServerStarter(null,9090);
		server.startServer();
		Object lock = new Object();
		BeauClient test = new BeauClient(InetAddress.getByName("localhost"),9090);
		new BeauClient(InetAddress.getByName("localhost"),9090);
		synchronized (lock) {
			lock.wait(2000);
			System.out.println("Done Waiting");
		}
		
		for(int i =0; i<30; i++) {
			test.sendData("move 1 1".getBytes());
		
		}
		
		
		
		
		
		/*BeauCleint clientOne = new BeauCleint(InetAddress.getByName("localhost"), 9090);
	    Thread tone = new Thread(clientOne);
	    tone.setDaemon(true);
	    tone.start();
	    
	    BeauCleint clientTwo = new BeauCleint(InetAddress.getByName("localhost"), 9090);
	    Thread ttwo = new Thread(clientTwo);
	    ttwo.setDaemon(true);
	    ttwo.start();
	    
	    BeauCleint clientThree = new BeauCleint(InetAddress.getByName("localhost"), 9090);
	    Thread tthree = new Thread(clientThree);
	    tthree.setDaemon(true);
	    tthree.start();
	    
	    BeauCleint clientFour = new BeauCleint(InetAddress.getByName("localhost"), 9090);
	    Thread tfour = new Thread(clientFour);
	    tfour.setDaemon(true);
	    tfour.start();*/
	    
	    
	    
	    
	}
}
