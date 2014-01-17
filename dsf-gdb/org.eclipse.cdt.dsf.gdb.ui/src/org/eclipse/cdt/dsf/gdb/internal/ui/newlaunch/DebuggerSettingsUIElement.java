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
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerSettingsElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class DebuggerSettingsUIElement extends AbstractUIElement {

	public DebuggerSettingsUIElement(DebuggerSettingsElement launchElement, boolean showDetails) {
		super(launchElement, showDetails);
	}

	@Override
	public DebuggerSettingsElement getLaunchElement() {
		return (DebuggerSettingsElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
	}

	@Override
	protected void doCreateDetailsContent(final Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));		
	}

	@Override
	protected void initializeDetailsContent() {
	}

	@Override
	protected void doCreateSummaryContent(Composite parent) {
	}

	@Override
	protected boolean hasMultipleRows() {
		return false;
	}
}
