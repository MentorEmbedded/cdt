package org.eclipse.cdt.ui.grid;

/* Presentation model that is uneditable text, together with associated
 * model with more details.
 */

/**
 * @since 6.0
 */
public class DrilldownPresentationModel extends StaticStringPresentationModel {
	
	private String label;
	private IPresentationModel details;

	public DrilldownPresentationModel(String label, IPresentationModel details)
	{
		this.label = label;
		this.details = details;
	}
	
	@Override
	public String getString() {
		return label;
	}

	@Override
	public void activate() {
		notifyListeners(IPresentationModel.ACTIVATED, details);
	}

}
