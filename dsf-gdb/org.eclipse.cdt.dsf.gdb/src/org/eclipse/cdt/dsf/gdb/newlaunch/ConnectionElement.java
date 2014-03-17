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

import java.util.Map;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;

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
	protected void doCreateChildren(Map<String, Object> attributes) {
		addChildren(new ILaunchElement[] {
			new TCPConnectionElement(this),
			new SerialConnectionElement(this),
		});
	}

	@Override
	public void initialiazeFrom(Map<String, Object> attributes) {
		ConnectionType oldType = getConnectionType();
		super.initialiazeFrom(attributes);
		update(new ConnectionTypeChangeEvent(this, getConnectionType(), oldType));
	}

	@Override
	public void setDefaults(Map<String, Object> attributes) {
		ConnectionType oldType = getConnectionType();
		super.setDefaults(attributes);
		update(new ConnectionTypeChangeEvent(this, getConnectionType(), oldType));
	}

	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		int connType = getAttribute(attributes, getId() + ATTR_TYPE, getDefaultConnectionType().ordinal());
		if (connType < 0 || connType > ConnectionType.values().length) {
			setErrorMessage("Invalid connection type");
		}
		else {
			fType = ConnectionType.values()[connType];
		}
	}

	@Override
	protected void doPerformApply(Map<String, Object> attributes) {
		attributes.put(getId() + ATTR_TYPE, fType.ordinal());
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		fType = getDefaultConnectionType();
		attributes.put(getId() + ATTR_TYPE, fType.ordinal());
	}

	@Override
	protected boolean isContentValid() {
		return true;
	}

	public ConnectionType getConnectionType() {
		return fType;
	}

	public static ConnectionType getDefaultConnectionType() {
		return ConnectionType.TCP;
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
		//elementChanged(CHANGE_DETAIL_CONTENT | CHANGE_DETAIL_STATE);
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
