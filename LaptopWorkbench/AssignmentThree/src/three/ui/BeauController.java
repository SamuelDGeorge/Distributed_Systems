package three.ui;

import three.util.PropertyListenerRegisterer;

public interface BeauController extends PropertyListenerRegisterer{
	public void setText(String text);
	public void setSelected(String mode);
}
