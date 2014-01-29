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

package org.eclipse.cdt.dsf.gdb.internal.ui.launching;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.ui.launch.IUIElementFactory;
import org.eclipse.cdt.debug.ui.launch.RootUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.commands.Messages;
import org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch.UIElementFactory;
import org.eclipse.cdt.dsf.gdb.newlaunch.ArgumentsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.RemoteBinaryElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.StopOnStartupElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.WorkingDirectoryElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class NewNewExecutableDialog extends TitleAreaDialog {
	
	private class NewExecutableElement extends ExecutableElement {
		
		private static final String ELEMENT_ID = GdbUIPlugin.PLUGIN_ID + ".newExecutable"; //$NON-NLS-1$

		public NewExecutableElement() {
			super(null, ELEMENT_ID);
		}

		@Override
		protected void doCreateChildren(Map<String, Object> attributes) {
			super.doCreateChildren(attributes);
			BuildSettingsElement build = findChild(BuildSettingsElement.class);
			if (build != null) {
				build.setEnabled(false);
			}
			RemoteBinaryElement remoteBinary = findChild(RemoteBinaryElement.class);
			if (remoteBinary != null) {
				remoteBinary.setEnabled(getAttribute(attributes, ATTR_REMOTE, false));
			}
		}

		@Override
		public boolean canRemove() {
			return false;
		}

		@Override
		protected void elementChanged(int details) {
			super.elementChanged(details);
			validate();
		}
	}

	private class DialogRootUIElement extends RootUIElement {

		@Override
		protected ILaunchElement createTopElement(Map<String, Object> attributes) {
			return new NewExecutableElement();
		}

		@Override
		public void initializeFrom(Map<String, Object> attributes) throws CoreException {
			super.initializeFrom(attributes);
			activateElement(getTopElement());
		}

		@Override
		protected IUIElementFactory createUIElementFactory(Map<String, Object> attributes) {
			return new UIElementFactory();
		}
	}

	public static final int REMOTE = 0x1;
	private static final String ATTR_REMOTE = "remote"; //$NON-NLS-1$

	private int fFlags = 0;
	private NewExecutableInfo fInfo = null;
	
	private DialogRootUIElement fRoot;

	public NewNewExecutableDialog(Shell parentShell, int flags) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fFlags = flags;
		fRoot = new DialogRootUIElement();
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setText(Messages.GdbDebugNewExecutableCommand_Debug_New_Executable); 
		setTitle(Messages.GdbDebugNewExecutableCommand_Select_Binary);
		Control control = super.createContents(parent);
		boolean remote = (fFlags & REMOTE) != 0;
		String message = (remote) ? 
			Messages.GdbDebugNewExecutableCommand_Select_binaries_on_host_and_target :
			Messages.GdbDebugNewExecutableCommand_Select_binary_and_specify_arguments;
		setMessage(message);
		try {
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put(ATTR_REMOTE, remote);
			fRoot.initializeFrom(attributes);
			validate();
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite control = (Composite)super.createDialogArea(parent);
		Composite comp = new Composite(control, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		fRoot.createControl(comp);

		return control;
	}

	@Override
	protected void okPressed() {
		Map<String, Object> attributes = new HashMap<String, Object>();
		fRoot.performApply(attributes);
		fInfo = createExecutableInfo();
		super.okPressed();
	}

	public NewExecutableInfo getExecutableInfo() {
		return fInfo;
	}
	
	private void validate() {
		fRoot.isValid();
		setErrorMessage(fRoot.getErrorMessage());
		getButton(IDialogConstants.OK_ID).setEnabled(getErrorMessage() == null);
	}

	/**
	 * Converts "new style" attributes to "old style".
	 */
	private NewExecutableInfo createExecutableInfo() {
		NewExecutableElement execElement = (NewExecutableElement)fRoot.getTopElement();
		
		String hostPath = execElement.getFullProgramPath();
		
		RemoteBinaryElement remoteElement = execElement.findChild(RemoteBinaryElement.class);
		String targetPath = (remoteElement != null) ? remoteElement.getRemotePath() : ""; //$NON-NLS-1$
		
		ArgumentsElement argsElement = execElement.findChild(ArgumentsElement.class);
		String args = (argsElement != null) ? argsElement.getArguments() : ""; //$NON-NLS-1$
		Map<String, Object> attributes = new HashMap<String, Object>();
		
		StopOnStartupElement stopElement = execElement.findChild(StopOnStartupElement.class);
		attributes.put(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, stopElement.isStop());
		attributes.put(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL, stopElement.getStopSymbol());

		WorkingDirectoryElement wdElement = execElement.findChild(WorkingDirectoryElement.class);
		String wd = null;
		if (wdElement != null) {
			if (!wdElement.useDefault()) {
				wd = wdElement.getPath();
			}
		}
		attributes.put(ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, wd);

		return new NewExecutableInfo(hostPath, targetPath, args, attributes);
	}
}
