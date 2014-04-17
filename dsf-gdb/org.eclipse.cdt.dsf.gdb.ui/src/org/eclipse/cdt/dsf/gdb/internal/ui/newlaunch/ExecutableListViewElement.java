package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.ListPresentationModel;
import org.eclipse.cdt.ui.grid.ListViewElement;
import org.eclipse.cdt.ui.grid.ViewElementFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class ExecutableListViewElement extends ListViewElement {
	
	public ExecutableListViewElement(ListPresentationModel model, ViewElementFactory factory) {
		super(model, factory);
		
		// FIXME: possible link element should be extensibile enough to be used
		// here.
		addChild(new GridElement() {
			
			Label l;
			
			@Override
			protected void createImmediateContent(Composite parent) {
				l = new Label(parent, SWT.NONE);
				new Label(parent, SWT.NONE);
				
				Link link = new Link(parent, SWT.NONE);
				link.setText("New: <a>Run</a>, <a>Attach</a>");
				
				CDTUITools.getGridLayoutData(link).horizontalSpan = 2;
				
				new Label(parent, SWT.NONE);
			}
			
			@Override
			public Label indent() {
				return l;
			}
		});
	}
	
	

}
