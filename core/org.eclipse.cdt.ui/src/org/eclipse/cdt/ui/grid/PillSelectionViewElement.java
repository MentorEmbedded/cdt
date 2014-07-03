package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.dialogs.PillsControl;

/**
 * @since 5.7
 */
public class PillSelectionViewElement extends ViewElement {

	public PillSelectionViewElement(ISelectionPresentationModel model) {
		super(model);
	}
	
	@Override
	public ISelectionPresentationModel getModel() {
		return (ISelectionPresentationModel) super.getModel();
	}
	
	@Override
	public void createImmediateContent(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(getModel().getName());
		
		new Label(parent, SWT.NONE);
		
		pills = new PillsControl(parent, SWT.NONE);
		pills.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		String[] items = getModel().getPossibleValues().toArray(new String[0]);
		pills.setItems(items);
				
		pills.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!blockSignals)
					getModel().setValue(getModel().getPossibleValues().get(e.detail));
			}
		});
		
		CDTUITools.getGridLayoutData(pills).horizontalSpan = 2;
		CDTUITools.grabAllWidth(pills);
		
		new Label(parent, SWT.NONE);		
	}
	
	@Override
	protected void modelChanged(int what, Object object) {
		if ((what & IPresentationModel.VALUE_CHANGED)!= 0) {
			try {
				blockSignals = true;
				pills.setSelection(getModel().getPossibleValues().indexOf(getModel().getValue()));
			} finally {
				blockSignals = false;
			}
		}
	}
	
	private boolean blockSignals;
	private PillsControl pills; 
}
