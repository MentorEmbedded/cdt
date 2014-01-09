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

package org.eclipse.cdt.dsf.gdb.newlaunch.services;

import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.newlaunch.LaunchModel;
import org.eclipse.cdt.dsf.gdb.newlaunch.NewDebugNewProcessSequence_7_2;
import org.eclipse.cdt.dsf.gdb.newlaunch.NewStartOrRestartProcessSequence;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_2_1;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class NewGDBProcesses_7_2_1 extends GDBProcesses_7_2_1 {

	private NewGDBBackend fBackend;

	public NewGDBProcesses_7_2_1(DsfSession session) {
		super(session);
	}

	@Override
	public void initialize(final RequestMonitor requestMonitor) {
		super.initialize(new ImmediateRequestMonitor(requestMonitor) {
			@Override
			protected void handleSuccess() {
				doInitialize(requestMonitor);
			}
		});
	}

	private void doInitialize(RequestMonitor rm) {
    	fBackend = getServicesTracker().getService(NewGDBBackend.class);
    	if (fBackend == null) {
			rm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, "Service is not available")); //$NON-NLS-1$
    	}
    	rm.done();
	}

	@Override
	protected Sequence getDebugNewProcessSequence(
		DsfExecutor executor, 
		boolean isInitial, 
		IDMContext dmc, 
		String file, 
		Map<String, Object> attributes,
		DataRequestMonitor<IDMContext> rm) {

		return new NewDebugNewProcessSequence_7_2(executor, isInitial, dmc, file, attributes, rm);
	}

	@Override
	protected Sequence getStartOrRestartProcessSequence(
		DsfExecutor executor, 
		IContainerDMContext containerDmc, 
		Map<String, Object> attributes,
		boolean restart, 
		DataRequestMonitor<IContainerDMContext> rm) {

		return new NewStartOrRestartProcessSequence(executor, containerDmc, attributes, restart, rm);
	}

	@Override
	protected boolean doIsDebuggerAttachSupported() {
		LaunchModel launchModel = fBackend.getLaunchModel();
		if (launchModel.getSessionType() == SessionType.CORE) {
			return false;
		}
		if (!launchModel.isNonStop() && getNumConnected() > 0) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean doIsDebugNewProcessSupported() {
		LaunchModel launchModel = fBackend.getLaunchModel();
		if (launchModel.getSessionType() == SessionType.CORE) {
			return false;
		}
		if (!launchModel.isNonStop() && getNumConnected() > 0) {
			return false;
		}
		return true;
	}
}
