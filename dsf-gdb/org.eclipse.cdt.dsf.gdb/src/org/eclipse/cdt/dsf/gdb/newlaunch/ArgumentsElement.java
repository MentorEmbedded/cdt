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

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class ArgumentsElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".arguments"; //$NON-NLS-1$
	final private static String ATTR_ARGUMENTS = ".arguments"; //$NON-NLS-1$

	private String fArguments = ""; //$NON-NLS-1$

	public ArgumentsElement(ExecutableElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Arguments", "Arguments");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		try {
			fArguments = config.getAttribute(getId() + ATTR_ARGUMENTS, ""); //$NON-NLS-1$
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_ARGUMENTS, fArguments);
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_ARGUMENTS, "");
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		return true;
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof SessionTypeChangeEvent) {
			handleSessionTypeChange((SessionTypeChangeEvent)event);
		}
		super.update(event);
	}

	private void handleSessionTypeChange(SessionTypeChangeEvent event) {
		 setEnabled(SessionType.LOCAL.equals(event.getNewType()));
	}
	
	public String getArguments() {
		return fArguments;
	}

	public void setArguments(String arguments) {
		fArguments = arguments;
		elementChanged(CHANGE_DETAIL_STATE);
	}
}
