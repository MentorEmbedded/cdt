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
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class ConnectionElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".connection"; //$NON-NLS-1$

	public ConnectionElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Connection", "Connection settings");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof SessionTypeChangeEvent) {
			handleSessionTypeChange((SessionTypeChangeEvent)event);
		}
		super.update(event);
	}
	
	private void handleSessionTypeChange(SessionTypeChangeEvent event) {
		 setEnabled(SessionType.REMOTE.equals(event.getNewType()));
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return super.isEnabled();
	}

	@Override
	public boolean setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		return super.setEnabled(enabled);
	}
}
