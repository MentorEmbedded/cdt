package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.widgets.Composite;

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
	
	@Override
	public void create(Composite parent) {
		
		super.create(parent);
		
		setupListener();
	}

	protected void setupListener() {
		model.addAndCallListener(listener = new IPresentationModel.Listener() {

			@Override
			public void changed(int what, Object object) {
				modelChanged(what, object);
			}

			@Override
			public void childAdded(IPresentationModel parent, IPresentationModel child) {
				assert parent == getModel();
				modelChildAdded(child);
			}

			@Override
			public void childRemoved(IPresentationModel parent, IPresentationModel child) {
				assert parent == getModel();
				modelChildRemoved(child);
			}
			
			@Override
			public void visibilityChanged(IPresentationModel model, boolean visible) {
				assert model == getModel();
				modelVisibilityChanged(visible);
			}
			
		});
	}
	
	@Override
	public void dispose()
	{
		model.removeListener(listener);
		super.dispose();
	}
	
	public IPresentationModel getModel() 
	{
		return model;
	}
	
	protected void modelChanged(int what, Object object) {}
	
	protected void modelChildAdded(IPresentationModel child) {}

	protected void modelChildRemoved(IPresentationModel child) {}
	
	protected void modelVisibilityChanged(boolean visible) {
		setVisible(visible);
	}
	
	private IPresentationModel model;
	private IPresentationModel.Listener listener;
}
 