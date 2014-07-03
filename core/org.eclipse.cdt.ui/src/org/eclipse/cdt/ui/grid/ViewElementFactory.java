package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.widgets.Composite;

/**
 * @since 5.7
 */
public class ViewElementFactory {
	
	public ViewElementFactory()
	{
		
	}
	
	public final GridElement create(final IPresentationModel model, Composite parent) {
		GridElement e = createViewElement(model, parent);
		e.create(parent);
		return e;
	}
	
	protected GridElement createViewElement(final IPresentationModel model, Composite parent) {
	
		if (model instanceof ISelectionPresentationModel) {
			return new PillSelectionViewElement((ISelectionPresentationModel) model);
			// FIXME: extract interface here. Checking for very specific implementation type is wrong.
		} else if (model instanceof PathStringReflectionPresentationModel) { 
			return new PathViewElement((PathStringReflectionPresentationModel)model);
		} else if (model instanceof IStringPresentationModel) {
			return new StringViewElement((IStringPresentationModel) model);
		} else if (model instanceof IStaticStringPresentationModel) {
			return new LinkViewElement((IStaticStringPresentationModel)model);
		} else if (model instanceof IBooleanPresentationModel) {
			return new CheckboxViewElement((IBooleanPresentationModel)model);
		}
		else if (model instanceof ListPresentationModel) {
			return new ListViewElement((ListPresentationModel)model, this);
		}
		else if (model instanceof ICompositePresentationModel) {
			
			// FIXME: this must be a proper class, finally.
			ICompositePresentationModel composite = (ICompositePresentationModel) model;
			if (model.getName() == null || model.getName().isEmpty() || composite.getClasses().indexOf("top") != -1)
			{
				// Just group view elements with no changes to presentation.
				GridElement group = new GridElement() {};
				group.create(parent);
				for (IPresentationModel m: composite.getChildren())
					group.addChild(create(m, parent));
				return group;
			}
			else
			{
				final BasicGroupGridElement group = new BasicGroupGridElement(model.getName());
				group.create(parent);
				for (IPresentationModel m: composite.getChildren())
					group.addChild(create(m, parent));
				model.addAndCallListener(new IPresentationModel.DefaultListener() {
					
					@Override
					public void visibilityChanged(IPresentationModel model, boolean visible) {
						group.setVisible(model.isVisible());
					}
				});
				return group;				
			}
		}
		
		return null;
	}
}
