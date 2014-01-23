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
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.TCPConnectionElement;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TCPConnectionUIElement extends AbstractUIElement {

	private Text fHostNameText;
	private Text fPortText;
	
	public TCPConnectionUIElement(TCPConnectionElement launchElement, boolean showDetails) {
		super(launchElement, true);
	}

	@Override
	public TCPConnectionElement getLaunchElement() {
		return (TCPConnectionElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fHostNameText = null;
		fPortText = null;
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		label.setText(LaunchUIMessages.getString("TCPSettingsBlock.0")); //$NON-NLS-1$
		
		fHostNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fHostNameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fHostNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				hostNameChanged();
			}
		});

		GridUtils.createHorizontalSpacer(parent, 2);

		label = new Label(parent, SWT.NONE);
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		label.setText(LaunchUIMessages.getString("TCPSettingsBlock.1")); //$NON-NLS-1$
		
		fPortText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fPortText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));		
		fPortText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				portNumberChanged();
			}
		});

		GridUtils.createHorizontalSpacer(parent, 2);
	}

	@Override
	protected void initializeDetailsContent() {
		if (fHostNameText != null) {
			fHostNameText.setText(getLaunchElement().getHostName());
		}
		if (fPortText != null) {
			fPortText.setText(getLaunchElement().getPortNumber());
		}
	}

	@Override
	public void save() {
		if (fHostNameText != null) {
			getLaunchElement().setHostName(fHostNameText.getText().trim());
		}
		if (fPortText != null) {
			getLaunchElement().setPortNumber(fPortText.getText().trim());
		}
	}

	private void hostNameChanged() {
		if (fHostNameText != null) {
			getLaunchElement().setHostName(fHostNameText.getText().trim());
		}
	}

	private void portNumberChanged() {
		if (fPortText != null) {
			getLaunchElement().setPortNumber(fPortText.getText().trim());
		}
	}
}
