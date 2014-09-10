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

import java.util.Map;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;

/**
 * @since 4.6
 */
public class AttachToProcessElement extends ExecutableElement {

	public static AttachToProcessElement createNewAttachToProcessElement(ILaunchElement parent, String id) {
		AttachToProcessElement element = new AttachToProcessElement(parent, id);
		element.doCreateChildren0();
		OverviewElement overview = element.findAncestor(OverviewElement.class);
		element.update(new OverviewElement.SessionTypeChangeEvent(overview, overview.getSessionType(), null));
		return element;
	}

	public AttachToProcessElement(ILaunchElement parent, String id) {
		super(parent, id, "Attach To Process", "Attach to a running process");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
		doCreateChildren0();
	}

	@Override
	protected boolean isProgramNameValid(String programName, String projectName) {
		if (programName.isEmpty() && projectName.isEmpty()) {
			return true;
		}
		return super.isProgramNameValid( programName, projectName );
	}

	private void doCreateChildren0() {
		addChildren(new ILaunchElement[] {
			new BuildSettingsElement(this),
		});
	}
}
