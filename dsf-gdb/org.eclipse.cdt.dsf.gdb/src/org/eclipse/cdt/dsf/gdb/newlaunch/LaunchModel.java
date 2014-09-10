/*******************************************************************************
 * Copyright (c) 2014 Mentor Graphics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mentor Graphics - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.dsf.gdb.newlaunch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionType;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * Helper class to create and access a launch model.
 * 
 * @since 4.6
 */
public class LaunchModel {

	final private ILaunchElement fRootElement;

	public static LaunchModel create(Map<String, Object> attributes) throws CoreException {
		LaunchModel model = new LaunchModel();
		model.fRootElement.initialiazeFrom(attributes);
		if (!model.fRootElement.isValid()) {
			String errorMessage = model.getErrorMessage();
			model.dispose();
			throw new CoreException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, errorMessage));
		}
		return model;
	}

	protected LaunchModel() {
		super();
		fRootElement = new OverviewElement();
	}

	public void dispose() {
		fRootElement.dispose();
	}

	public String getErrorMessage() {
		return fRootElement.getErrorMessage();
	}

	public SessionType getSessionType() {
		OverviewElement overview = fRootElement.findChild(OverviewElement.class);
		return (overview != null) ? overview.getSessionType() : SessionType.LOCAL;
	}

	public String getGDBPath() {
		DebuggerElement debugger = fRootElement.findChild(DebuggerElement.class);
		return (debugger != null) ? debugger.getGDBPath() : DebuggerElement.getDefaultGDBPath();
	}
	
	public String getGDBInitFile() {
		DebuggerElement debugger = fRootElement.findChild(DebuggerElement.class);
		return (debugger != null) ? debugger.getGDBInitFile() : DebuggerElement.getDefaultGDBInitFile();
	}

	public String[] getEnvironment() {
		return new String[0];
	}

	public boolean isNonStop() {
		boolean defNonStop = Platform.getPreferencesService().getBoolean(
				GdbPlugin.PLUGIN_ID, IGdbDebugPreferenceConstants.PREF_DEFAULT_NON_STOP, false, null);
		DebuggerSettingsElement debuggerSettings = fRootElement.findChild(DebuggerSettingsElement.class);
		return (debuggerSettings != null && debuggerSettings.isEnabled()) ? debuggerSettings.isNonStop() : defNonStop;
	}

	public boolean isPostMortemTracing() {
		return false;
	}
	
	public String getGDBWorkingDirectory() {
		return "."; //$NON-NLS-1$
	}
	
	public ConnectionType getConnectionType() {
		ConnectionElement connection = fRootElement.findChild(ConnectionElement.class);
		return (connection != null) ? connection.getConnectionType() : ConnectionElement.getDefaultConnectionType();
	}
	
	public String getTCPHost() {
		TCPConnectionElement connection = fRootElement.findChild(TCPConnectionElement.class);
		return (connection != null) ? connection.getHostName() : TCPConnectionElement.getDefaultHostName();
	}
	
	public String getTCPPort() {
		TCPConnectionElement connection = fRootElement.findChild(TCPConnectionElement.class);
		return (connection != null) ? connection.getPortNumber() : TCPConnectionElement.getDefaultPortNumber();
	}
	
	public String getSerialDevice() {
		SerialConnectionElement connection = fRootElement.findChild(SerialConnectionElement.class);
		return (connection != null) ? connection.getDevice() : SerialConnectionElement.getDefaultDevice();
	}
	
	public int getExecutablesCount() {
		ExecutablesListElement executables = fRootElement.findChild(ExecutablesListElement.class);
		return (executables != null) ? executables.getChildren(ExecutableElement.class).length : 0;
	}

	public boolean isAttach(String execId) {
		ExecutableElement executable = getExecutable(execId);
		return executable instanceof AttachToProcessElement;
	}

	public String getExecutablePath(String execId) {
		ExecutableElement executable = getExecutable(execId);
		return (executable != null) ? executable.getFullProgramPath() : null;
	}

	public String getArguments(String execId) {
		ExecutableElement executable = getExecutable(execId);
		if (executable != null) {
			ArgumentsElement arguments = executable.findChild(ArgumentsElement.class);
			return (arguments != null) ? arguments.getArguments() : null;
		}
		return null;
	}

	public String getRemoteExecutablePath(String execId) {
		ExecutableElement executable = getExecutable(execId);
		if (executable != null) {
			RemoteBinaryElement remote = executable.findChild(RemoteBinaryElement.class);
			return (remote != null) ? remote.getRemotePath() : null;
		}
		return null;
	}

	public String getStopOnStartupSymbol(String execId) {
		ExecutableElement executable = getExecutable(execId);
		if (executable != null) {
			StopOnStartupElement stop = executable.findChild(StopOnStartupElement.class);
			return (stop != null && stop.isStop()) ? stop.getStopSymbol() : null;
		}
		return null;
	}

	public String[] getExecutables() {
		ExecutablesListElement executablesList = fRootElement.findChild(ExecutablesListElement.class);
		if (executablesList == null) {
			return new String[0];
		}
		ExecutableElement[] children = executablesList.getExecutables();
		String[] ids = new String[children.length];
		for (int i = 0; i < children.length; ++i) {
			ids[i] = children[i].getId();
		}
		return ids;
	}
	
	private ExecutableElement getExecutable(String execId) {
		ILaunchElement child = fRootElement.findChild(execId);
		return (child instanceof ExecutableElement) ? (ExecutableElement)child : null;
	}
	
	public String getCoreExecutable() {
		CoreExecutableElement exec = fRootElement.findChild(CoreExecutableElement.class);
		return (exec != null) ? exec.getId() : null;
	}
	
	public String getCoreFile(String execId) {
		CoreFileElement coreFile = null; 
		ILaunchElement element = fRootElement.findChild(execId);
		if (element instanceof CoreExecutableElement) {
			coreFile = element.findChild(CoreFileElement.class);
		}
		return (coreFile != null) ? coreFile.getCoreFile() : null;
	}

	public List<String> getSharedLibraryPaths() {
		SharedLibrariesElement element = fRootElement.findChild(SharedLibrariesElement.class);
		List<String> s = Collections.emptyList();
		return (element != null) ? Arrays.asList(element.getSharedLibraryPaths()) : s;
	}

	public boolean autoLoadSharedLibrarySymbols() {
		SharedLibrariesElement element = fRootElement.findChild(SharedLibrariesElement.class);
		return (element != null) ? element.isAutoLoadSymbols() : SharedLibrariesElement.getDefaultAutoLoadSymbols();
	}
}
