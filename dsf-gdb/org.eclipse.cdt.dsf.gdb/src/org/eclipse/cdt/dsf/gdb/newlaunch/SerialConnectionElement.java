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

import java.util.Arrays;
import java.util.Map;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionType;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionTypeChangeEvent;

/**
 * @since 4.3
 */
public class SerialConnectionElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".serial"; //$NON-NLS-1$
	
	final private static String ATTR_DEVICE = ".device"; //$NON-NLS-1$
	final private static String ATTR_DEVICE_SPEED = ".deviceSpeed"; //$NON-NLS-1$
	
	final private static String DEFAULT_DEVICE = "/dev/ttyS0"; //$NON-NLS-1$
	final private static String DEFAULT_DEVICE_SPEED = "115200"; //$NON-NLS-1$

	final private static String fgSpeedChoices[] = { 
		"9600",  //$NON-NLS-1$
		"19200",  //$NON-NLS-1$
		"38400",  //$NON-NLS-1$
		"57600",  //$NON-NLS-1$
		"115200"  //$NON-NLS-1$
	};

	private String fDevice = DEFAULT_DEVICE;
	private String fSpeed = DEFAULT_DEVICE_SPEED;

	public SerialConnectionElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Serial", "Serial connection");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
	}

	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		fDevice = getAttribute(attributes, getId() + ATTR_DEVICE, DEFAULT_DEVICE);
		fSpeed = getAttribute(attributes, getId() + ATTR_DEVICE_SPEED, DEFAULT_DEVICE_SPEED);
	}

	@Override
	protected void doPerformApply(Map<String, Object> attributes) {
		attributes.put(getId() + ATTR_DEVICE, fDevice);
		attributes.put(getId() + ATTR_DEVICE_SPEED, fSpeed);
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		fDevice = DEFAULT_DEVICE;
		fSpeed = DEFAULT_DEVICE_SPEED;
		attributes.put(getId() + ATTR_DEVICE, DEFAULT_DEVICE);
		attributes.put(getId() + ATTR_DEVICE_SPEED, DEFAULT_DEVICE_SPEED);
	}

	@Override
	protected boolean isContentValid() {
		setErrorMessage(null);
		if (fDevice.isEmpty()) {
			setErrorMessage("Device must be specified.");
		}
		else if (isValidDevice(fDevice)) {
			setErrorMessage("Invalid device.");
		}
		else if (isValidDeviceSpeed(fSpeed)) {
			setErrorMessage("INvalid device speed.");
		}
		return getInternalErrorMessage() == null;
	}

	protected boolean isValidDevice(String device) {
		return true;
	}

	protected boolean isValidDeviceSpeed(String speed) {
		return Arrays.asList(getDeviceSpeedChoices()).contains(speed);
	}
	
	public String[] getDeviceSpeedChoices() {
		return fgSpeedChoices;
	}

	public static String getDefaultDevice() {
		return DEFAULT_DEVICE;
	}

	public String getDevice() {
		return fDevice;
	}

	public void setDevice(String device) {
		if (fDevice.equals(device))
			return;
		fDevice = device;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public String getSpeed() {
		return fSpeed;
	}

	public static String getDefaultSpeed() {
		return DEFAULT_DEVICE_SPEED;
	}

	public void setDeviceSpeed(String speed) {
		if (fSpeed.equals(speed))
			return;
		fSpeed = speed;
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
		 setEnabled(ConnectionType.SERIAL.equals(event.getNewType()));
	}
}
