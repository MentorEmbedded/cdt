package org.eclipse.cdt.ui.grid;

/**
 * @since 6.0
 */
public class DefaultViewElementFactory implements IViewElementFactory {

	@Override
	public GridElement createElement(ISomePresentationModel model) {
		
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
