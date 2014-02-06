package org.eclipse.cdt.ui.grid;

public class DefaultViewElementFactory implements IViewElementFactory {

	@Override
	public IGridElement createElement(ISomePresentationModel model) {
		
		if (model instanceof IStringPresentationModel)
		{
			IStringPresentationModel castModel = (IStringPresentationModel)model;
			return new StringViewElement(castModel);
		}
		else if (model instanceof ICompositePresentationModel) {
			
		}
		
		
		return null;
	}

}
