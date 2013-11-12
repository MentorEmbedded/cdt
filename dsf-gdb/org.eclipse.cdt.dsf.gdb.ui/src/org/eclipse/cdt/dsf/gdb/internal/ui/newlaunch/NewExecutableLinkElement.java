package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.debug.ui.dialogs.IAttributeStore;
import org.eclipse.cdt.debug.ui.dialogs.UIElement;

/**
 * @since 7.4
 */
public class NewExecutableLinkElement extends ExecutableUIElement {

	public NewExecutableLinkElement(UIElement parentElement) {
		super(parentElement, "Add Executable", "Add a new executable to the launch");
	}

	@Override
	protected boolean hasContent() {
		return false;
	}

	@Override
	protected void doInitializeFrom(IAttributeStore store) {
	}
}
