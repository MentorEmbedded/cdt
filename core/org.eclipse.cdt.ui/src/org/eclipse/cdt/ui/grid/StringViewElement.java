package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.grid.IPresentationModel.Listener;

/**
 * @since 5.7
 */
public class StringViewElement extends GridElement {

	public StringViewElement(IStringPresentationModel model) {
		this.model = model;
	}
	
	public void indentLabel()
	{
		this.indentLabel = true;
	}

	@Override
	public void createImmediateContent(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		if (!indentLabel)
			l.setText(model.getName());
		
		new Label(parent, SWT.NONE);
		
		if (indentLabel)
		{
			Label l2 = new Label(parent, SWT.NONE);
			l2.setText(model.getName());
		}
		
		text = new Text(parent, SWT.BORDER);
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if (!blockSignals) { 
					try {
						blockSignals = true;
						model.setValue(text.getText());
					} finally {
						blockSignals = false;
					}
				}
			}
		});
		
		if (!indentLabel)
			CDTUITools.getGridLayoutData(text).horizontalSpan = 2;
		CDTUITools.grabAllWidth(text);
		
		createButton(parent);
					
		modelListener = new Listener() {
			@Override
			public void changed(int what, Object object) {
				if (blockSignals)
					return;
				
				if ((what & IPresentationModel.VALUE_CHANGED) != 0) {
					try {
						blockSignals = true;
						text.setText(model.getValue());	
					} finally {
						blockSignals = false;
					}
				}
				
				if ((what & IPresentationModel.VISIBILITY_CHANGED) != 0) 
					setVisible(model.isVisible());
			}
		};
		model.addAndCallListener(modelListener);			
	}
	
	
	@Override
	public void dispose() {
		model.removeListener(modelListener);
		super.dispose();
	}	

	protected void createButton(Composite parent) {
		new Label(parent, SWT.NONE);
	}
	
	private IStringPresentationModel model;
	private boolean blockSignals;
	protected Text text; 
	protected boolean indentLabel;
	private Listener modelListener;
}
