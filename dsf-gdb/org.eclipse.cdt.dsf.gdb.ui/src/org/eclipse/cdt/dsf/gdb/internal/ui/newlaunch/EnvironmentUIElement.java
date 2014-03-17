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

import org.eclipse.cdt.dsf.gdb.newlaunch.EnvironmentElement;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class EnvironmentUIElement extends GridElement {

	private EnvironmentElement launchElement;
	private boolean showDetails;

	public EnvironmentUIElement(EnvironmentElement launchElement, boolean showDetails) {
		this.launchElement = launchElement;
		this.showDetails = showDetails;
	}

	@Override
	protected void createImmediateContent(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(launchElement.getName());
		
		Label padding = new Label(parent, SWT.NONE);
		padding.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));			
	}

}
