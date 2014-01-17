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
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.SharedLibrariesElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SharedLibrariesUIElement extends AbstractUIElement {

	// Summary widgets
	private Label fSummaryLabel;

	// Detail widgets
	private Button fAutoSolibButton;
	
	public SharedLibrariesUIElement(SharedLibrariesElement launchElement, boolean showDetails) {
		super(launchElement, showDetails);
	}

	@Override
	public SharedLibrariesElement getLaunchElement() {
		return (SharedLibrariesElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fAutoSolibButton = null;
	}

	@Override
	protected void doCreateSummaryContent(Composite parent) {
		fSummaryLabel = new Label(parent, SWT.NONE);
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		fAutoSolibButton = new Button(parent, SWT.CHECK);
		fAutoSolibButton.setText(LaunchUIMessages.getString("GDBSolibBlock.0")); //$NON-NLS-1$
		fAutoSolibButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoSolibButtonChecked();
			}
		});
	}

	@Override
	protected void initializeSummaryContent() {
		if (fSummaryLabel != null) {
			boolean autoSolib = getLaunchElement().isAutoLoadSymbols();
			fSummaryLabel.setText((autoSolib) ? 
				"Load shared library symbols automatically" : 
				"Do not load shared library symbols automatically");
		}
	}

	@Override
	protected void initializeDetailsContent() {
		if (fAutoSolibButton != null) {
			fAutoSolibButton.setSelection(getLaunchElement().isAutoLoadSymbols());
		}
	}

	private void autoSolibButtonChecked() {
		getLaunchElement().setAutoLoadSymbols(fAutoSolibButton.getSelection());
	}

	@Override
	protected boolean hasMultipleRows() {
		return getLaunchElement().getSharedLibraryPaths().length > 0;
	}
}
