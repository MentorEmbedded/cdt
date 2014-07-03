package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;

/**
 * @since 5.7
 */
public class CheckboxViewElement extends ViewElement {

	public CheckboxViewElement(IBooleanPresentationModel model) {
		super(model);
	}
	
	@Override
	public IBooleanPresentationModel getModel() {
		return (IBooleanPresentationModel)super.getModel();
	}
	
	public CheckboxViewElement labelInContentArea()
	{
		this.labelInContentArea = true;
		return this;
	}

	@Override
	public void createImmediateContent(Composite parent) {
		
		label = new Label(parent, SWT.NONE);
		if (!labelInContentArea)
			label.setText(getModel().getName());
		
		new Label(parent, SWT.NONE);
		
		checkbox = new Button(parent, SWT.CHECK);
		if (labelInContentArea)
			checkbox.setText(getModel().getName());
		checkbox.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (!blockSignals) { 
					try {
						blockSignals = true;
						getModel().setValue(checkbox.getSelection());
					} finally {
						blockSignals = false;
					}
				}
			}
		});
				
		CDTUITools.getGridLayoutData(checkbox).horizontalSpan = 2;
		CDTUITools.grabAllWidth(checkbox);
		
		createButton(parent);
	}
	
	@Override
	protected void modelChanged(int what, Object object) {

		if (!blockSignals && (what | IPresentationModel.VALUE_CHANGED) != 0) {
			try {
				blockSignals = true;
				checkbox.setSelection(getModel().getValue());	
			} finally {
				blockSignals = false;
			}
		}	
	}
	
	@Override
	public void indent() {
		Label result = new Label(label.getParent(), SWT.NONE);
		result.moveAbove(label);
		label.dispose();
		checkbox.setText(getModel().getName());
	}

	protected void createButton(Composite parent) {
		new Label(parent, SWT.NONE);
	}
	
	private boolean blockSignals;
	protected Button checkbox;
	protected boolean labelInContentArea;
	private Label label;
}
