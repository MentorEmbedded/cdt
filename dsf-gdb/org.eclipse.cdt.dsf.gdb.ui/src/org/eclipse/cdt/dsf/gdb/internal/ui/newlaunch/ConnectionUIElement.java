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

import org.eclipse.cdt.debug.ui.dialogs.PillsControl;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionType;
import org.eclipse.cdt.dsf.gdb.newlaunch.SerialConnectionElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.TCPConnectionElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ConnectionUIElement extends AbstractUIElement {

	// Summary widgets
	private Label fSummaryText;
	
	// Details widgets
	private PillsControl fTypeSelector;

	public ConnectionUIElement(ConnectionElement launchElement, boolean showDetails ) {
		super(launchElement, showDetails);
	}

	@Override
	public ConnectionElement getLaunchElement() {
		return (ConnectionElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fSummaryText = null;
		fTypeSelector = null;
	}

	@Override
	protected void doCreateSummaryContent(Composite parent) {
		fSummaryText = new Label(parent, SWT.NONE);
		fSummaryText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
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
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label label = new Label(comp, SWT.NONE);
		label.setText("Connection type: ");
		
		fTypeSelector = new PillsControl(comp, SWT.NONE);
		fTypeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fTypeSelector.setBackground(parent.getBackground());		
		fTypeSelector.setAlignment(SWT.LEFT);
		fTypeSelector.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectionTypeChanged();
			}
		});
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
}
