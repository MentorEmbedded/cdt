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

import java.io.File;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class CoreFileElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".coreFile"; //$NON-NLS-1$

	public enum CoreFileType {
		CORE_FILE,
		TRACE_FILE
	}

	private CoreFileType fType = CoreFileType.CORE_FILE;	
	private String fCoreFile;

	public CoreFileElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Core File", "Core file");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		try {
			String coreType = config.getAttribute(
				IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_POST_MORTEM_TYPE,
				IGDBLaunchConfigurationConstants.DEBUGGER_POST_MORTEM_TYPE_DEFAULT);
			if (IGDBLaunchConfigurationConstants.DEBUGGER_POST_MORTEM_CORE_FILE.equals(coreType)) {
				fType = CoreFileType.CORE_FILE;
			}
			else if (IGDBLaunchConfigurationConstants.DEBUGGER_POST_MORTEM_TRACE_FILE.equals(coreType)) {
				fType = CoreFileType.TRACE_FILE;
			}
			else {
				setErrorMessage("Invalid core file type");
			}
			
			fCoreFile = config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_COREFILE_PATH, ""); //$NON-NLS-1$
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		if (fType == CoreFileType.CORE_FILE) {
			config.setAttribute(
				IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_POST_MORTEM_TYPE,
				IGDBLaunchConfigurationConstants.DEBUGGER_POST_MORTEM_CORE_FILE);
		}
		else if (fType == CoreFileType.TRACE_FILE) {
			config.setAttribute(
				IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_POST_MORTEM_TYPE,
				IGDBLaunchConfigurationConstants.DEBUGGER_POST_MORTEM_TRACE_FILE);
		}
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_COREFILE_PATH, fCoreFile);
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(
			IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_POST_MORTEM_TYPE,
			IGDBLaunchConfigurationConstants.DEBUGGER_POST_MORTEM_TYPE_DEFAULT);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_COREFILE_PATH, ""); //$NON-NLS-1$
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		if (fCoreFile == null || fCoreFile.isEmpty()) {
			return true;
		}
		String coreName = fCoreFile;
		try {
			// Replace the variables
			coreName = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(coreName, false);
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
			return false;
		}
		
		coreName = coreName.trim();
		File filePath = new File(coreName);
		if (!filePath.isDirectory()) {
			IPath corePath = new Path(coreName);
			if (!corePath.toFile().exists()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.File_does_not_exist")); //$NON-NLS-1$
				return false;
			}
		}
		else {
			setErrorMessage(LaunchMessages.getString("CMainTab.File_does_not_exist")); //$NON-NLS-1$
			return false;
		}
		return false;
	}

	public CoreFileType getCoreFileType() {
		return fType;
	}

	public String getCoreFile() {
		return fCoreFile;
	}

	public void setType(CoreFileType type) {
		if (fType == type)
			return;
		fType = type;
		elementChanged(CHANGE_DETAIL_STATE);
	}
	
	public void setCoreFile(String coreFile) {
		fCoreFile = coreFile;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof SessionTypeChangeEvent) {
			handleSessionTypeChange((SessionTypeChangeEvent)event);
		}
		super.update(event);
	}
	
	private void handleSessionTypeChange(SessionTypeChangeEvent event) {
		 setEnabled(SessionType.CORE.equals(event.getNewType()));
	}
}
