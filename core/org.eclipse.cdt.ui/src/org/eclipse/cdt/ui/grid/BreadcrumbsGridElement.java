package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.widgets.Composite;

/**
 * @since 5.7
 */
public class BreadcrumbsGridElement extends GridElement {
	
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
	public void createImmediateContent(Composite parent) {
		
		
		
		// TODO Auto-generated method stub
		
	}
	
	
	private Composite parent;
	private ISomePresentationModel topModel;
	private ISomePresentationModel selectedModel;
	private IViewElementFactory factory;
	
}
