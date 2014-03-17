package org.eclipse.cdt.ui.grid;

/** GridElement that displays an IPresentationModel,
 *  and synchronizes changes between UI and model in
 *  both directions.
 *  
 *  As soon as 'create' method is called, the UI elements
 *  are created and must display the current state of the
 *  model, and sync must already work.
 *  FIXME: actually rename fillIntoGrid to 'create' or 'createControls'
 *  
 * @since 5.7
 */
public abstract class ViewElement extends GridElement {

	// Public interfaces 
	
	public ViewElement(IPresentationModel model) {
		this.model = model;
	}
	
	public IPresentationModel getModel() 
	{
		return model;
	}
		
	private IPresentationModel model;
}
 