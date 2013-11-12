package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.debug.ui.dialogs.UIElement;
import org.eclipse.cdt.debug.ui.dialogs.SingleLinkUIElement;

/**
 * @since 7.4
 */
public class AttachToProcessUIElement extends SingleLinkUIElement {

	final private static String ELEMENT_ID = ".attachToProcess"; //$NON-NLS-1$

	public AttachToProcessUIElement(UIElement parentElement) {
		super(parentElement.getId() + ELEMENT_ID, parentElement, "Attach to process", "Attach to a running process");
	}
}
