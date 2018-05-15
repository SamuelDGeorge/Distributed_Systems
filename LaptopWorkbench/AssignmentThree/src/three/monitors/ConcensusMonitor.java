package three.monitors;

public interface ConcensusMonitor {
	public void waitForConcensus();
	public void setconcensusAchieved(boolean toSet);
	public boolean getConcensusAchieved();
}
