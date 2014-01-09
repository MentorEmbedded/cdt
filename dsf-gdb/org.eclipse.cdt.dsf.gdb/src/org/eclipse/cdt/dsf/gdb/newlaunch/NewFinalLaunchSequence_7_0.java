/*******************************************************************************
 * Copyright (c) 2014 Mentor Graphics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mentor Graphics - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.dsf.gdb.newlaunch;

import java.util.HashMap;

import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.launching.FinalLaunchSequence_7_0;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionType;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.variables.IStringVariableManager;

/**
 * @since 4.3
 */
public class NewFinalLaunchSequence_7_0 extends FinalLaunchSequence_7_0 {

	final private LaunchModel fLaunchModel;
	
	public NewFinalLaunchSequence_7_0(DsfSession session, LaunchModel launchModel, RequestMonitorWithProgress rm) {
		super(session, new HashMap<String, Object>(), rm);
		fLaunchModel = launchModel;
	}

	protected LaunchModel getLaunchModel() {
		return fLaunchModel;
	}

	@Override
	protected int getPid() {
		return super.getPid();
	}

	@Override
	protected boolean isNoBinarySpecified() {
		return super.isNoBinarySpecified();
	}

	@Override
	protected ConnectionType getRemoteConnectionType() {
		return getLaunchModel().getConnectionType();
	}

	@Override
	protected String getTcpHost() {
		return getLaunchModel().getTCPHost();
	}

	@Override
	protected String getTcpPort() {
		return getLaunchModel().getTCPPort();
	}

	@Override
	protected String getSerialDevice() {
		return getLaunchModel().getSerialDevice();
	}

	@Override
	protected boolean autoLoadSolibSymbols() {
		// TODO Auto-generated method stub
		return super.autoLoadSolibSymbols();
	}

	@Override
	protected boolean isNonStop() {
		return getLaunchModel().isNonStop();
	}

	@Override
	protected IStringVariableManager getStringSubstitutor() {
		// TODO Auto-generated method stub
		return super.getStringSubstitutor();
	}
}
