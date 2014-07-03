package org.eclipse.cdt.ui.grid;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.FontAwesome;

/**
 * @since 5.7
 */
public class ListViewElement extends ViewElement {
	
	private GridElement placeholder;
	
	private ViewElementFactory factory;
	private Map<IPresentationModel, GridElement> elementForModel = new HashMap<IPresentationModel, GridElement>();
	
	public ListViewElement(final ListPresentationModel model, ViewElementFactory factory)
	{
		super(model);
		this.factory = factory;
		
	}
		
	@Override
	public void create(Composite parent) {
		
		this.parent = parent;
		
		final ListPresentationModel model = getModel();
		
		for (IPresentationModel c: model.getChildren())
			modelChildAdded(c);
		
		addChild(placeholder = new ViewElement(model) {
					
			@Override
			protected void modelChildAdded(IPresentationModel child) {
				updateVisibility();
			}
			
			@Override
			protected void modelChildRemoved(IPresentationModel child) {
				updateVisibility();
			}
			
			@Override
			protected void modelVisibilityChanged(boolean visible) {
				updateVisibility();
			}
			
			private void updateVisibility()
			{
				setVisible(model.isVisible() && model.getChildren().size() == 0);
			}
						
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
		
		placeholder.create(parent);
		placeholder.setIndented(true);
		assert placeholder.getTopLeftLabel() != null;
		addLabel(placeholder);
		
		setupListener();
	}
	
	@Override
	protected void modelChildAdded(final IPresentationModel child)
	{
		GridElement e = factory.create(child, parent);
		elementForModel.put(child, e);
		addChild(e);
		
		e.setIndented(true);
		
		if (elementForModel.size() == 1)
			addLabel(e);

		// Create composite for control buttons.
		Composite composite = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		
		Button button;
		
		button = new Button(composite, SWT.NONE);
		FontAwesome.setFontAwesomeToControl(button);
		button.setText(FontAwesome.FA_ARROW_UP);
		
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getModel().moveUp(child);
			}
		});			

		button = new Button(composite, SWT.NONE);
		FontAwesome.setFontAwesomeToControl(button);
		button.setText(FontAwesome.FA_ARROW_DOWN);
		
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getModel().moveDown(child);
			}
		});
		
		//Button button = createButton(parent, CDebugImages.get(CDebugImages.IMG_LCL_REMOVE_UIELEMENT), "Delete", 1, 1);
		button = new Button(composite, SWT.NONE);
		FontAwesome.setFontAwesomeToControl(button);
		button.setText(FontAwesome.FA_TRASH_O);
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getModel().remove(child);
			}
		});
		
		e.setButton(composite);
	}
	
	@Override
	protected void modelChildRemoved(IPresentationModel child)
	{
		GridElement e = elementForModel.get(child);
		if (e != null) {
			e.dispose();
			elementForModel.remove(child);
		}	
	}

	protected void addLabel(GridElement e)
	{
		Label topLabel = e.getTopLeftLabel();
		topLabel.setText(getModel().getName());
		topLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
	}	

	
	@Override
	public ListPresentationModel getModel() {
		return (ListPresentationModel) super.getModel();
	}
	
	@Override
	protected void createImmediateContent(Composite parent) {
	}
		
	protected Button createButton(Composite parent, Image image, String tooltip, int horSpan, int verSpan) {
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, horSpan, verSpan));
		button.setImage(image);
		button.setToolTipText(tooltip);
		return button;
	}
}
