package org.eclipse.cdt.ui.grid;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.FontAwesome;

/**
 * @since 5.7
 */
public class ListViewElement extends ViewElement {
	
	public ListViewElement(ListPresentationModel model, ViewElementFactory factory)
	{
		super(model);
		for (IPresentationModel c: model.getChildren())
			addChild(factory.createViewElement(c));
	}
	
	@Override
	public ListPresentationModel getModel() {
		return (ListPresentationModel) super.getModel();
	}
	
	@Override
	protected void createImmediateContent(Composite parent) {
		if (getModel().getChildren().isEmpty()) {
			addChild(new GridElement() {
				@Override
				protected void createImmediateContent(Composite parent) {
					Label message = new Label(parent, SWT.NONE);
					message.setText("None specified");
					new Label(parent, SWT.NONE);
					Label l = new Label(parent, SWT.NONE);
					l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
					new Label(parent, SWT.NONE);
				}
			});
		}
	}
	
	@SuppressWarnings("null")
	@Override
	protected void adjustChildren(Composite parent) {
		
		Label topLabel = null;
		// Cannot be empty since we add a fake element in createImmediateContent.
		assert !getChildElements().isEmpty();
		for (int i = 0; i < getChildElements().size(); ++i) {
			GridElement child = getChildElements().get(i);
			Label l = child.indent();
			topLabel = (topLabel == null) ? l : topLabel;
			
			
			//Button button = createButton(parent, CDebugImages.get(CDebugImages.IMG_LCL_REMOVE_UIELEMENT), "Delete", 1, 1);
			Button button = new Button(parent, SWT.NONE);
			FontAwesome.setFontAwesomeToControl(button);
			button.setText(FontAwesome.FA_TRASH_O);
			
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					//removeButtonPressed(element);
				}
			});
			child.addButton(button);
		}
	
		topLabel.setText(getModel().getName());
		topLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
	}		
	
	protected Button createButton(Composite parent, Image image, String tooltip, int horSpan, int verSpan) {
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, horSpan, verSpan));
		button.setImage(image);
		button.setToolTipText(tooltip);
		return button;
	}
}
