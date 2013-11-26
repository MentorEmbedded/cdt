/*******************************************************************************
 * Copyright (c) 2013 Mentor Graphics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mentor Graphics - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.debug.ui.dialogs.SingleLinkUIElement;
import org.eclipse.cdt.debug.ui.dialogs.UIElement;

public class AddExecutableElement extends SingleLinkUIElement {

	final private static String ELEMENT_ID = ".addExecutable"; //$NON-NLS-1$

	public AddExecutableElement(UIElement parentElement) {
		super(parentElement.getId() + ELEMENT_ID, parentElement, "Add Executable", "Add a new executable to the launch");
	}

	@Override
	protected void linkActivated() {
		ExecutableUIElement newElement = new ExecutableUIElement(getParent());
		getParent().insertChild(this, newElement);
		getChangeListener().elementAdded(newElement);
	}
}
