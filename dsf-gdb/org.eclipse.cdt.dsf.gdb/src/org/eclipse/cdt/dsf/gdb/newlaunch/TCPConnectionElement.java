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

package org.eclipse.cdt.dsf.gdb.newlaunch;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionType;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionTypeChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class TCPConnectionElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".tcp"; //$NON-NLS-1$
	final private static String ATTR_HOST_NAME = ".hostName"; //$NON-NLS-1$
	final private static String ATTR_PORT_NUMBER = ".portNumber"; //$NON-NLS-1$

	final private static String DEFAULT_HOST_NAME = "localhost"; //$NON-NLS-1$
	final private static String DEFAULT_PORT_NUMBER = "10000"; //$NON-NLS-1$

	private String fHostName = DEFAULT_HOST_NAME;
	private String fPortNumber = DEFAULT_PORT_NUMBER;

	public TCPConnectionElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "TCP", "TCP Connection");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		try {
			fHostName = config.getAttribute(getId() + ATTR_HOST_NAME, DEFAULT_HOST_NAME);
			fPortNumber = config.getAttribute(getId() + ATTR_PORT_NUMBER, DEFAULT_PORT_NUMBER);
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_HOST_NAME, fHostName);
		config.setAttribute(getId() + ATTR_PORT_NUMBER, fPortNumber);
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		fHostName = DEFAULT_HOST_NAME;
		fPortNumber = DEFAULT_PORT_NUMBER;
		config.setAttribute(getId() + ATTR_HOST_NAME, DEFAULT_HOST_NAME);
		config.setAttribute(getId() + ATTR_PORT_NUMBER, DEFAULT_PORT_NUMBER);
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		if (fHostName.isEmpty()) {
			setErrorMessage("Host name or IP address must be specified.");
		}
		else if (!hostNameIsValid(fHostName)) {
			setErrorMessage("Invalid host name or IP address.");
		}
		else if (fPortNumber.isEmpty()) {
			setErrorMessage("Port number must be specified.");
			return false;
		}
		else if (!portNumberIsValid(fPortNumber)) {
			setErrorMessage("Invalid port number.");
		}
		return super.getInternalErrorMessage() == null;
	}

	protected boolean hostNameIsValid(String hostName) {
		return true;
	}

	protected boolean portNumberIsValid(String portNumber) {
		try {
			int port = Integer.parseInt(portNumber);
			return (port > 0 && port <= 0xFFFF);
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	public String getHostName() {
		return fHostName;
	}

	public void setHostName(String hostName) {
		if (fHostName.equals(hostName))
			return;
		fHostName = hostName;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public String getPortNumber() {
		return fPortNumber;
	}

	public void setPortNumber(String portNumber) {
		if (fPortNumber.equals(portNumber))
			return;
		fPortNumber = portNumber;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof ConnectionTypeChangeEvent) {
			handleConnectionTypeChange((ConnectionTypeChangeEvent)event);
		}
		super.update(event);
	}
	
	private void handleConnectionTypeChange(ConnectionTypeChangeEvent event) {
		 setEnabled(ConnectionType.TCP.equals(event.getNewType()));
	}
}