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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class SharedLibrariesElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".sharedLibraries"; //$NON-NLS-1$
	final private static String ATTR_SOLIB_PATHS = ".sharedLibraryPathsList"; //$NON-NLS-1$
	final private static String ATTR_AUTO_SOLIB = ".autoLoadSymbols"; //$NON-NLS-1$

	private List<String> fSolibPaths = new ArrayList<String>();
	private boolean fAutoSolib = IGDBLaunchConfigurationConstants.DEBUGGER_AUTO_SOLIB_DEFAULT;

	public SharedLibrariesElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Shared Libraries", "Shared Libraries");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		try {
			@SuppressWarnings("unchecked")
			List<String> paths = config.getAttribute(getId() + ATTR_SOLIB_PATHS, Collections.EMPTY_LIST);
			fSolibPaths.addAll(paths);
			fAutoSolib = config.getAttribute(getId() + ATTR_AUTO_SOLIB, 
				IGDBLaunchConfigurationConstants.DEBUGGER_AUTO_SOLIB_DEFAULT);
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_SOLIB_PATHS, Arrays.asList(getSharedLibraryPaths()));
		config.setAttribute(getId() + ATTR_AUTO_SOLIB, fAutoSolib);
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		fSolibPaths.clear();
		fAutoSolib = IGDBLaunchConfigurationConstants.DEBUGGER_AUTO_SOLIB_DEFAULT;
		config.setAttribute(getId() + ATTR_SOLIB_PATHS, Collections.EMPTY_LIST);
		config.setAttribute(getId() + ATTR_AUTO_SOLIB, 
			IGDBLaunchConfigurationConstants.DEBUGGER_AUTO_SOLIB_DEFAULT);
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		return true;
	}

	public String[] getSharedLibraryPaths() {
		return fSolibPaths.toArray(new String[fSolibPaths.size()]);
	}

	public void addSharedLibraryPath(String path) {
		fSolibPaths.add(path);
		elementChanged(CHANGE_DETAIL_STATE);
	}
	
	public void removeSharedLibraryPath(int index) {
		if (index >= 0 && index < fSolibPaths.size()) {
			fSolibPaths.remove(index);
			elementChanged(CHANGE_DETAIL_STATE);
		}
	}
	
	public void moveSharedLibraryPathUp(int index) {
		if (index > 0 && index < fSolibPaths.size()) {
			fSolibPaths.add(--index, fSolibPaths.remove(index));
			elementChanged(CHANGE_DETAIL_STATE);
		}
	}
	
	public void moveSharedLibraryPathDown(int index) {
		if (index >= 0 && index + 1 < fSolibPaths.size()) {
			fSolibPaths.add(++index, fSolibPaths.remove(index));
			elementChanged(CHANGE_DETAIL_STATE);
		}
	}

	public boolean isAutoLoadSymbols() {
		return fAutoSolib;
	}

	public void setAutoLoadSymbols(boolean autoSolib) {
		if (fAutoSolib == autoSolib)
			return;
		fAutoSolib = autoSolib;
		elementChanged(CHANGE_DETAIL_STATE);
	}
}
