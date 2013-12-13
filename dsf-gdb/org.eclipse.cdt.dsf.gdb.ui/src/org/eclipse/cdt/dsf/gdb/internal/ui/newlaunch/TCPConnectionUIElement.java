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
import org.eclipse.cdt.dsf.gdb.newlaunch.TCPConnectionElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label label = new Label(comp, SWT.NONE);
		label.setText(LaunchUIMessages.getString("TCPSettingsBlock.0")); //$NON-NLS-1$
		
		fHostNameText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		fHostNameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fHostNameText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				hostNameChanged();
			}
		});
		
		label = new Label(comp, SWT.NONE);
		label.setText(LaunchUIMessages.getString("TCPSettingsBlock.1")); //$NON-NLS-1$
		
		fPortText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		fPortText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));		
		fPortText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portNumberChanged();
			}
		});
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
		save();
	}

	private void portNumberChanged() {
		save();
	}
}
