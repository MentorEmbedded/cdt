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

import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.StopModeElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class StopModeUIElement extends AbstractUIElement {

	private Button fNonStopButton;

	public StopModeUIElement(StopModeElement launchElement) {
		super(launchElement, true);
	}

	@Override
	public StopModeElement getLaunchElement() {
		return (StopModeElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fNonStopButton = null;
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		fNonStopButton = new Button(parent, SWT.CHECK);
		GridUtils.fillIntoGrid(fNonStopButton, parent);
		fNonStopButton.setText(LaunchUIMessages.getString("GDBDebuggerPage.nonstop_mode")); //$NON-NLS-1$
		fNonStopButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setNonStop(fNonStopButton.getSelection());
			}
		});
	}

	@Override
	protected void initializeDetailsContent() {
		if (fNonStopButton != null) {
			fNonStopButton.setSelection(getLaunchElement().isNonStop());
		}
	}
}
