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

import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerSettingsElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class DebuggerSettingsUIElement extends AbstractUIElement {

	private static String[] fgTraceModeLabels = new String[] {
		LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_fast"), //$NON-NLS-1$
		LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_normal"), //$NON-NLS-1$
		LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_auto"), //$NON-NLS-1$
	};

	// Detail widgets
	private Button fReverseButton;
	private Button fUpdateThreadsButton;
	private Button fDebugOnFork;
	private Combo fTracepointModeCombo;
	
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
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));		
		GridUtils.fillIntoGrid(comp, parent);
		
		fReverseButton = new Button(comp, SWT.CHECK);
		GridUtils.fillIntoGrid(fReverseButton, comp);
		fReverseButton.setText(LaunchUIMessages.getString("GDBDebuggerPage.reverse_Debugging")); //$NON-NLS-1$
		fReverseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().enableReverse(fReverseButton.getSelection());
			}
		});
		
		fUpdateThreadsButton = new Button(comp, SWT.CHECK);
		GridUtils.fillIntoGrid(fUpdateThreadsButton, comp);
		fUpdateThreadsButton.setText(LaunchUIMessages.getString("GDBDebuggerPage.update_thread_list_on_suspend")); //$NON-NLS-1$
		fUpdateThreadsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setUpdateThreadListOnSuspend(fUpdateThreadsButton.getSelection());;
			}
		});
		PlatformUI.getWorkbench().getHelpSystem().setHelp(fUpdateThreadsButton, GdbUIPlugin.PLUGIN_ID + ".update_threadlist_button_context"); //$NON-NLS-1$
		
		fDebugOnFork = new Button(comp, SWT.CHECK);
		GridUtils.fillIntoGrid(fDebugOnFork, comp);
		fDebugOnFork.setText(LaunchUIMessages.getString("GDBDebuggerPage.Automatically_debug_forked_processes")); //$NON-NLS-1$
		fDebugOnFork.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setDebugOnFork(fDebugOnFork.getSelection());;
			}
		});
		
		Label label = new Label(comp, SWT.NONE);
		label.setText(LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_label")); //$NON-NLS-1$
		
		fTracepointModeCombo = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
		fTracepointModeCombo.setItems(fgTraceModeLabels);
	}

	@Override
	protected void initializeDetailsContent() {
		if (fReverseButton != null) {
			fReverseButton.setSelection(getLaunchElement().isReverseEnabled());
		}
		if (fUpdateThreadsButton != null) {
			fUpdateThreadsButton.setSelection(getLaunchElement().updateThreadListOnSuspend());
		}
		if (fDebugOnFork != null) {
			fDebugOnFork.setSelection(getLaunchElement().isDebugOnFork());
		}
		if (fTracepointModeCombo != null) {
			fTracepointModeCombo.select(getLaunchElement().getTracepointMode().ordinal());
		}
	}

	@Override
	protected void doCreateSummaryContent(Composite parent) {
	}

	@Override
	protected boolean hasMultipleRows() {
		return false;
	}
}
