package three.monitors;

public interface ClassLock {
	public void lock(Object owningClass);
	public void unlock();
}
