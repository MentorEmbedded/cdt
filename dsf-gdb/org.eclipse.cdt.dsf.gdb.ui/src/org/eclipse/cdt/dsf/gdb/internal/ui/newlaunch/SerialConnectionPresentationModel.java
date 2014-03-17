package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.SerialConnectionElement;
import org.eclipse.cdt.ui.grid.CompositePresentationModel;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.cdt.ui.grid.StringPresentationModel;

public class SerialConnectionPresentationModel extends CompositePresentationModel {
	
	public SerialConnectionPresentationModel(final SerialConnectionElement launchElement)
	{		
		super();
	
		StringPresentationModel device = new StringPresentationModel(LaunchUIMessages.getString("SerialPortSettingsBlock.0")) { //$NON-NLS-1$

			@Override
			protected String doGetValue() {
				return launchElement.getDevice();
			}

			@Override
			protected void doSetValue(String value) {
				launchElement.setDevice(value.trim());
			}
		};
		add(device);

		SelectionPresentationModel speed = new SelectionPresentationModel(LaunchUIMessages.getString("SerialPortSettingsBlock.1")) {

			@Override
			public List<String> getPossibleValues() {
				return Arrays.asList(launchElement.getDeviceSpeedChoices());
			}

			@Override
			protected String doGetValue() {
				return launchElement.getSpeed();
			};

			@Override
			protected void doSetValue(String value) {
				launchElement.setDeviceSpeed(value.trim());
			};
		};
		add(speed);
	}
}
