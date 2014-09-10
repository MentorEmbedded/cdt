package org.eclipse.cdt.ui.grid;

/** View Element that can show composite presentation model,
 *  by first creating view elements for children presentation models,
 *  using a factory, and then combining these 
 * 
 * @since 6.0
 */
public class CompositeViewElement extends ViewElement {
	
	public CompositeViewElement(ICompositePresentationModel model) {
		super(model);
		
		// First create a child group element that will contain others.
		// This indirection is necessary so that CompositeViewElement
		// can define common behaviour of creating suitable view elements
		// without 
		GridElement group = createGroupElement();
		addChild(group);
		
		
		// BasicCompositeViewElement
		//
		//   addChildren(factory.create(model.getChildren())
		
		// if (model instanceof ICompositePresentationModel) return new BasicCompositeViewElement(model, this);

		
	}
	
	public GridElement createGroupElement()
	{
		return new BasicGroupGridElement(getModel().getName());
	}

}
