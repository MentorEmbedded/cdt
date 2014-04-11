
package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.List;

/** Implementation of the IPresentationModel interface. 
 * 
 *  Adds protected methods to invoke listeners.
 * @since 5.7
 */
public class PresentationModel implements IPresentationModel {
		
	private List<Listener> listeners = new ArrayList<Listener>();
	private String name;
	private boolean visible = true;
	private boolean enabled = true;
	private String id = ""; //$NON-NLS-1$

	@Override
	public String getName()
	{
		return name;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean v)
	{
		if (v != visible) {
			visible = v;
			notifyListeners(VISIBILITY_CHANGED, this);
		}
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean e) {
		if (e != enabled) {
			enabled = e;
			notifyListeners(ENABLENESS_CHANGED, this);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.ui.grid.IPresentationModel#addAndCallListener(org.eclipse.cdt.ui.grid.PresentationModel.Listener)
	 */
	@Override
	public void addAndCallListener(Listener listener)
	{
		this.listeners.add(listener);
		// FIXME: just OR every flag.	
		listener.changed(IPresentationModel.VALUE_CHANGED | IPresentationModel.VISIBILITY_CHANGED, this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.ui.grid.IPresentationModel#removeListener(org.eclipse.cdt.ui.grid.PresentationModel.Listener)
	 */
	@Override
	public void removeListener(Listener listener)
	{
		this.listeners.remove(listener);
	}
	
	@Override
	public void activate() {
		notifyListeners(ACTIVATED, this);
	}
	
	protected void notifyListeners(int what, Object object)
	{
		for (Listener l: listeners)
			l.changed(what, object);
	}
	
	protected void notifyListeners()
	{
		notifyListeners(0, this);
	}
	
	@Override
	public IPresentationModel findChild(String id) {
		return null;
	}

	public PresentationModel(String name) {
		this.name = name;
	}
}