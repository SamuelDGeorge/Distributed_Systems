package three.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public interface PropertyListenerSupport {
	public int size();
	public PropertyChangeListener elementAt(String name);
	public void addElement(String name,PropertyChangeListener l);
	public void addElement(PropertyChangeListener l);
	public void notifyAllListeners(PropertyChangeEvent event);
	public void notifySpecificListener(PropertyChangeEvent evt,String listenerName);
}
