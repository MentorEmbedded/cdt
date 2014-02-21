package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.dialogs.PillsControl;
import org.eclipse.cdt.ui.grid.IPresentationModel.Listener;

/**
 * @since 5.7
 */
public class PillSelectionViewElement extends GridElement {

	public PillSelectionViewElement(ISelectionPresentationModel model) {
		this.model = model;
	}
	
	@Override
	public void createImmediateContent(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(model.getName());
		
		Label spacer = new Label(parent, SWT.NONE);
		
		final PillsControl pills = new PillsControl(parent, SWT.NONE);
		pills.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		String[] items = model.getPossibleValues().toArray(new String[0]);
		pills.setItems(items);
				
		pills.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!blockSignals)
					model.setValue(model.getPossibleValues().get(e.detail));
			}
		});
		
		model.addAndCallListener(new Listener() {
			@Override
			public void changed(int what, Object object) {
				if ((what | IPresentationModel.CHANGED)!= 0) {
					try {
						blockSignals = true;
						pills.setSelection(model.getPossibleValues().indexOf(model.getValue()));
					} finally {
						blockSignals = false;
					}
				}
			}
		});
		
		CDTUITools.getGridLayoutData(pills).horizontalSpan = 2;
		CDTUITools.grabAllWidth(pills);
		
		Label spacer2 = new Label(parent, SWT.NONE);
	}
	
	private ISelectionPresentationModel model;
	private boolean blockSignals; 
}
