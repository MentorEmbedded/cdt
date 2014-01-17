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

import java.io.File;

import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.IGdbUIConstants;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.StopModeElement;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DebuggerSettingsUIElement extends AbstractUIElement {

	// Summary widgets
	private Label fGDBCommandSummary;
	private Label fStopModeSummary;

	// Detail widgets
	private Text fGDBCommandText;
	private Text fGDBInitText;

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
		fGDBCommandText = null;
		fGDBInitText = null;
	}

	@Override
	protected void doCreateDetailsContent(final Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label label = ControlFactory.createLabel(comp, LaunchUIMessages.getString("GDBDebuggerPage.gdb_debugger")); //$NON-NLS-1$
		label.setLayoutData(new GridData());
		fGDBCommandText = ControlFactory.createTextField(comp, SWT.SINGLE | SWT.BORDER);
		fGDBCommandText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
            	getLaunchElement().setGDBPath(fGDBCommandText.getText().trim());
			}
		});
		Button button = new Button(comp, SWT.PUSH);
		button.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		button.setToolTipText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_browse")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleGDBButtonSelected(parent.getShell());
			}
		});
		label = ControlFactory.createLabel(comp, LaunchUIMessages.getString("GDBDebuggerPage.gdb_command_file")); //$NON-NLS-1$
		label.setLayoutData(new GridData());
		fGDBInitText = ControlFactory.createTextField(comp, SWT.SINGLE | SWT.BORDER);
		fGDBInitText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fGDBInitText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
            	getLaunchElement().setGDBInitFile(fGDBInitText.getText().trim());
			}
		});

		button = new Button(comp, SWT.PUSH);
		button.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		button.setToolTipText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_cmdfile_browse")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleGDBInitButtonSelected(parent.getShell());
			}
		});

		label = ControlFactory.createLabel(
			comp, 
			LaunchUIMessages.getString("GDBDebuggerPage.cmdfile_warning"), //$NON-NLS-1$
			200, 
			SWT.DEFAULT, 
			SWT.WRAP);

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd.widthHint = 200;
		label.setLayoutData(gd);
	}

	private void handleGDBButtonSelected(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.NONE);
		dialog.setText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_browse_dlg_title")); //$NON-NLS-1$
		String gdbCommand = fGDBCommandText.getText().trim();
		int lastSeparatorIndex = gdbCommand.lastIndexOf(File.separator);
		if (lastSeparatorIndex != -1) {
			dialog.setFilterPath(gdbCommand.substring(0, lastSeparatorIndex));
		}
		String res = dialog.open();
		if (res == null) {
			return;
		}
		fGDBCommandText.setText(res);
	}

	private void handleGDBInitButtonSelected(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.NONE);
		dialog.setText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_cmdfile_dlg_title")); //$NON-NLS-1$
		String gdbCommand = fGDBInitText.getText().trim();
		int lastSeparatorIndex = gdbCommand.lastIndexOf(File.separator);
		if (lastSeparatorIndex != -1) {
			dialog.setFilterPath(gdbCommand.substring(0, lastSeparatorIndex));
		}
		String res = dialog.open();
		if (res == null) {
			return;
		}
		fGDBInitText.setText(res);
	}

	@Override
	protected void initializeDetailsContent() {
		if (fGDBCommandText != null) {
			fGDBCommandText.setText(getLaunchElement().getGDBPath());
		}
		if (fGDBInitText != null) {
			fGDBInitText.setText(getLaunchElement().getGDBInitFile());
		}
	}

	@Override
	protected void doCreateSummaryContent(Composite parent) {
		fGDBCommandSummary = new Label(parent, SWT.NONE);
		String gdbPath = getLaunchElement().getGDBPath();
		if (getLaunchElement().getDefaultGDBPath().equals(gdbPath)) {
			gdbPath += " (default)";
		}
		fGDBCommandSummary.setText(gdbPath);
		
		StopModeElement stopMode = getLaunchElement().findChild(StopModeElement.class);
		boolean isNonStop = (stopMode != null) ? stopMode.isNonStop() : StopModeElement.isNonStopDefault();
		fStopModeSummary = new Label(parent, SWT.NONE);
		fStopModeSummary.setText(String.format("Mode: %s", (isNonStop) ? "non-stop" : "all-mode"));  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	protected boolean hasMultipleRows() {
		return true;
	}
}
