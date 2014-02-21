package org.eclipse.cdt.ui.grid;


/**
 * @since 5.7
 */
public abstract class ViewElement extends GridElement {

	public ViewElement(IPresentationModel model) {
		this.model = model;
	}
	
	public IPresentationModel getModel() 
	{
		return model;
	}
	
	private IPresentationModel model;
}
