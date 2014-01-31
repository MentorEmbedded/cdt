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

package org.eclipse.cdt.debug.ui.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;


/**
 * @since 7.4
 */
abstract public class LaunchRootUIElement extends RootUIElement {

	private Map<String, Map<String, String>> fElementIds = new HashMap<String, Map<String,String>>();

	@Override
	public void dispose() {
		super.dispose();
		fElementIds.clear();
	}

	@SuppressWarnings( "unchecked" )
	public void initializeFrom(ILaunchConfigurationWorkingCopy config) throws CoreException {
		setInitializing(true);
		super.initializeFrom(config.getAttributes());
		// Restore previous state
		ILaunchElement topElement = getTopElement();
		String id = getStoredElementId(config);
		ILaunchElement current = null;
		if (id != null && topElement != null) {
			current = topElement.findChild(id);
		}
		if (current == null) {
			current = topElement;
		}
		if (current != null) {
			restoreAndActivateElement(current);
		}
		setInitializing(false);
	}

	public void performApply(ILaunchConfigurationWorkingCopy config) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		super.performApply(attributes);
		config.setAttributes(attributes);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		super.setDefaults(attributes);
		config.setAttributes(attributes);
	}

	public void storeState(ILaunchConfigurationWorkingCopy config) {
		if (config == null)
			return;
		String currentId = getCurrentElementId();
		if (currentId == null)
			return;
		try {
			String typeId = config.getType().getIdentifier();
			Map<String, String> map = fElementIds.get(typeId);
			if (map == null) {
				map = new HashMap<String, String>();
				fElementIds.put(typeId, map);
			}
			map.put(config.getName(), currentId);
		}
		catch(CoreException e) {
			CDebugCorePlugin.log(e.getStatus());
		}
	}

	private String getStoredElementId(ILaunchConfiguration config) {
		try {
			String typeId = config.getType().getIdentifier();
			Map<String, String> map = fElementIds.get(typeId);
			return (map != null) ? map.get(config.getName()) : null;
		}
		catch(CoreException e) {
			CDebugCorePlugin.log(e.getStatus());
		}
		return null;
	}
	
	private void restoreAndActivateElement(ILaunchElement element) {
		List<ILaunchElement> list = new ArrayList<ILaunchElement>();
		ILaunchElement current = element;
		while(current != null) {
			list.add(0, current);
			current = current.getParent();
		}
		for (ILaunchElement el : list) {
			activateElement(el);
		}
	}
}
