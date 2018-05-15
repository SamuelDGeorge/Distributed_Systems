package three.monitors;

public class AConcensusMonitor implements ConcensusMonitor {
	private boolean concensusAchieved;
	
	public AConcensusMonitor() {
		this.concensusAchieved = true;
	}
	
	public synchronized void waitForConcensus() {
			while(!getConcensusAchieved()) {
				//System.out.println("WaitingOnConcesus");
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//System.out.println("Unlocked!");
	}

	public synchronized void setconcensusAchieved(boolean toSet) {
			if (toSet) {
				this.concensusAchieved = true;
				//System.out.println("Concensus Achieved!");
				notifyAll();
			} else {
				this.concensusAchieved = false;
			}		
	}

	public boolean getConcensusAchieved() {
		return this.concensusAchieved;
	}

}
