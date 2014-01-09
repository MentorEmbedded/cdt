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

import java.util.Map;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class RemoteBinaryElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".remoteBinary"; //$NON-NLS-1$
	final private static String ATTR_REMOTE_PATH = ".remotePath"; //$NON-NLS-1$

	private String fRemotePath = ""; //$NON-NLS-1$

	public RemoteBinaryElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Binary on target", "Binary path on target");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
	}

	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		fRemotePath = getAttribute(attributes, getId() + ATTR_REMOTE_PATH, ""); //$NON-NLS-1$
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_REMOTE_PATH, fRemotePath);
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_REMOTE_PATH, ""); //$NON-NLS-1$
	}

	@Override
	protected boolean isContentValid() {
		if (fRemotePath == null || fRemotePath.isEmpty()) {
			setErrorMessage("Remote binary must be specified");
			return false;
		}
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
		 setEnabled(SessionType.REMOTE.equals(event.getNewType()));
	}

	public String getRemotePath() {
		return fRemotePath;
	}

	public void setRemotePath(String remotePath) {
		fRemotePath = remotePath;
		elementChanged(CHANGE_DETAIL_STATE);
	}
}
