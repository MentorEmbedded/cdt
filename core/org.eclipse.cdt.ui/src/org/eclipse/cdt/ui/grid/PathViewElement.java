package org.eclipse.cdt.ui.grid;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.cdt.ui.CDTSharedImages;

/**
 * @since 5.7
 */
public class PathViewElement extends StringViewElement {
	
	public PathViewElement(IStringPresentationModel model) {
		super(model);
	}
	
	@Override
	protected void createButton(final Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		// FIXME: use proper icon.
		button.setImage(CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_SEARCH_REF));
		button.setToolTipText("Browse");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				Shell shell = parent.getShell();
				
				FileDialog dialog = new FileDialog(shell, SWT.NONE);
				dialog.setText("Browse"); // FIXME:
				String current = text.getText().trim();
				int lastSeparatorIndex = current.lastIndexOf(File.separator);
				if (lastSeparatorIndex != -1) {
					dialog.setFilterPath(current.substring(0, lastSeparatorIndex));
				}
				String res = dialog.open();
				if (res == null) {
					return;
				}
				text.setText(res);				
			}
		});
	}
}
