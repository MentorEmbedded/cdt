package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import org.eclipse.cdt.ui.CDTUITools;

public class LinksElement implements IGridElement {
	
	public LinksElement(String[] names)
	{
		this.names = names;
	}

	@Override
	public void fillIntoGrid(Composite parent) {
		
		// Place links in columns, leaving last column unoccupied, since
		// it's normally used to store buttons, and putting a link
		// there will mess up layout.
		for (int i = 0; i < names.length; ++i)
		{
			Link link = new Link(parent, SWT.NONE);
			link.setText("<a>" + names[i] + "</a>");
			
			int column = (i % (IGridElement.DEFAULT_WIDTH - 1));
			if (column == 0) {
				CDTUITools.getGridLayoutData(link).horizontalIndent = 12;
			}
				
			
			if (column == IGridElement.DEFAULT_WIDTH - 2) {
				new Label(parent, SWT.NONE);
			}
		}
	}
	
	private String[] names;

}
