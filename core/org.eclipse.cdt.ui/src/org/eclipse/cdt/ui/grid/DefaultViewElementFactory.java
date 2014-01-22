package org.eclipse.cdt.ui.grid;

public class DefaultViewElementFactory implements IViewElementFactory {

	@Override
	public IGridElement createElement(IPresentationModel model) {
		
		if (model instanceof IPresentationModelString)
		{
			IPresentationModelString castModel = (IPresentationModelString)model;
			return new StringViewElement(castModel);
		}
		else if (model instanceof ICompositePresentationModel) {
			
		}
		
		
		return null;
	}

}
