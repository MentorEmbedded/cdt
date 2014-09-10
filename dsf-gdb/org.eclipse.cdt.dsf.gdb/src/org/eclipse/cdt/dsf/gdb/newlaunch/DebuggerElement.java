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
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.core.runtime.Platform;

/**
 * @since 4.6
 */
public class DebuggerElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".debugger"; //$NON-NLS-1$
	final private static String ATTR_GDB_PATH = ".gdbPath"; //$NON-NLS-1$
	final private static String ATTR_GDB_INIT_FILE = ".gdbInitFile"; //$NON-NLS-1$

	private String fGDBPath = getDefaultGDBPath();
	private String fGDBInitFile = getDefaultGDBInitFile();

	public DebuggerElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Debugger", "Debugger");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
		addChildren(new ILaunchElement[] {
			new ConnectionElement(this),
			new DebuggerSettingsElement(this),
			/*new SharedLibrariesElement(this)*/
		});
	}
	
	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		fGDBPath = getAttribute(attributes, getId() + ATTR_GDB_PATH, getDefaultGDBPath());
		fGDBInitFile = getAttribute(attributes, getId() + ATTR_GDB_INIT_FILE, getDefaultGDBInitFile());
	}

	@Override
	protected void doPerformApply(Map<String, Object> attributes) {
		attributes.put(getId() + ATTR_GDB_PATH, fGDBPath);
		attributes.put(getId() + ATTR_GDB_INIT_FILE, fGDBInitFile);
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		fGDBPath = getDefaultGDBPath();
		fGDBInitFile = getDefaultGDBInitFile();
		attributes.put(getId() + ATTR_GDB_PATH, fGDBPath);
		attributes.put(getId() + ATTR_GDB_INIT_FILE, fGDBInitFile);
	}

	@Override
	protected boolean isContentValid() {
		setErrorMessage(null);
		if (fGDBPath.isEmpty()) {
			setErrorMessage("GDB path must be specified");
			return false;
		}
		return true;
	}

	public static String getDefaultGDBPath() {
		return Platform.getPreferencesService().getString(
			GdbPlugin.PLUGIN_ID, 
			IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_COMMAND, 
			IGDBLaunchConfigurationConstants.DEBUGGER_DEBUG_NAME_DEFAULT,
			null);
	}
	
	public static String getDefaultGDBInitFile() {
		return Platform.getPreferencesService().getString(
			GdbPlugin.PLUGIN_ID, 
			IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_INIT, 
			IGDBLaunchConfigurationConstants.DEBUGGER_GDB_INIT_DEFAULT,
			null);
	}

	public String getGDBPath() {
		return fGDBPath;
	}

	public void setGDBPath(String path) {
		if (fGDBPath == null || !fGDBPath.equals(path)) {
			fGDBPath = path;
			elementChanged(CHANGE_DETAIL_STATE);
		}
	}

	public String getGDBInitFile() {
		return fGDBInitFile;
	}

	public void setGDBInitFile(String initFile) {
		if (fGDBInitFile == null || !fGDBInitFile.equals(initFile)) {
			fGDBInitFile = initFile;
			elementChanged(CHANGE_DETAIL_STATE);
		}
	}
}
