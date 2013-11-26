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

package org.eclipse.cdt.dsf.gdb.newlaunch;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.core.launch.ListLaunchElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @since 4.3
 */
public class ExecutablesListElement extends ListLaunchElement {

	final private static String ELEMENT_ID = ".executableList"; //$NON-NLS-1$

	public ExecutablesListElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Executables", "Executables");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
		try {
			String programName = config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, ""); //$NON-NLS-1$
			if (!programName.isEmpty()) {
				addChildren(new ILaunchElement[] { new ExecutableElement(this, 0) });
			}
		}
		catch(CoreException e) {
		}		
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		return true;
	}

	@Override
	public void addNewElement() {
		ExecutableElement element = new ExecutableElement(this, getChildren().length);
		doInsertChild(getChildren().length, element);
		elementAdded(element, ADD_DETAIL_ACTIVATE);
	}
}
