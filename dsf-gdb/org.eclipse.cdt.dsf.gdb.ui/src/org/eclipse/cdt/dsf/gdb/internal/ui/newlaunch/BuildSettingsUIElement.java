package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.debug.ui.dialogs.IAttributeStore;
import org.eclipse.cdt.debug.ui.dialogs.UIElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class BuildSettingsUIElement extends UIElement {

	final private static String ELEMENT_ID = ".buildSettings"; //$NON-NLS-1$

	public BuildSettingsUIElement(UIElement parentElement) {
		super(parentElement.getId() + ELEMENT_ID, parentElement, "Build Settings", "BuildSettings");
	}

	@Override
	protected void doCreateSummaryContent(Composite parent, IAttributeStore store) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Build automatically");
	}

	@Override
	protected boolean isRemovable() {
		return false;
	}
}
