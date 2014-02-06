package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.widgets.Composite;

public class BarGroupElement implements IGridElement {
	
	public BarGroupElement(Composite parent, ICompositePresentationModel model, IViewElementFactory factory) {
		this.model = model;
	}

	@Override
	public void fillIntoGrid(Composite parent) {
		
		
		
		for (ISomePresentationModel cm: model.getChildren()) {
			
			IGridElement e = factory.createElement(cm);
			e.fillIntoGrid(parent);
			
			
			
			
		}		
	}
	
	ICompositePresentationModel model;
	IViewElementFactory factory;
		
}
