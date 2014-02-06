package org.eclipse.cdt.ui.grid;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.dialogs.PillsControl;
import org.eclipse.cdt.ui.grid.IPresentationModel.Listener;

public class PillSelectionViewElement implements IGridElement {

	public PillSelectionViewElement(ISelectionPresentationModel model) {
		this.model = model;
	}
	
	@Override
	public void fillIntoGrid(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(model.getName());
		
		Label spacer = new Label(parent, SWT.NONE);
		
		PillsControl pills = new PillsControl(parent, SWT.NONE);
		pills.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		String[] items = model.getPossibleValues().toArray(new String[0]);
		pills.setItems(items);
		
		/*
		
		final Text t = new Text(parent, SWT.BORDER);
		t.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				blockSignals = true;
				model.setValue(t.getText());
				blockSignals = false;
			}
		});
		
		model.addAndCallListener(new Listener() {
			@Override
			public void changed(int what, Object object) {
				if ((what | IPresentationModel.CHANGED) != 0) {
					if (!blockSignals)
						t.setText(model.getValue());
				}
			}
		}); */
		
		CDTUITools.getGridLayoutData(pills).horizontalSpan = 2;
		CDTUITools.grabAllWidth(pills);
		
		Label spacer2 = new Label(parent, SWT.NONE);
	}
	
	private ISelectionPresentationModel model;
	private boolean blockSignals; 
}
