package org.eclipse.cdt.ui.grid;

import java.util.List;

/**
 * @since 6.0
 */
public class SelectionPresentationModel extends StringPresentationModel implements ISelectionPresentationModel {

	public SelectionPresentationModel(String name)
	{
		super(name);
	}
	
	public SelectionPresentationModel(String name, List<String> possibleValues)
	{
		super(name);
		this.possibleValues = possibleValues;
	}
		
	@Override
	public List<String> getPossibleValues() {
		return possibleValues;
	}
	
	protected List<String> possibleValues;
}
