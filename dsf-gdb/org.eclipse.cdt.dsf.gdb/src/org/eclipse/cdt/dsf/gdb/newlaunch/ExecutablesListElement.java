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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.core.launch.ListLaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class ExecutablesListElement extends ListLaunchElement {

	final private static String ELEMENT_ID = ".executableList"; //$NON-NLS-1$
	
	/**
	 * The list of all child ids used by this list from initialization.
	 * Some of the ids may be deleted from the model but still exist in 
	 * the launch configuration. We need to take them in account when 
	 * generating unique ids.   
	 */
	private Set<Integer> fIds = new HashSet<Integer>();

	public ExecutablesListElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Executables", "Executables");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
		List<ExecutableElement> list = new ArrayList<ExecutableElement>();
		try {
			@SuppressWarnings("unchecked")
			List<String> ids = config.getAttribute(getId(), Collections.EMPTY_LIST);
			for (String id : ids) {
				list.add(new ExecutableElement(this, id));
				Integer index = parseId(id);
				if (index != null) {
					fIds.add(index);
				}
			}
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
		}
		addChildren(list.toArray(new ExecutableElement[list.size()]));
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		return true;
	}

	@Override
	public void addNewElement() {
		ExecutableElement element = ExecutableElement.createNewExecutableElement(this, getUniqueChildId());
		doInsertChild(getChildren().length, element);
		elementAdded(element, ADD_DETAIL_ACTIVATE);
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof SessionTypeChangeEvent) {
			handleSessionTypeChange((SessionTypeChangeEvent)event);
		}
		super.update(event);
	}
	
	private void handleSessionTypeChange(SessionTypeChangeEvent event) {
		 setEnabled(!SessionType.CORE.equals(event.getNewType()));
	}

	/**
	 * Returns a new unique child id for this list.
	 * Note that we take in account all ids created during the life 
	 * cycle of this list to avoid conflicts with the underlying 
	 * launch configuration.
	 */
	protected String getUniqueChildId() {
		int index = 0;
		for (; fIds.contains(Integer.valueOf(index)); ++index);
		fIds.add(Integer.valueOf(index));
		return String.format("%s.%d", getId(), index); //$NON-NLS-1$
	}
	
	/**
	 * We are assuming the "proper" id of a list element has 
	 * the 'parent_id.index' format where 'index' is a non-negative 
	 * decimal integer. 
	 * This method returns the index of such an element or null if the id 
	 * has a different format.
	 */
	protected Integer parseId(String id) {
		int i = id.lastIndexOf('.');
		if (i < 0 || ++i >= id.length()) {
			return null;
		}
		try {
			return Integer.valueOf(id.substring(i));
		}
		catch(NumberFormatException e) {
			return null;
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		List<String> ids = new ArrayList<String>(getChildren().length);
		for (ILaunchElement child : getChildren()) {
			ids.add(child.getId());
		}
		config.setAttribute(getId(), ids);
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId(), Collections.EMPTY_LIST);
	}
}
