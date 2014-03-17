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
public class DropdownSelectionViewElement extends GridElement {

	public DropdownSelectionViewElement(ISelectionPresentationModel model) {
		this.model = model;
	}
	
	@Override
	public void createImmediateContent(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(model.getName());
		
		new Label(parent, SWT.NONE);
		
		final Combo combo = new Combo(parent, SWT.NONE);
		String[] items = model.getPossibleValues().toArray(new String[0]);
		for (String item: items) {
			combo.add(item);
		}
				
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!blockSignals)
					model.setValue(model.getPossibleValues().get(combo.getSelectionIndex()));
			}
		});
		
		CDTUITools.getGridLayoutData(combo).horizontalSpan = 2;
		CDTUITools.grabAllWidth(combo);
		
		new Label(parent, SWT.NONE);
				
		model.addAndCallListener(new Listener() {
			@Override
			public void changed(int what, Object object) {
				if ((what & IPresentationModel.VALUE_CHANGED) != 0) {
					try {
						blockSignals = true;
						combo.select(model.getPossibleValues().indexOf(model.getValue()));
					} finally {
						blockSignals = false;
					}
				}
				
				if ((what & IPresentationModel.VISIBILITY_CHANGED) != 0) 
					setVisible(model.isVisible());
			}
		});		
	}
	
	private ISelectionPresentationModel model;
	private boolean blockSignals; 
}
