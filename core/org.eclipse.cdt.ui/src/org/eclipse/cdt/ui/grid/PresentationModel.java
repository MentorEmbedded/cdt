
package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.List;

/** Implementation of the IPresentationModel interface. 
 * 
 *  Adds protected methods to invoke listeners.
 */
public class PresentationModel implements IPresentationModel {
		
	private List<Listener> listeners = new ArrayList<Listener>();
	private String name;

	@Override
	public String getName()
	{
		return name;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.ui.grid.IPresentationModel#addAndCallListener(org.eclipse.cdt.ui.grid.PresentationModel.Listener)
	 */
	@Override
	public void addAndCallListener(Listener listener)
	{
		this.listeners.add(listener);
		listener.changed(IPresentationModel.CHANGED, this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.ui.grid.IPresentationModel#removeListener(org.eclipse.cdt.ui.grid.PresentationModel.Listener)
	 */
	@Override
	public void removeListener(Listener listener)
	{
		this.listeners.remove(listener);
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

	public PresentationModel(String name) {
		this.name = name;
	}
}