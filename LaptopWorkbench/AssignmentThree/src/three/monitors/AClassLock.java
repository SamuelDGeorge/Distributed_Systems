package three.monitors;

import three.server.BeauGIPCServer;
import three.server.BeauRMIServer;
import three.server.BroadcastingNioServer;

public class AClassLock implements ClassLock {
	private boolean nioOwns = false;
	private boolean rmiOwns = false;
	private boolean gipcOwns = false;
	private boolean locked = false;
	
	public synchronized void lock(Object requester) {
		while (locked) {
			if (requester instanceof BroadcastingNioServer && nioOwns) {
				break;
			} else if (requester instanceof BeauRMIServer && rmiOwns) {
				break;
			} else if (requester instanceof BeauGIPCServer && gipcOwns) {
				break;
			} else if (!this.locked) {
				break;
			} else {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		setLocked(requester);	
	}

	public synchronized void unlock() {
		this.nioOwns = false;
		this.rmiOwns = false;
		this.gipcOwns = false;
		this.locked = false;
		//System.out.println("Race Lock lifted!");
		this.notifyAll();
	}
	
	private void setLocked(Object requester) {
		if (requester instanceof BroadcastingNioServer) {
			this.nioOwns = true;
			this.locked = true;
			//System.out.println("NIO Owns the lock.");
		} else if (requester instanceof BeauRMIServer) {
			this.rmiOwns = true;
			this.locked = true;
			//System.out.println("RMI Owns the lock");
		} else if (requester instanceof BeauGIPCServer) {
			this.gipcOwns = true;
			this.locked = true;
			//System.out.println("GIPC Owns the lock");
		} else {
			System.out.println("You are not able to own the lock.");
		}
	}

}
