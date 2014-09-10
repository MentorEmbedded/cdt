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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;

/**
 * @since 4.6
 */
public class SharedLibrariesElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".sharedLibraries"; //$NON-NLS-1$
	final private static String ATTR_SOLIB_PATHS = ".sharedLibraryPathsList"; //$NON-NLS-1$
	final private static String ATTR_AUTO_SOLIB = ".autoLoadSymbols"; //$NON-NLS-1$
	final private static String ATTR_AUTO_SOLIB_LIST = ".autoLoadSymbolsList"; //$NON-NLS-1$

	private List<String> fSolibPaths = new ArrayList<String>();
	private boolean fAutoSolib = getDefaultAutoLoadSymbols();
	private Set<String> fAutoSolibsList = new HashSet<String>();

	public SharedLibrariesElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Shared Libraries", "Shared Libraries");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
	}

	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		fSolibPaths.addAll(getAttribute(attributes, getId() + ATTR_SOLIB_PATHS, new ArrayList<String>()));
		fAutoSolib = getAttribute(
			attributes, 
			getId() + ATTR_AUTO_SOLIB, 
			IGDBLaunchConfigurationConstants.DEBUGGER_AUTO_SOLIB_DEFAULT);
		fAutoSolibsList.addAll(getAttribute(attributes, getId() + ATTR_AUTO_SOLIB_LIST, new HashSet<String>()));
	}

	@Override
	protected void doPerformApply(Map<String, Object> attributes) {
		attributes.put(getId() + ATTR_SOLIB_PATHS, Arrays.asList(getSharedLibraryPaths()));
		attributes.put(getId() + ATTR_AUTO_SOLIB, fAutoSolib);
		attributes.put(getId() + ATTR_AUTO_SOLIB_LIST, fAutoSolibsList);
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		fSolibPaths.clear();
		fAutoSolib = getDefaultAutoLoadSymbols();
		fAutoSolibsList.clear();
		attributes.put(getId() + ATTR_SOLIB_PATHS, Arrays.asList(getSharedLibraryPaths()));
		attributes.put(getId() + ATTR_AUTO_SOLIB, fAutoSolib);
		attributes.put(getId() + ATTR_AUTO_SOLIB_LIST, fAutoSolibsList);
	}

	@Override
	protected boolean isContentValid() {
		return true;
	}

	public String[] getSharedLibraryPaths() {
		return fSolibPaths.toArray(new String[fSolibPaths.size()]);
	}

	public void setSharedLibraryPaths(String[] paths) {
		boolean changed = false;
		if (paths.length == fSolibPaths.size()) {
			for (int i = 0; i < paths.length; ++i) {
				if (!paths[i].equals(fSolibPaths.get(i))) {
					changed = true;
					break;
				}
			}
		}
		else {
			changed = true;
		}
		if (!changed) {
			return;
		}
		fSolibPaths.clear();
		fSolibPaths.addAll(Arrays.asList(paths));
		elementChanged(CHANGE_DETAIL_STATE);
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
	
	public String[] getAutoSolibList() {
		return fAutoSolibsList.toArray(new String[fAutoSolibsList.size()]);
	}
	
	public void setAutoSolibList(String[] autoSolibs) {
		boolean changed = false;
		if (autoSolibs.length != fAutoSolibsList.size()) {
			changed = true;
		}
		else {
			for (String solib : autoSolibs) {
				if (!fAutoSolibsList.contains(solib)) {
					changed = true;
					break;
				}
			}
			if (!changed) {
				for (String solib : fAutoSolibsList) {
					if (!Arrays.asList(autoSolibs).contains(solib)) {
						changed = true;
						break;
					}
				}
			}
		}
		if (!changed) {
			return;
		}
		fAutoSolibsList.clear();
		fAutoSolibsList.addAll(Arrays.asList(autoSolibs));
		elementChanged(CHANGE_DETAIL_STATE);
	}
	
	public static boolean getDefaultAutoLoadSymbols() {
		return IGDBLaunchConfigurationConstants.DEBUGGER_AUTO_SOLIB_DEFAULT;
	}
}
