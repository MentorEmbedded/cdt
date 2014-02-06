package org.eclipse.cdt.ui.grid;

import java.util.List;

public class SelectionPresentationModel extends StringPresentationModel implements ISelectionPresentationModel {

	public SelectionPresentationModel(String name, List<String> possibleValues)
	{
		super(name);
		this.possibleValues = possibleValues;
	}

	@Override
	public List<String> getPossibleValues() {
		return possibleValues;
	}
	
	private List<String> possibleValues;
}
