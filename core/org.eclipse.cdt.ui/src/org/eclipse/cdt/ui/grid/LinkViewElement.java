package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import org.eclipse.cdt.ui.CDTUITools;

/** Displays a StringPresentationModel as a link in the
 *  third/fourth columns of the grid layout.
 *  @since 5.7
 */
public class LinkViewElement extends GridElement {
	
	public LinkViewElement(IStringPresentationModel model)
	{
		this.model = model;
	}

	@Override
	public void createImmediateContent(Composite parent) {
		
		new Label(parent, SWT.NONE);
		
		new Label(parent, SWT.NONE);
				
		final Link link = new Link(parent, SWT.NONE);
		CDTUITools.getGridLayoutData(link).horizontalSpan = 2;
		CDTUITools.grabAllWidth(link);
		
		model.addAndCallListener(new IPresentationModel.Listener() {
			
			@Override
			public void changed(int what, Object object) {
				// Handling of the link click might have disposed
				// us.
				if (!link.isDisposed())
					link.setText("<a>" + model.getValue() + "</a>");				
			}
		});
		
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.activate();
			}
		});
		
		new Label(parent, SWT.NONE);
	}
	
	IStringPresentationModel model;
}
