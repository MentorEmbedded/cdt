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
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class ConnectionElement extends AbstractLaunchElement {

	public enum ConnectionType {
		TCP,
		SERIAL
	}

	public class ConnectionTypeChangeEvent extends ChangeEvent {

		private ConnectionType fNewType;
		private ConnectionType fOldType;
		
		public ConnectionTypeChangeEvent(AbstractLaunchElement source, ConnectionType newType, ConnectionType oldType) {
			super(source);
			fNewType = newType;
			fOldType = oldType;
		}

		public ConnectionType getNewType() {
			return fNewType;
		}

		public ConnectionType getOldType() {
			return fOldType;
		}
	}

	final private static String ELEMENT_ID = ".connection"; //$NON-NLS-1$
	final private static String ATTR_TYPE = ".type"; //$NON-NLS-1$

	private ConnectionType fType = ConnectionType.TCP;

	public ConnectionElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Connection", "Connection settings");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
		addChildren(new ILaunchElement[] {
			new TCPConnectionElement(this),
			new SerialConnectionElement(this),
		});
	}

	@Override
	public void initialiazeFrom(ILaunchConfiguration config) {
		ConnectionType oldType = getConnectionType();
		super.initialiazeFrom(config);
		update(new ConnectionTypeChangeEvent(this, getConnectionType(), oldType));
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		ConnectionType oldType = getConnectionType();
		super.setDefaults(config);
		update(new ConnectionTypeChangeEvent(this, getConnectionType(), oldType));
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		try {
			int connType = config.getAttribute(getId() + ATTR_TYPE, ConnectionType.TCP.ordinal());
			if (connType < 0 || connType > ConnectionType.values().length) {
				setErrorMessage("Invalid connection type");
			}
			else {
				fType = ConnectionType.values()[connType];
			}
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(ATTR_TYPE, fType.ordinal());
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		fType = ConnectionType.TCP;
		config.setAttribute(ATTR_TYPE, ConnectionType.TCP.ordinal());
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		return true;
	}

	public ConnectionType getConnectionType() {
		return fType;
	}

	public String getConnectionTypeLabel(ConnectionType type) {
		if (ConnectionType.TCP.equals(type)) {
			return "TCP";
		}
		if (ConnectionType.SERIAL.equals(type)) {
			return "Serial";
		}
		return "Unknown";
	}

	public void setConnectionType(ConnectionType type) {
		if (fType == type)
			return;
		ConnectionType oldType = fType;
		fType = type;
		update(new ConnectionTypeChangeEvent(this, getConnectionType(), oldType));
		elementChanged(CHANGE_DETAIL_CONTENT | CHANGE_DETAIL_STATE);
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof SessionTypeChangeEvent) {
			handleSessionTypeChange((SessionTypeChangeEvent)event);
		}
		super.update(event);
	}

	private void handleSessionTypeChange(SessionTypeChangeEvent event) {
		 setEnabled(SessionType.REMOTE.equals(event.getNewType()));
	}
}
