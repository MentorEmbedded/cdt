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

import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement;
import org.eclipse.cdt.ui.grid.BasicGroupGridElement;
import org.eclipse.cdt.ui.grid.PillSelectionViewElement;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.cdt.ui.grid.StringPresentationModel;
import org.eclipse.cdt.ui.grid.StringViewElement;
import org.eclipse.swt.widgets.Composite;

// FIXME: need to revive all the advanced functionlity we had.
public class ConnectionUIElement extends BasicGroupGridElement {

	private ConnectionElement launchElement;

	public ConnectionUIElement(ConnectionElement launchElement) {
		super("Connection");
		this.launchElement = launchElement;
	}

	@Override
	protected void createImmediateContent(Composite parent) {	
	}
	
	// FIXME: link everything with the model.
	@Override
	protected void populateChildren() {
		
		
		SelectionPresentationModel type = new SelectionPresentationModel("Type", Arrays.asList("TCP", "Serial")) {
		};
		PillSelectionViewElement typeView = new PillSelectionViewElement(type);
		addChild(typeView);
		
		// FIXME: make it actually useful. 
		StringPresentationModel address = new StringPresentationModel("Address") {
			
		};
		StringViewElement addressView = new StringViewElement(address);
		addChild(addressView);
	}
	
	/*
	@Override
	protected int doCreateSummaryContent(Composite parent) {
		fSummaryText = new Label(parent, SWT.NONE);
		fSummaryText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		return 1;
	}

	@Override
	protected void initializeSummaryContent() {
		if (fSummaryText == null)
			return;
		ConnectionElement connElement = getLaunchElement();
		StringBuilder sb = new StringBuilder();
		ConnectionType type = connElement.getConnectionType();
		if (type == ConnectionType.TCP) {
			sb.append("TCP");
			TCPConnectionElement tcpElement = connElement.findChild(TCPConnectionElement.class);
			String hostName = tcpElement.getHostName();
			String portNumber = tcpElement.getPortNumber();
			sb.append(String.format(":%s:%s", hostName, portNumber)); //$NON-NLS-1$
		}
		else if (type == ConnectionType.SERIAL) {
			sb.append("Serial");
			SerialConnectionElement serElement = connElement.findChild(SerialConnectionElement.class);
			String device = serElement.getDevice();
			String speed = serElement.getSpeed();
			sb.append(String.format(":%s:%s", device, speed)); //$NON-NLS-1$
		}
		else {
			sb.append("Unknown connection type");
		}
		fSummaryText.setText(sb.toString());
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {		
		Label label = new Label(parent, SWT.NONE);
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		label.setText("Connection type: ");
		
		fTypeSelector = new PillsControl(parent, SWT.NONE);
		fTypeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fTypeSelector.setBackground(parent.getBackground());		
		fTypeSelector.setAlignment(SWT.LEFT);
		fTypeSelector.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectionTypeChanged();
			}
		});
		
		GridUtils.createHorizontalSpacer(parent, 2);
	}

	@Override
	protected void initializeDetailsContent() {
		if (fTypeSelector == null)
			return;
		String[] types = new String[ConnectionType.values().length];
		for (int i = 0; i < ConnectionType.values().length; ++i) {
			types[i] = getLaunchElement().getConnectionTypeLabel(ConnectionType.values()[i]);
		}
		fTypeSelector.setItems(types);
		fTypeSelector.setSelection(getLaunchElement().getConnectionType().ordinal());
	}

	@Override
	public void save() {
		if (fTypeSelector == null)
			return;
		ConnectionType type = ConnectionType.values()[fTypeSelector.getSelection()];
		getLaunchElement().setConnectionType(type);
	}
	
	private void connectionTypeChanged() {
		save();
	}
	*/
}
