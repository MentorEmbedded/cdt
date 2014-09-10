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

import java.util.Map;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;

/**
 * @since 4.6
 */
public class CoreExecutableElement extends ExecutableElement {

	final private static String ELEMENT_ID = ".coreExecutable"; //$NON-NLS-1$

	public CoreExecutableElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Executable", "Executable to debug");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
		addChildren(new ILaunchElement[] { 
			new CoreFileElement(this) 
		});
	}

	@Override
	public boolean canRemove() {
		return false;
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof SessionTypeChangeEvent) {
			handleSessionTypeChange((SessionTypeChangeEvent)event);
		}
		super.update(event);
	}
	
	private void handleSessionTypeChange(SessionTypeChangeEvent event) {
		 setEnabled(SessionType.CORE.equals(event.getNewType()));
	}
}
