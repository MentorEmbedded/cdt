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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;

/**
 * @since 4.3
 */
public class EnvironmentElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".environment"; //$NON-NLS-1$
	final private static String ATTR_ENVIRONMENT_VARIABLES = ".environmentVariables"; //$NON-NLS-1$
	final private static String ATTR_APPEND_ENVIRONMENT_VARIABLES = ".appendEnvironmentVariables"; //$NON-NLS-1$	

	private Map<String, String> fVariables = new HashMap<String, String>();
	private boolean fAppend = true;

	public EnvironmentElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Environment", "Environment");
	}

	@Override
	public void dispose() {
		fVariables.clear();
		super.dispose();
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		fVariables = getAttribute(attributes, getId() + ATTR_ENVIRONMENT_VARIABLES, Collections.EMPTY_MAP);
		fAppend = getAttribute(attributes, getId() + ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
	}

	@Override
	protected void doPerformApply(Map<String, Object> attributes) {
		attributes.put(getId() + ATTR_ENVIRONMENT_VARIABLES, new HashMap<String, String>(getVariables()));
		attributes.put(getId() + ATTR_APPEND_ENVIRONMENT_VARIABLES, isAppend());
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		fVariables.clear();
		fAppend = true;
		attributes.put(getId() + ATTR_ENVIRONMENT_VARIABLES, Collections.EMPTY_MAP);
		attributes.put(getId() + ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
	}

	@Override
	protected boolean isContentValid() {
		return true;
	}

	public Map<String, String> getVariables() {
		return fVariables;
	}

	public void setVariables(Map<String, String> variables) {
		if (fVariables.equals(variables))
			return;
		fVariables = variables;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public boolean isAppend() {
		return fAppend;
	}

	public void setAppend(boolean append) {
		if (fAppend == append)
			return;
		fAppend = append;
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
		 setEnabled(SessionType.LOCAL.equals(event.getNewType()));
	}
}
