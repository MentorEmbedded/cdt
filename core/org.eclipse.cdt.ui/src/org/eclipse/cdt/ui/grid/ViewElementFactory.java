package org.eclipse.cdt.ui.grid;

/**
 * @since 5.7
 */
public class ViewElementFactory {
	
	public GridElement createViewElement(IPresentationModel model) {
		
		if (model instanceof ISelectionPresentationModel) {
				return new PillSelectionViewElement((ISelectionPresentationModel) model);
		} else if (model instanceof IStringPresentationModel) {
			if (model.suggestedViewClass() == PathViewElement.class)
				return new PathViewElement((IStringPresentationModel)model);
			else
				return new StringViewElement((IStringPresentationModel) model);
		} else if (model instanceof IBooleanPresentationModel) {
			return new CheckboxViewElement((IBooleanPresentationModel)model);
		}	
		else if (model instanceof ICompositePresentationModel) {
			
			ICompositePresentationModel composite = (ICompositePresentationModel) model;
			if (model.getName() == null || model.getName().isEmpty())
			{
				// Just group view elements with no changes to presentation.
				GridElement group = new GridElement() {};
				for (IPresentationModel m: composite.getChildren())
					group.addChild(createViewElement(m));
				return group;
			}
			else
			{
				BasicGroupGridElement group = new BasicGroupGridElement(model.getName());
				for (IPresentationModel m: composite.getChildren())
					group.addChild(createViewElement(m));
				return group;				
			}
		}
		
		return null;
	}
}
