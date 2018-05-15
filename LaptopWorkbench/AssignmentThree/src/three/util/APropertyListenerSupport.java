package three.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APropertyListenerSupport implements PropertyListenerSupport{
	Map<String,PropertyChangeListener> listeners = new HashMap<String,PropertyChangeListener>();
	List<String> names = new ArrayList<String>();
	private int size = 0;
	
	public int size() {
		return this.size;
	}
	
	public PropertyChangeListener elementAt(String name) {
		return this.listeners.get(name);
	}

	public void addElement(String name, PropertyChangeListener l) {
		this.listeners.put(name, l);
		this.names.add(name);
		this.size++;
	}
	
	public void addElement(PropertyChangeListener l) {
		String temp = "" + this.size + "";
		this.addElement(temp, l);
		
	}

	public void notifyAllListeners(PropertyChangeEvent event) {
		for (int i = 0; i < this.size; i++) {
			this.listeners.get(this.names.get(i)).propertyChange(event);
		}
		
	}


	public void notifySpecificListener(PropertyChangeEvent evt, String listenerName) {
		try {
			this.listeners.get(listenerName).propertyChange(evt);
		} catch (Exception e) {
			System.out.println("Listener Not Found!");
		}
		
	}


	
	


}
