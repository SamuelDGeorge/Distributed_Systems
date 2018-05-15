package three.ui;
import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

import StringProcessors.HalloweenCommandProcessor;
import port.trace.nio.RemoteCommandExecuted;
import three.clients.Client;
import three.server.BeauGIPCServer;

public class EventHandler implements Runnable{
	private BlockingQueue<PropertyChangeEvent> queue;
	private Client processor;
	private HalloweenCommandProcessor simulation;
	public EventHandler(Client client,HalloweenCommandProcessor simulation,BlockingQueue<PropertyChangeEvent> queue) {
		this.queue = queue;
		this.processor = client;
		this.simulation = simulation;
	}

	public void run() {
			PropertyChangeEvent evt = null;
			while (true) {
				try {
					evt = this.queue.take();
					if (evt.getPropertyName().equals("InputString")){
						if (this.processor.isLocal()) {
							//do nothing
						} else if (this.processor.isNonAtomic()) {
							this.processor.sendCommand((String) evt.getNewValue());
						} else {
							this.processor.sendCommand((String) evt.getNewValue());
						}	
					} else {}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
			}
		
		
	}
}