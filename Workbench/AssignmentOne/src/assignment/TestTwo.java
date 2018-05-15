package assignment;

import clients.BeauClient;
import nioextend.BroadcastingNioServer;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class TestTwo {
	public static void main(String[] args) {
		//Set up trace settings
		//Do Not show warings in OE
		Tracer.showWarnings(false);
		//Show traceables
		Tracer.showInfo(true);
				
		//Set up the Tracer to show status when it is in any of these classes, went with the client and the server. 
		//rest is housekeeping
		Tracer.setKeywordPrintStatus(BeauClient.class, true);
		Tracer.setKeywordPrintStatus(BroadcastingNioServer.class, true);
		Tracer.setDisplayThreadName(true);
		TraceableInfo.setPrintTraceable(true);
		TraceableInfo.setPrintTime(true);
		
		
	}
}
