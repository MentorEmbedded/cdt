package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.cdt.ui.grid.IPresentationModelString.ValueListener;

public class StringViewElement implements IGridElement {

	public StringViewElement(IPresentationModelString model) {
		this.model = model;
	}

	@Override
	public void fillIntoGrid(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(model.getName());
		
		final Text t = new Text(parent, SWT.BORDER);
		t.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				blockSignals = true;
				model.setValue(t.getText());
				blockSignals = false;
			}
		});
		
		model.setValueListener(new ValueListener() {
			@Override
			public void value(String value) {
				// Maybe need to do this in modifyText instead?
				if (!blockSignals) {
					if (value == null) 
						value = "";
					t.setText(value);
				}
			}
		});
	}
	
	private IPresentationModelString model;
	private boolean blockSignals; 
}
