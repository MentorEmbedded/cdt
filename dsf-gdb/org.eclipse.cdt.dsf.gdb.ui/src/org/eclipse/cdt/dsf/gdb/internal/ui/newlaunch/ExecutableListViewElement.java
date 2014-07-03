package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.ListViewElement;
import org.eclipse.cdt.ui.grid.ViewElementFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class ExecutableListViewElement extends ListViewElement {
	
	@Override
	public ExecutableListPresentationModel getModel()
	{
		return (ExecutableListPresentationModel)super.getModel();
	}
	
	public ExecutableListViewElement(ExecutableListPresentationModel model, ViewElementFactory factory) {
		super(model, factory);					
	}
	
	@Override
	public void create(Composite parent) {
			
		// TODO Auto-generated method stub
		super.create(parent);
		
		// Create an element to hold new executable actions. This is ugly, in two ways
		// - There should be a way for presentation model to expose actions, maybe as just
		//   a special type of IPresentationModel. After all, view generally should not know
		//   about specifics of model.
		// - It would be nice if the LinkViewElement could handle two links in one element,
		//   but doing this in a clean way might be hard.
		//
		// Ideal short-term solution would be a special composite-actions presentation model,
		// along with default view rendering of same.
		GridElement e = new GridElement() {


			Label l;

			@Override
			protected void createImmediateContent(Composite parent) {
				l = new Label(parent, SWT.NONE);
				new Label(parent, SWT.NONE);

				Link link = new Link(parent, SWT.NONE);
				link.setText("New: <a href=\"run\">Run</a>, <a href=\"attach\">Attach</a>");

				link.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						if (e.text.equals("run"))
							getModel().newRunExecutable();
						else if (e.text.equals("attach"))
							getModel().newAttachExecutable();
					}
				});


				CDTUITools.getGridLayoutData(link).horizontalSpan = 2;

				new Label(parent, SWT.NONE);
			}

			@Override
			protected void indentChildControls() {				
			}	
		};	
		e.create(parent);
		addChild(e);
	}
	
	

}
