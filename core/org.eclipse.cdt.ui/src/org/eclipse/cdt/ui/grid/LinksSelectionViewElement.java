package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import org.eclipse.cdt.ui.CDTUITools;

/** View element that renders selection model as a set of links.
 * 
 * WARNING: this element does not support changing the model. It only
 * displays links, leaving it to other code to handle clicks.
 * @since 6.0
 */
public class LinksSelectionViewElement extends GridElement {

	public LinksSelectionViewElement(ISelectionPresentationModel model) {
		this.model = model;
	}
	
	@Override
	public void createImmediateContent(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(model.getName());
		
		Label spacer = new Label(parent, SWT.NONE);
		
		Composite links = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		links.setLayout(layout);
		for (final String item: model.getPossibleValues()) {
			Link link = new Link(links, SWT.NONE);
			link.setText("<a>" + item + "</a>");
			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					model.setValue(item);
				}
			});
		}
		
		
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
		
		CDTUITools.getGridLayoutData(links).horizontalSpan = 2;
		CDTUITools.grabAllWidth(links);
		
		Label spacer2 = new Label(parent, SWT.NONE);
	}
	
	private ISelectionPresentationModel model;
	private boolean blockSignals; 
}
