package org.eclipse.cdt.flatui.tests.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipset.cdt.flatui.FA;
import org.eclipset.cdt.flatui.FlatUI;

public class FontAwesomeDemoHandler extends AbstractHandler {

	public FontAwesomeDemoHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		Dialog d = new Dialog(window.getShell()) {

			protected void configureShell(Shell shell) {
				super.configureShell(shell);
				shell.setText("FontAwesome demo");
			}

			@Override
			protected Control createDialogArea(Composite parent) {
				Composite composite = (Composite)super.createDialogArea(parent);

				Composite fa = new Composite(composite, SWT.NONE);

				GridLayout layout = new GridLayout(2, false);
				layout.marginHeight = layout.marginWidth = 0;
				fa.setLayout(layout);

				Label l = new Label(fa, SWT.NONE);
				l.setText("FontAwesome");
				l.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				l.setFont(FlatUI.getBold(l));

				Label l2 = new Label(fa, SWT.NONE);
				l2.setText("\uf197");
				l2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				FA.apply(l2);


				return composite;
			}

			protected void createButtonsForButtonBar(Composite parent) {
				createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
			}

		};

		d.open();
		return null;
	}
}
