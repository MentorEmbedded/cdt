package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.List;

public class CompositePresentationModel implements ICompositePresentationModel {
	
	public CompositePresentationModel(String name)
	{
		this.name = name;
	}
	
	public void add(ISomePresentationModel child)
	{
		children.add(child);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<ISomePresentationModel> getChildren() {
		return children;
	}
	
	public String name;
	public List<ISomePresentationModel> children = new ArrayList<ISomePresentationModel>();

}
