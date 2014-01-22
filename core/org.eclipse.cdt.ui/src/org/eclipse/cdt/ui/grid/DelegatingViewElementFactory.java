package org.eclipse.cdt.ui.grid;

public abstract class DelegatingViewElementFactory implements IViewElementFactory {

	public DelegatingViewElementFactory(IViewElementFactory inner)
	{
		this.inner = inner;
	}
	
	@Override
	public IGridElement createElement(IPresentationModel model) {
		
		IGridElement element = createCustom(model);
		if (element != null)
			return element;
		
		return inner.createElement(model);
	}
	
	public abstract IGridElement createCustom(IPresentationModel model);
	
	private IViewElementFactory inner;
}
