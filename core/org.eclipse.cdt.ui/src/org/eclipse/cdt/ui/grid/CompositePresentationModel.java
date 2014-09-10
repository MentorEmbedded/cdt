package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @since 6.0
 */
public class CompositePresentationModel extends PresentationModel implements ICompositePresentationModel {
	
	private Object casls;
	public CompositePresentationModel()
	{
		super("");
	}
	
	public CompositePresentationModel(String name)
	{
		super(name);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public void setClasses(String[] classes)
	{
		this.classes = Arrays.asList(classes);
	}
	
	@Override
	public List<String> getClasses() {
		return classes;
	}
	
	private IPresentationModel.Listener listener = new IPresentationModel.DefaultListener() {
		
		@Override
		public void changed(int what, Object object) {
			if ((what & IPresentationModel.ACTIVATED) != 0)
				notifyListeners(what, object);
						
			// TODO: maybe, need to do something sensible for other whats				
		}
	};
	
	public void add(IPresentationModel child)
	{
		children.add(child);
		child.addAndCallListener(listener);
		getListener().childAdded(this, child);
	}
	
	public void remove(IPresentationModel child) {
		child.removeListener(listener);
		children.remove(child);
		getListener().childRemoved(this, child);
	}
	
	@Override
	public void setVisible(boolean v) {
		for (IPresentationModel child: children) {
			if (child instanceof PresentationModel) {
				((PresentationModel)child).setVisible(v);
			}
		}
		super.setVisible(v);
	}

	@Override
	public List<IPresentationModel> getChildren() {
		return children;
	}
	
	@Override
	public IPresentationModel findChild(String id) {
		for (IPresentationModel m: getChildren()) {
			if (m.getName().equals(id)) 
				return m;
		}
		
		for (IPresentationModel m: getChildren()) {
			IPresentationModel r = m.findChild(id);
			if (r != null)
				return r;
		}
		
		return null;
	}
	
	public String name;
	public List<IPresentationModel> children = new ArrayList<IPresentationModel>();
	
	public List<String> classes = Collections.emptyList();
}
