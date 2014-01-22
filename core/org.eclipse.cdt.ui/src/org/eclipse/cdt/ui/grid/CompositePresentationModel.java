package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.List;

public class CompositePresentationModel implements ICompositePresentationModel {
	
	public CompositePresentationModel(String name)
	{
		this.name = name;
	}
	
	public void add(IPresentationModel child)
	{
		children.add(child);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IPresentationModel> getChildren() {
		return children;
	}
	
	public String name;
	public List<IPresentationModel> children = new ArrayList<IPresentationModel>();

}
