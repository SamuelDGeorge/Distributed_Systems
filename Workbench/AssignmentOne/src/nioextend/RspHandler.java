package nioextend;
public class RspHandler {
	private byte[] rsp = null;	
	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return true;
	}	
	public synchronized String waitForResponse() {
		try {
		while(this.rsp == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		
		return new String(this.rsp);
		} finally {
			this.rsp = null;
		}
	}
}

