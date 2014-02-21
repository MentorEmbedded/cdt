package org.eclipse.cdt.ui.grid;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @since 5.7
 */
public class GridView extends Composite {
	
	IAdapterManager adaptor = null; // FIXME:
	
	public GridView(Composite parent)
	{
		super(parent, SWT.NONE);
	}
	
	/*
	
	public void setViewModel(IGridViewModel viewModel)
	{
		// FIXME: we actually need to have a selected element as well.
		
		
		
		IGridElement element = (IGridElement)adaptor.getAdapter(viewModel, IGridElement.class);
		element.fillIntoGrid(this);
	}
	
	public void setCurrent(IGridViewModel viewModel)
	{
		assert viewElement != null;
		viewElement.setCurrent()
	}
	*/

}


