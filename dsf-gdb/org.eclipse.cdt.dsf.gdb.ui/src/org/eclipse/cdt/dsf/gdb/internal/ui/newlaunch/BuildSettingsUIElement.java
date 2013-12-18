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

import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement;

public class BuildSettingsUIElement extends AbstractUIElement {

	public BuildSettingsUIElement(BuildSettingsElement launchElement, boolean showDetails) {
		super(launchElement, showDetails);
	}

	@Override
	public BuildSettingsElement getLaunchElement() {
		return (BuildSettingsElement)super.getLaunchElement();
	}
}
