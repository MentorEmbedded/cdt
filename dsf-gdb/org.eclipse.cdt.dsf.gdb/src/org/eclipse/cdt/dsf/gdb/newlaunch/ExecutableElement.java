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

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class ExecutableElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".executable"; //$NON-NLS-1$
	final private static String ATTR_PROGRAM_NAME = ".programName"; //$NON-NLS-1$
	final private static String ATTR_PROJECT_NAME = ".projectName"; //$NON-NLS-1$
	final private static String ATTR_PLATFORM = ".platform"; //$NON-NLS-1$

	private String fProgramName = ""; //$NON-NLS-1$
	private String fProjectName = ""; //$NON-NLS-1$

	private String fPlatformFilter = ""; //$NON-NLS-1$

	public static ExecutableElement createNewExecutableElement(ILaunchElement parent, String id) {
		ExecutableElement element = new ExecutableElement(parent, id);
		element.doCreateChildren0();
		return element;
	}

	public ExecutableElement(ILaunchElement parent, String id) {
		this(parent, id, "Executable", "Executable to run/debug");
	}

	public ExecutableElement(ILaunchElement parent, String id, String name, String description) {
		super(parent, id, name, description);
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
		doCreateChildren0();
	}

	private void doCreateChildren0() {
		addChildren(new ILaunchElement[] {
				new RemoteBinaryElement(this),
				new ArgumentsElement(this),
				new WorkingDirectoryElement(this),
				new BuildSettingsElement(this),
				new DebugOptionsElement(this),
			});
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		try {
			fProgramName = config.getAttribute(getId() + ATTR_PROGRAM_NAME, ""); //$NON-NLS-1$
			fPlatformFilter = config.getAttribute(getId() + ATTR_PLATFORM, Platform.getOS());
			fProjectName = config.getAttribute(getId() + ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
		}
		catch(CoreException e) {
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_PROGRAM_NAME, getProgramName());
		config.setAttribute(getId() + ATTR_PROJECT_NAME, getProjectName());
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_PROGRAM_NAME, ""); //$NON-NLS-1$
		config.setAttribute(getId() + ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		return isProgramNameValid(getProgramName(), getProjectName()) && isProjectNameValid(getProjectName());
	}

	public String getProgramName() {
		return fProgramName;
	}

	public void setProgramName(String programName) {
		if (fProgramName == null || !fProgramName.equals(programName)) {
			fProgramName = programName;
			elementChanged(CHANGE_DETAIL_STATE);
		}
	}

	public String getProjectName() {
		return fProjectName;
	}

	public void setProjectName(String projectName) {
		if (fProjectName == null || !fProjectName.equals(projectName)) {
			fProjectName = projectName;
			elementChanged(CHANGE_DETAIL_STATE);
		}
	}
	
	public ICProject getProject(String name) {
		return CoreModel.getDefault().getCModel().getCProject(name);
	}

	protected boolean isProgramNameValid(String programName, String projectName) {
   		try {
			programName = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(programName);
		} catch (CoreException e) {
			// Silently ignore substitution failure (for consistency with "Arguments" and "Work directory" fields)
		}
		if (programName.length() == 0) {
			setErrorMessage(LaunchMessages.getString("CMainTab.Program_not_specified")); //$NON-NLS-1$
			return false;
		}
		if (programName.equals(".") || programName.equals("..")) { //$NON-NLS-1$ //$NON-NLS-2$
			setErrorMessage(LaunchMessages.getString("CMainTab.Program_does_not_exist")); //$NON-NLS-1$
			return false;
		}
		IPath exePath = new Path(programName);
		if (exePath.isAbsolute()) {
			// For absolute paths, we don't need a project, we can debug the binary directly
			// as long as it exists
			File executable = exePath.toFile();
			if (!executable.exists()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Program_does_not_exist")); //$NON-NLS-1$
				return false;
			}
			if (!executable.isFile()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Selection_must_be_file")); //$NON-NLS-1$
				return false;
			}
		} else {
			// For relative paths, we need a proper project
			if (projectName == null || projectName.length() == 0) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Project_not_specified")); //$NON-NLS-1$
				return false;
			}
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (!project.exists()) {
				setErrorMessage(LaunchMessages.getString("Launch.common.Project_does_not_exist")); //$NON-NLS-1$
				return false;
			}
			if (!project.isOpen()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Project_must_be_opened")); //$NON-NLS-1$
				return false;
			}
			if (!project.getFile(programName).exists()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Program_does_not_exist")); //$NON-NLS-1$
				return false;
			}
		}
		// Notice that we don't check if exePath points to a valid executable since such
		// check is too expensive to be done on the UI thread.
		// See "https://bugs.eclipse.org/bugs/show_bug.cgi?id=328012".
		return true;
	}
	
	protected boolean isProjectNameValid(String projectName) {
		if (projectName == null || projectName.isEmpty()) {
			return true;
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!project.exists()) {
			setErrorMessage(LaunchMessages.getString("Launch.common.Project_does_not_exist")); //$NON-NLS-1$
			return false;
		}
		if (!project.isOpen()) {
			setErrorMessage(LaunchMessages.getString("CMainTab.Project_must_be_opened")); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	public String getPlatformFilter() {
		return fPlatformFilter;
	}

	@Override
	public boolean canRemove() {
		return true;
	}
}
