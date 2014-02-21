package org.eclipse.cdt.ui.grid;

/**
 * @since 5.7
 */
public abstract class DelegatingViewElementFactory implements IViewElementFactory {

	public DelegatingViewElementFactory(IViewElementFactory inner)
	{
		this.inner = inner;
	}
	
	@Override
	public GridElement createElement(ISomePresentationModel model) {
		
		GridElement element = createCustom(model);
		if (element != null)
			return element;
		
		return inner.createElement(model);
	}
	
	public abstract GridElement createCustom(ISomePresentationModel model);
	
	private IViewElementFactory inner;
}
