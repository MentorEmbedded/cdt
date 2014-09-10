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

import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.newlaunch.services.NewGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.gdb.service.StartOrRestartProcessSequence_7_0;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;

/**
 * @since 4.6
 */
public class NewStartOrRestartProcessSequence extends StartOrRestartProcessSequence_7_0 {

	public NewStartOrRestartProcessSequence(
		DsfExecutor executor, 
		IContainerDMContext containerDmc, 
		Map<String, Object> attributes, 
		boolean restart,
		DataRequestMonitor<IContainerDMContext> rm ) {
		super(executor, containerDmc, attributes, restart, rm);
	}

	@Override
	protected boolean useContinueCommand() {
		DsfServicesTracker tracker = new DsfServicesTracker(GdbPlugin.getBundleContext(), getContainerContext().getSessionId());
		NewGDBBackend backend = tracker.getService(NewGDBBackend.class);
		if (backend == null) {
			return false;
		}
		return backend.getSessionType() == SessionType.REMOTE && !backend.isNonStop();
	}
}
