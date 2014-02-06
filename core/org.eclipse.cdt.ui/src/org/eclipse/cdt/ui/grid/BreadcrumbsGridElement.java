package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.widgets.Composite;

public class BreadcrumbsGridElement implements IGridElement {
	
	public BreadcrumbsGridElement(Composite parent, ISomePresentationModel model, IViewElementFactory factory) 
	{
		this.parent = parent;
		
		this.factory = factory;					
	}
	
	
	/*
	private select(IPresentationModel model)
	{		
		// Update
		
		
		//currentElement->setVisible(false);
		
		IViewElement viewElement = factory.createElement(model);
		viewElement->fillIntoGrid(parent);
	}*/
	
		
	/*
	 
	 PresentationFactory f = ....
	 IGridElement view = f.get(model);
	 // 
	 view->fillIntoGrid(); 
	   
	 */
	
	@Override
	public void fillIntoGrid(Composite parent) {
		
		
		
		// TODO Auto-generated method stub
		
	}
	
	
	private Composite parent;
	private ISomePresentationModel topModel;
	private ISomePresentationModel selectedModel;
	private IViewElementFactory factory;
	
}
