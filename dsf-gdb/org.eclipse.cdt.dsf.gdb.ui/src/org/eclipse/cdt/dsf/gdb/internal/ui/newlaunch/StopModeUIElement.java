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

package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.StopModeElement;
import org.eclipse.cdt.ui.grid.BooleanPresentationModel;
import org.eclipse.cdt.ui.grid.CheckboxViewElement;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.swt.widgets.Composite;

public class StopModeUIElement extends GridElement {

	public StopModeUIElement(final StopModeElement launchElement) {
		String n = LaunchUIMessages.getString("GDBDebuggerPage.nonstop_mode");
		BooleanPresentationModel model = new BooleanPresentationModel(n) {
			@Override
			protected boolean doGetValue() { return launchElement.isNonStop(); }
			
			@Override
			protected void doSetValue(boolean value) { launchElement.setNonStop(value); }
		};
		addChild(new CheckboxViewElement(model));
	}

	@Override
	protected void createImmediateContent(Composite parent) { }
}
