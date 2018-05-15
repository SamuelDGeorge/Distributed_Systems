package three.monitors;

public class MonitorFactory {
	private static ConcensusMonitor ipcMonitor;
	private static ConcensusMonitor modeMonitor;
	private static ClassLock raceMonitor;
	
	public static ConcensusMonitor getIPCConcensusMonitor() {
		if (ipcMonitor == null) {
			ipcMonitor = new AConcensusMonitor();
			return ipcMonitor;
		}
		return ipcMonitor;
	}
	
	public static ConcensusMonitor getBroadcastModeConcensusMonitor() {
		if(modeMonitor == null) {
			modeMonitor = new AConcensusMonitor();
			return modeMonitor;
		}
		return modeMonitor;
	}
	
	public static ClassLock getRaceLock() {
		if (raceMonitor == null) {
			raceMonitor = new AClassLock();
			return raceMonitor;
		}
		return raceMonitor;
	}
}
