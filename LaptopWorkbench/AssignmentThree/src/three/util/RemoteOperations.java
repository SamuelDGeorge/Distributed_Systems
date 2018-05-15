package three.util;

import java.util.concurrent.BlockingQueue;

import StringProcessors.HalloweenCommandProcessor;
import port.trace.nio.RemoteCommandExecuted;

public class RemoteOperations implements Runnable{
	private BlockingQueue<String> queue;
	private HalloweenCommandProcessor processor;
	public RemoteOperations(HalloweenCommandProcessor observingSimulation, BlockingQueue<String> queue) {
		this.queue = queue;
		this.processor = observingSimulation;
	}

	public void run() {
			String read = null;
			while (true) {
				try {
					read = this.queue.take();
					String[] commands = read.split(",");
					for (int i = 0; i < commands.length; i++) {
						this.processor.processCommand(commands[i]);
						RemoteCommandExecuted.newCase(this, commands[i]);
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
			}
		
		
	}
}