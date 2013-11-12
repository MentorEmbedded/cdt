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

import org.eclipse.cdt.debug.ui.dialogs.UIElement;

public class SharedLibrariesElement extends UIElement {

	final private static String ELEMENT_ID = ".sharedLibraries"; //$NON-NLS-1$

	public SharedLibrariesElement(UIElement parentElement) {
		super(parentElement.getId() + ELEMENT_ID, parentElement, "SharedLibraries", "Shared Libraries");
	}
}
