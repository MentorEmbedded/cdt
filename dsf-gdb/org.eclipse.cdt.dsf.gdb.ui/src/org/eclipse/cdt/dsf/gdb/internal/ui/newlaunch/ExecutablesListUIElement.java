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

package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.core.launch.IListLaunchElement;
import org.eclipse.cdt.debug.ui.launch.ListUIElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;

public class ExecutablesListUIElement extends ListUIElement {

	public ExecutablesListUIElement(IListLaunchElement launchElement) {
		super(launchElement);
	}

	@Override
	protected String getLinkLabel(ILaunchElement element) {
		if (element instanceof ExecutableElement) {
			return ((ExecutableElement)element).getProgramName();
		}
		return super.getLinkLabel(element);
	}

	@Override
	protected void upButtonPressed(ILaunchElement element) {
		// TODO Auto-generated method stub
		super.upButtonPressed(element);
	}

	@Override
	protected void downButtonPressed(ILaunchElement element) {
		// TODO Auto-generated method stub
		super.downButtonPressed(element);
	}

	@Override
	protected void removeButtonPressed(ILaunchElement element) {
		super.removeButtonPressed(element);
	}

	@Override
	protected void addButtonPressed() {
		// TODO Auto-generated method stub
		super.addButtonPressed();
	}

}
