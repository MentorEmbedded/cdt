package org.eclipse.cdt.ui.grid;

/**
 * @since 5.7
 */
public class ViewElementFactory {
	
	public ViewElementFactory()
	{
		
	}
	
	public GridElement createViewElement(final IPresentationModel model) {

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
			
			ICompositePresentationModel composite = (ICompositePresentationModel) model;
			if (model.getName() == null || model.getName().isEmpty() || composite.getClasses().indexOf("top") != -1)
			{
				// Just group view elements with no changes to presentation.
				GridElement group = new GridElement() {};
				for (IPresentationModel m: composite.getChildren())
					group.addChild(createViewElement(m));
				return group;
			}
			else
			{
				final BasicGroupGridElement group = new BasicGroupGridElement(model.getName());
				for (IPresentationModel m: composite.getChildren())
					group.addChild(createViewElement(m));
				model.addAndCallListener(new IPresentationModel.DefaultListener() {
					@Override
					public void changed(int what, Object object) {
						if ((what & IPresentationModel.VISIBILITY_CHANGED) != 0)
							group.setVisible(model.isVisible());
					}
				});
				return group;				
			}
		}
		
		return null;
	}
}
