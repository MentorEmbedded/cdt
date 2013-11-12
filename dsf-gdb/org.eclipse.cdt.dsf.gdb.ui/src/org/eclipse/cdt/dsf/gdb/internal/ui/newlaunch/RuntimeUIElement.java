package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.ui.dialogs.IAttributeStore;
import org.eclipse.cdt.debug.ui.dialogs.UIElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class RuntimeUIElement extends UIElement {

	final private static String ELEMENT_ID = ".runtime"; //$NON-NLS-1$
	
	private Label fArguments;
	private Label fWorkDir;

	public RuntimeUIElement(UIElement parentElement) {
		super(parentElement.getId() + ELEMENT_ID, parentElement, "Runtime", "Runtime settings");
	}

	@Override
	protected void doCreateSummaryContent(Composite parent, IAttributeStore store) {
		Composite base = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = layout.marginWidth = 0;
		base.setLayout(layout);
		base.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label label = new Label(base, SWT.NONE);
		label.setText("Arguments: ");		
		fArguments = new Label(base, SWT.NONE);

		label = new Label(base, SWT.NONE);
		label.setText("Working directory: ");		
		fWorkDir = new Label(base, SWT.NONE);
	}

	@Override
	protected boolean isRemovable() {
		return false;
	}

	@Override
	protected int numberOfRowsInSummary() {
		return 2;
	}

	@Override
	protected void doInitializeFrom(IAttributeStore store) {
		if (fArguments != null) {
			try {
				fArguments.setText(store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "")); //$NON-NLS-1$
			}
			catch(CoreException e) {
				getStatusListener().errorReported(e.getLocalizedMessage());
			}
		}
		if (fWorkDir != null) {
			try {
				String wd = store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, ""); //$NON-NLS-1$
				if (wd.isEmpty()) {
					wd = "using default";
				}
				fWorkDir.setText(wd);
			}
			catch(CoreException e) {
				getStatusListener().errorReported(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fArguments = null;
		fWorkDir = null;
	}

	@Override
	protected void remove(IAttributeStore store) {
		super.remove(store);
		store.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, ""); //$NON-NLS-1$
		store.setAttribute(ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, ""); //$NON-NLS-1$
	}
}