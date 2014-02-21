package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.grid.IPresentationModel.Listener;

/**
 * @since 5.7
 */
public class CheckboxViewElement extends GridElement {

	public CheckboxViewElement(IBooleanPresentationModel model) {
		this.model = model;
	}
	
	public CheckboxViewElement labelInContentArea()
	{
		this.labelInContentArea = true;
		return this;
	}

	@Override
	public void createImmediateContent(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		if (!labelInContentArea)
			l.setText(model.getName());
		
		new Label(parent, SWT.NONE);
		
		checkbox = new Button(parent, SWT.CHECK);
		if (labelInContentArea)
			checkbox.setText(model.getName());
		checkbox.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (!blockSignals) { 
					try {
						blockSignals = true;
						model.setValue(checkbox.getSelection());
					} finally {
						blockSignals = false;
					}
				}
			}
		});
		
		model.addAndCallListener(new Listener() {
			@Override
			public void changed(int what, Object object) {
				if (!blockSignals && (what | IPresentationModel.CHANGED) != 0) {
					try {
						blockSignals = true;
						checkbox.setSelection(model.getValue());	
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

	protected void createButton(Composite parent) {
		new Label(parent, SWT.NONE);
	}
	
	private IBooleanPresentationModel model;
	private boolean blockSignals;
	protected Button checkbox;
	protected boolean labelInContentArea;
}
