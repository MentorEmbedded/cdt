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
import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class OverviewElement extends AbstractLaunchElement {
	
	public class SessionTypeChangeEvent extends ChangeEvent {

		final private SessionType fNewType;
		final private SessionType fOldType;
		
		public SessionTypeChangeEvent(AbstractLaunchElement source, SessionType newType, SessionType oldType) {
			super(source);
			fNewType = newType;
			fOldType = oldType;
		}

		public SessionType getNewType() {
			return fNewType;
		}

		public SessionType getOldType() {
			return fOldType;
		}
	}

	final private static String ID = GdbPlugin.PLUGIN_ID + ".overview";  //$NON-NLS-1$
	
	private SessionType fSessionType = SessionType.LOCAL;
	private boolean fAttach = false;

	public OverviewElement() {
		super(null, ID, "Overview", "Overview");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
		createExecutablesList(config);
		createCoreFileElements(config);
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		try {
			String debugMode = config.getAttribute(
				ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE, 
				ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);
			SessionType type = SessionType.LOCAL;
			boolean attach = false;
			if (debugMode.equals(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN)) {
				type = SessionType.LOCAL;
			} else if (debugMode.equals(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_ATTACH)) {
				type = SessionType.LOCAL;
				attach = true;
			} else if (debugMode.equals(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_CORE)) {
				type = SessionType.CORE;
			} else if (debugMode.equals(IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE)) {
				type = SessionType.REMOTE;
			} else if (debugMode.equals(IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE_ATTACH)) {
				type = SessionType.REMOTE;
				attach = true;
			}
			setSessionType(type);
			setAttach(attach);
			update(new SessionTypeChangeEvent(this, type, null));
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		SessionType type = getSessionType();
		boolean attach = isAttach();
		String value = ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN;
		if (type == SessionType.LOCAL) {
			value = (attach) ? 
				ICDTLaunchConfigurationConstants.DEBUGGER_MODE_ATTACH : 
				ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN;
		}
		else if (type == SessionType.REMOTE) {
			value = (attach) ? 
				IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE_ATTACH : 
				IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE;
		}
		else if (type == SessionType.CORE) {
			value = ICDTLaunchConfigurationConstants.DEBUGGER_MODE_CORE;
		}
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE, value);
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(
			ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE, 
			ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		return getInternalErrorMessage() == null;
	}

	public SessionType getSessionType() {
		return fSessionType;
	}

	public void setSessionType(SessionType sessionType) {
		if (fSessionType == sessionType)
			return;
		SessionType oldType = fSessionType;
		fSessionType = sessionType;
		update(new SessionTypeChangeEvent(this, sessionType, oldType));
		elementChanged(CHANGE_DETAIL_CONTENT | CHANGE_DETAIL_STATE);
	}

	public boolean isAttach() {
		return fAttach;
	}

	public void setAttach(boolean attach) {
		if (fAttach == attach)
			return;
		fAttach = attach;
		elementChanged(CHANGE_DETAIL_CONTENT | CHANGE_DETAIL_STATE);
	}
	
	protected void createExecutablesList(ILaunchConfiguration config) {
		addChildren(new ILaunchElement[] { new ExecutablesListElement(this) });
	}
	
	protected void createCoreFileElements(ILaunchConfiguration config) {
		addChildren(new ILaunchElement[] { new CoreExecutableElement(this) });
	}
}