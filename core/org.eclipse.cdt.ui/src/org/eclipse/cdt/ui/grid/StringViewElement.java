package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.cdt.ui.CDTUITools;

/**
 * @since 5.7
 */
public class StringViewElement extends ViewElement {

	public StringViewElement(IStringPresentationModel model) {
		super(model);
	}
	
	@Override
	public IStringPresentationModel getModel()
	{
		return (IStringPresentationModel) super.getModel();
	}
	
	@Override
	public void createImmediateContent(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(getModel().getName());
		
		new Label(parent, SWT.NONE);
						
		text = new Text(parent, SWT.BORDER);
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if (!blockSignals) { 
					try {
						blockSignals = true;
						getModel().setValue(text.getText());
					} finally {
						blockSignals = false;
					}
				}
			}
		});
		
		CDTUITools.getGridLayoutData(text).horizontalSpan = 2;
		CDTUITools.grabAllWidth(text);
		
		createButton(parent);
	}
	
	@Override
	protected void modelChanged(int what, Object object) {
		if (blockSignals)
			return;
		
		if ((what & IPresentationModel.VALUE_CHANGED) != 0) {
			try {
				blockSignals = true;
				text.setText(getModel().getValue());	
			} finally {
				blockSignals = false;
			}
		}		
	}

	protected void createButton(Composite parent) {
		new Label(parent, SWT.NONE);
	}
	
	private boolean blockSignals;
	protected Text text; 	
}
