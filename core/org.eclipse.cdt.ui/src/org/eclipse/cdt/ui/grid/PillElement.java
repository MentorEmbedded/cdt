package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.dialogs.PillsControl;

public class PillElement implements IGridElement {
	
	public PillElement(String[] items)
	{
		this.items = items;
	}
	
	public void setBackground(Color color)
	{
		this.background = color;
	}

	@Override
	public void fillIntoGrid(Composite parent) {
		
		PillsControl control = new PillsControl(parent, SWT.NONE);
		control.setItems(items);
		control.setSelection(0);
		if (background != null)
			control.setBackground(background);
		CDTUITools.grabAllWidth(control);
		CDTUITools.getGridLayoutData(control).horizontalSpan = IGridElement.DEFAULT_WIDTH;
		
		
		
		// TODO Auto-generated method stub

	}
	
	private String[] items;
	private Color background;

}
