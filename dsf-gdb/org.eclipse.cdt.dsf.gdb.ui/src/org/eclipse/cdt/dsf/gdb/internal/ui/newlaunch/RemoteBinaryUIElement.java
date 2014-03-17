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

import org.eclipse.cdt.dsf.gdb.newlaunch.RemoteBinaryElement;
import org.eclipse.cdt.ui.grid.StringPresentationModel;
import org.eclipse.cdt.ui.grid.StringViewElement;

public class RemoteBinaryUIElement extends StringViewElement {

	public RemoteBinaryUIElement(final RemoteBinaryElement launchElement, boolean showDetails) {
		super(new StringPresentationModel("Binary on target:") {
			@Override
			protected void doSetValue(String value) {
				launchElement.setRemotePath(value);
			}
			@Override
			protected String doGetValue() {
				return launchElement.getRemotePath();
			}
		});
	}
}
