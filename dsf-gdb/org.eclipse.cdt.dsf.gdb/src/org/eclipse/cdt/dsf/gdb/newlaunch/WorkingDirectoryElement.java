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
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement.ProjectChangeEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

/**
 * @since 4.3
 */
public class WorkingDirectoryElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".workingDirectory"; //$NON-NLS-1$
	final private static String ATTR_PATH = ".path"; //$NON-NLS-1$
	final private static String ATTR_USE_DEFAULT = ".useDefault"; //$NON-NLS-1$

	private String fPath = ""; //$NON-NLS-1$
	private boolean fUseDefault = true;

	public WorkingDirectoryElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Working Directory", "Working directory");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
	}

	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		fPath = getAttribute(attributes, getId() + ATTR_PATH, ""); //$NON-NLS-1$
		fUseDefault = getAttribute(attributes, getId() + ATTR_USE_DEFAULT, true);
	}

	@Override
	protected void doPerformApply(Map<String, Object> attributes) {
		attributes.put(getId() + ATTR_PATH, getPath());
		attributes.put(getId() + ATTR_USE_DEFAULT, useDefault());
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		fPath = ""; //$NON-NLS-1$
		fUseDefault = true;
		attributes.put(getId() + ATTR_PATH, ""); //$NON-NLS-1$
		attributes.put(getId() + ATTR_USE_DEFAULT, true);
	}

	@Override
	protected boolean isContentValid() {
		setErrorMessage(null);
		if (useDefault())
			return true;

		// if variables are present, we cannot resolve the directory
		String path = getPath();
		if (path.indexOf("${") >= 0) { //$NON-NLS-1$
			IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
			try {
				manager.validateStringVariables(path);
			} 
			catch(CoreException e) {
				setErrorMessage(e.getMessage());
				return false;
			}
		} else if (path.length() > 0) {
			IContainer container = getContainer();
			if (container == null) {
				if (new File(path).isDirectory()) {
					return true;
				}
				setErrorMessage("Working directory does not exist");
				return false;
			}
		}
		return true;
	}

	public String getPath() {
		return (useDefault()) ? getDefaultPath() : fPath;
	}

	public void setPath(String path) {
		if (fPath.equals(path))
			return;
		fPath = path;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public boolean useDefault() {
		return fUseDefault;
	}

	public void setUseDefault(boolean useDefault) {
		if (fUseDefault == useDefault)
			return;
		fUseDefault = useDefault;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	protected String getDefaultPath() {
		String projectName = getProjectName();
		if (projectName != null && !projectName.isEmpty()) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			ICProject cProject = CCorePlugin.getDefault().getCoreModel().create(project);
			if (cProject != null && cProject.exists()) {
				return String.format("${workspace_loc:%s}", cProject.getPath().makeRelative().toOSString()); //$NON-NLS-1$
			}
		}
		String programName = getProgramName();
		if (programName != null && !programName.isEmpty()) {
			File file = new File(programName);
			if (file.isAbsolute() && file.exists() && !file.isDirectory()) {
				String parentDir = file.getParent();
				if (parentDir != null) {
					return parentDir;
				}
			}
		}		
		return System.getProperty("user.dir"); //$NON-NLS-1$
	}
	
	protected String getProjectName() {
		ExecutableElement execElement = findAncestor(ExecutableElement.class);
		return execElement != null ? execElement.getProjectName() : null;
	}
	
	protected String getProgramName() {
		ExecutableElement execElement = findAncestor(ExecutableElement.class);
		return execElement != null ? execElement.getProgramName() : null;
	}

	protected IContainer getContainer() {
		String path = getPath();
		if (path.length() > 0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource res = root.findMember(path);
			if (res instanceof IContainer) {
				return (IContainer) res;
			}
		}
		return null;
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof ProjectChangeEvent) {
			handleProjectChange((ProjectChangeEvent)event);
		}
		super.update(event);
	}

	private void handleProjectChange(ProjectChangeEvent event) {
		if (useDefault()) {
			elementChanged(CHANGE_DETAIL_STATE);
		}
	}
}
