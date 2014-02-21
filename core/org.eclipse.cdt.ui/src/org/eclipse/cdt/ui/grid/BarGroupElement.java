package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.widgets.Composite;

/**
 * @since 5.7
 */
public class BarGroupElement extends GridElement {
	
	public BarGroupElement(Composite parent, ICompositePresentationModel model, IViewElementFactory factory) {
		this.model = model;
	}

	@Override
	public void createImmediateContent(Composite parent) {
		
		
		
		for (ISomePresentationModel cm: model.getChildren()) {
			
			GridElement e = factory.createElement(cm);
			e.fillIntoGrid(parent);
			
			
			
			
		}		
	}
	
	ICompositePresentationModel model;
	IViewElementFactory factory;
		
}
