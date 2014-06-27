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
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.core.launch.ListLaunchElement;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @since 4.3
 */
public class ExecutablesListElement extends ListLaunchElement {

	private class ListElementData {

		private String fClassName;
		private String fId;

		private ListElementData(String data) throws CoreException {
			StringTokenizer st = new StringTokenizer(data, ","); //$NON-NLS-1$
			if (st.hasMoreTokens()) {
				fClassName = st.nextToken();
				if (st.hasMoreTokens()) {
					fId = st.nextToken();
					return;
				}
			}
			throw new CoreException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, "Invalid list element"));
		}

		private ListElementData(String className, String id) {
			super();
			fClassName = className;
			fId = id;
		}
		
		private String getClassName() {
			return fClassName;
		}

		private String getId() {
			return fId;
		}

		@Override
		public String toString() {
			return String.format("%s,%s", fClassName, fId); //$NON-NLS-1$
		}
	}

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
	protected void doCreateChildren(Map<String, Object> attributes) {
		List<ExecutableElement> list = new ArrayList<ExecutableElement>();
		List<String> dataList = getAttribute(attributes, getId(), new ArrayList<String>());
		for (String dataStr : dataList) {
			try {
				ListElementData data = new ListElementData(dataStr);
				if (AttachToProcessElement.class.getName().equals(data.getClassName())) {
					list.add(new AttachToProcessElement(this, data.getId()));
					Integer index = parseId(data.getId());
					if (index != null) {
						fIds.add(index);
					}
				}
				if (ExecutableElement.class.getName().equals(data.getClassName())) {
					list.add(new ExecutableElement(this, data.getId()));
					Integer index = parseId(data.getId());
					if (index != null) {
						fIds.add(index);
					}
				}
			}
			catch(CoreException e) {
				GdbPlugin.log(e.getStatus());
			}
		}
		addChildren(list.toArray(new ExecutableElement[list.size()]));
	}

	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
	}

	@Override
	protected boolean isContentValid() {
		return true;
	}

	public ExecutableElement[] getExecutables() {
		List<ExecutableElement> list = new ArrayList<ExecutableElement>();
		for (ILaunchElement child : getChildren()) {
			if (child.isEnabled() && child instanceof ExecutableElement) {
				list.add((ExecutableElement)child);
			}
		}
		return list.toArray(new ExecutableElement[list.size()]);
	}

	public ExecutableElement addExecutable() {
		ExecutableElement element = ExecutableElement.createNewExecutableElement(this, getUniqueChildId());
		doInsertChild(getChildren().length, element);
		elementAdded(element, ADD_DETAIL_ACTIVATE);
		return element;
	}

	public AttachToProcessElement addAttachToProcess() {
		AttachToProcessElement element = AttachToProcessElement.createNewAttachToProcessElement(this, getUniqueChildId());
		doInsertChild(getChildren().length, element);
		elementAdded(element, ADD_DETAIL_ACTIVATE);
		return element;
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
	protected void doPerformApply(Map<String, Object> attributes) {
		List<String> ids = new ArrayList<String>(getChildren().length);
		for (ILaunchElement child : getChildren()) {
			ListElementData data = new ListElementData(child.getClass().getName(), child.getId());
			ids.add(data.toString());
		}
		attributes.put(getId(), ids);
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		attributes.put(getId(), Collections.EMPTY_LIST);
	}
}
