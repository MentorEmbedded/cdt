package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.grid.IPresentationModel.Listener;

/**
 * @since 5.7
 */
public class DropdownSelectionViewElement extends ViewElement {

	public DropdownSelectionViewElement(ISelectionPresentationModel model) {
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
		
		combo = new Combo(parent, SWT.NONE);
		String[] items = getModel().getPossibleValues().toArray(new String[0]);
		for (String item: items) {
			combo.add(item);
		}
				
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!blockSignals)
					getModel().setValue(getModel().getPossibleValues().get(combo.getSelectionIndex()));
			}
		});
		
		CDTUITools.getGridLayoutData(combo).horizontalSpan = 2;
		CDTUITools.grabAllWidth(combo);
		
		new Label(parent, SWT.NONE);
	}
	
	@Override
	protected void modelChanged(int what, Object object) {
		if ((what & IPresentationModel.VALUE_CHANGED) != 0) {
			try {
				blockSignals = true;
				combo.select(getModel().getPossibleValues().indexOf(getModel().getValue()));
			} finally {
				blockSignals = false;
			}
		}
	}
	
	private boolean blockSignals;
	private Combo combo; 
}
