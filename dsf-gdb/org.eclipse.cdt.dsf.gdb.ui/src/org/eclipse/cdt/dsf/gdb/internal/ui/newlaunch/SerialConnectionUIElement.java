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

import java.util.Arrays;

import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.SerialConnectionElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SerialConnectionUIElement extends AbstractUIElement {

	private Text fDeviceText;
	private Combo fSpeedCombo;

	public SerialConnectionUIElement(SerialConnectionElement launchElement, boolean showDetails) {
		super(launchElement, true);
	}

	@Override
	public SerialConnectionElement getLaunchElement() {
		return (SerialConnectionElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fDeviceText = null;
		fSpeedCombo = null;
	}

	@Override
	protected void doCreateDetailsContent( Composite parent ) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label label = new Label(comp, SWT.NONE);
		label.setText(LaunchUIMessages.getString("SerialPortSettingsBlock.0")); //$NON-NLS-1$
		
		fDeviceText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		fDeviceText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fDeviceText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				deviceChanged();
			}
		});
		
		label = new Label(comp, SWT.NONE);
		label.setText(LaunchUIMessages.getString("SerialPortSettingsBlock.1")); //$NON-NLS-1$
		
		fSpeedCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		fSpeedCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deviceSpeedChanged();
			}
		});
	}

	@Override
	protected void initializeDetailsContent() {
		if (fDeviceText != null) {
			fDeviceText.setText(getLaunchElement().getDevice());
		}
		if (fSpeedCombo != null) {
			String[] speedChoices = getLaunchElement().getDeviceSpeedChoices();
			fSpeedCombo.setItems(speedChoices);
			int index = Arrays.asList(speedChoices).indexOf(getLaunchElement().getSpeed());
			if (index < 0) {
				index = 0;
			}
			fSpeedCombo.select(index);
		}
	}

	@Override
	public void save() {
		if (fDeviceText != null) {
			getLaunchElement().setDevice(fDeviceText.getText().trim());
		}
		if (fSpeedCombo != null) {
			getLaunchElement().setDeviceSpeed(fSpeedCombo.getItem(fSpeedCombo.getSelectionIndex()));
		}
	}
	
	private void deviceChanged() {
		if (fDeviceText != null) {
			getLaunchElement().setDevice(fDeviceText.getText().trim());
		}
	}
	
	private void deviceSpeedChanged() {
		if (fSpeedCombo != null) {
			getLaunchElement().setDeviceSpeed(fSpeedCombo.getItem(fSpeedCombo.getSelectionIndex()));
		}
	}
}
