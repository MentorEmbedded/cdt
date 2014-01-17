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
import org.eclipse.cdt.dsf.gdb.newlaunch.WorkingDirectoryElement;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

public class WorkingDirectoryUIElement extends AbstractUIElement {

	// Summary widgets
	private Label fSummaryLabel;

	// Detail widgets
	private Text fPathText;
	private Button fWorkspaceButton;
	private Button fBrowseButton;
	private Button fVariablesButton;
	private Button fUseDefaultButton;

	@Override
	public void disposeContent() {
		super.disposeContent();
		fPathText = null;
		fWorkspaceButton = null;
		fBrowseButton = null;
		fVariablesButton = null;
		fUseDefaultButton = null;
	}

	@Override
	protected void doCreateSummaryContent(Composite parent) {
		fSummaryLabel = new Label(parent, SWT.NONE);
	}

	@Override
	protected void doCreateDetailsContent(final Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label label = new Label(comp, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 4, 1));
		label.setText(LaunchUIMessages.getString("WorkingDirectoryBlock.Working_directory")); //$NON-NLS-1$
		
		fPathText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		fPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPathText.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				getLaunchElement().setPath(fPathText.getText().trim());
			}
		});
		
		fWorkspaceButton = new Button(comp, SWT.PUSH);
		fWorkspaceButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		fWorkspaceButton.setToolTipText(LaunchUIMessages.getString("WorkingDirectoryBlock.0")); //$NON-NLS-1$
		fWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				workspaceButtonPressed(parent.getShell());
			}
		});
		
		fBrowseButton = new Button(comp, SWT.PUSH);
		fBrowseButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_WORKSPACE));
		fBrowseButton.setToolTipText(LaunchUIMessages.getString("WorkingDirectoryBlock.1")); //$NON-NLS-1$
		fBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonPressed(parent.getShell());
			}
		});
		
		fVariablesButton = new Button(comp, SWT.PUSH);
		fVariablesButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_PATH_VARIABLES));
		fVariablesButton.setToolTipText(LaunchUIMessages.getString("WorkingDirectoryBlock.17")); //$NON-NLS-1$
		fVariablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonPressed(parent.getShell());
			}
		});
		
		fUseDefaultButton = new Button(comp, SWT.CHECK);
		fUseDefaultButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
		fUseDefaultButton.setText(LaunchUIMessages.getString("WorkingDirectoryBlock.Use_default")); //$NON-NLS-1$
		fUseDefaultButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useDefaultButtonChecked();
			}
		});
	}

	@Override
	protected void initializeSummaryContent() {
		String text = getLaunchElement().getPath();
		if (getLaunchElement().useDefault()) {
			text += " (default)";
		}
		fSummaryLabel.setText(text);
	}

	@Override
	protected void initializeDetailsContent() {
		if (fPathText != null) {
			fPathText.setText(getLaunchElement().getPath());
		}
		if (fUseDefaultButton != null) {
			fUseDefaultButton.setSelection(getLaunchElement().useDefault());
		}
		updateEnablement();
	}

	public WorkingDirectoryUIElement(WorkingDirectoryElement launchElement, boolean showDetails) {
		super(launchElement, showDetails);
	}

	@Override
	public WorkingDirectoryElement getLaunchElement() {
		return (WorkingDirectoryElement)super.getLaunchElement();
	}
	
	private void workspaceButtonPressed(Shell shell) {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
			shell, 
			ResourcesPlugin.getWorkspace().getRoot(), 
			false,
			LaunchUIMessages.getString("WorkingDirectoryBlock.4")); //$NON-NLS-1$

		IContainer currentContainer = getContainer();
		if (currentContainer != null) {
			IPath path = currentContainer.getFullPath();
			dialog.setInitialSelections(new Object[] { path});
		}

		dialog.showClosedProjects(false);
		dialog.open();
		Object[] results = dialog.getResult();
		if ((results != null) && (results.length > 0) && (results[0] instanceof IPath)) {
			IPath path = (IPath) results[0];
			String containerName = path.makeRelative().toString();
			fPathText.setText("${workspace_loc:" + containerName + "}"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void browseButtonPressed(Shell shell) {
		DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setMessage(LaunchUIMessages.getString("WorkingDirectoryBlock.7")); //$NON-NLS-1$
		String currentWorkingDir = fPathText.getText().trim();
		if (!currentWorkingDir.trim().equals("")) { //$NON-NLS-1$
			File path = new File(currentWorkingDir);
			if (path.exists()) {
				dialog.setFilterPath(currentWorkingDir);
			}
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			fPathText.setText(selectedDirectory);
		}
	}
	
	private void variablesButtonPressed(Shell shell) {
		String variableText = getVariable(shell);
		if (variableText != null) {
			fPathText.insert(variableText);
		}
	}
	
	private void useDefaultButtonChecked() {
		getLaunchElement().setUseDefault(fUseDefaultButton.getSelection());
		if (fUseDefaultButton.getSelection()) {
			fPathText.setText(getLaunchElement().getPath());
		}
		updateEnablement();
	}
	
	private void updateEnablement() {
		fPathText.setEnabled(!fUseDefaultButton.getSelection());
		fWorkspaceButton.setEnabled(!fUseDefaultButton.getSelection());
		fBrowseButton.setEnabled(!fUseDefaultButton.getSelection());
		fVariablesButton.setEnabled(!fUseDefaultButton.getSelection());
	}

	/**
	 * Returns the selected workspace container,or <code>null</code>
	 */
	protected IContainer getContainer() {
		String path = fPathText.getText().trim();
		if (path.length() > 0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource res = root.findMember(path);
			if (res instanceof IContainer) {
				return (IContainer) res;
			}
		}
		return null;
	}

	private String getVariable(Shell shell) {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(shell);
		dialog.open();
		return dialog.getVariableExpression();
	}
}
