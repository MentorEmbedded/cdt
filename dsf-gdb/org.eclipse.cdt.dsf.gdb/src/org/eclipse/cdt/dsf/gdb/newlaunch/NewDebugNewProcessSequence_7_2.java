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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.ImmediateDataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.newlaunch.services.NewGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.DebugNewProcessSequence_7_2;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMICommandControl;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @since 4.6
 */
public class NewDebugNewProcessSequence_7_2 extends DebugNewProcessSequence_7_2 {

	final static public String ATTR_EXECUTABLE_ID = "executableId"; //$NON-NLS-1$

	private NewGDBBackend fBackend;
	private IGDBControl fCommandControl;
	private CommandFactory fCommandFactory;
	private String fSessionId;
	private LaunchModel fLaunchModel;
	private String fExecId;

	public NewDebugNewProcessSequence_7_2(
		DsfExecutor executor, 
		boolean isInitial, 
		IDMContext dmc, 
		String file, 
		Map<String, Object> attributes,
		DataRequestMonitor<IDMContext> rm ) {
		super(executor, isInitial, dmc, file, attributes, rm);
		fSessionId = dmc.getSessionId();
		fExecId = CDebugUtils.getAttribute(attributes, ATTR_EXECUTABLE_ID, ""); //$NON-NLS-1$
	}

	@Override
	protected String[] getExecutionOrder(String group) {
		if (GROUP_TOP_LEVEL.equals(group)) {
			// Initialize the list with the base class' steps
			// We need to create a list that we can modify, which is why we create our own ArrayList.
			List<String> orderList = new ArrayList<String>(Arrays.asList(super.getExecutionOrder(GROUP_TOP_LEVEL)));

			// Now insert our steps right after the initialization of the base class.
			orderList.add(orderList.indexOf("stepInitializeSequence_7_2") + 1, "stepNewInitializeSequence"); //$NON-NLS-1$ //$NON-NLS-2$
			
			return orderList.toArray(new String[orderList.size()]);
		}

		return null;
	}

	@Execute
	public void stepNewInitializeSequence(RequestMonitor rm) {
		DsfServicesTracker tracker = new DsfServicesTracker(GdbPlugin.getBundleContext(), fSessionId);
		fBackend = tracker.getService(NewGDBBackend.class);
		fCommandControl = tracker.getService(IGDBControl.class);
		fCommandFactory = tracker.getService(IMICommandControl.class).getCommandFactory();		
		tracker.dispose();
		
		if (fBackend == null || fCommandControl == null || fCommandFactory == null) {
			rm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, IDsfStatusConstants.INTERNAL_ERROR, "Cannot obtain service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}
		
		fLaunchModel = fBackend.getLaunchModel();

		rm.done();
	}

	@Execute
	@Override
	public void stepRemoteConnection(RequestMonitor rm) {
		rm.done();
	}

	@Execute
	@Override
	public void stepSetRemoteExecutable(RequestMonitor rm) {
		if (fExecId != null && fLaunchModel.getSessionType() == SessionType.REMOTE && fLaunchModel.isNonStop()) {
			String remoteBinary = fLaunchModel.getRemoteExecutablePath(fExecId);
			if (remoteBinary.length() == 0) {
				rm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, "Binary on host is not specified")); //$NON-NLS-1$
				rm.done();
				return;
			}

			fCommandControl.queueCommand(
					fCommandFactory.createMIGDBSet(
							getContainerContext(), 
							new String[] {
								"remote", //$NON-NLS-1$
								"exec-file", //$NON-NLS-1$
								remoteBinary,
							}), 
					new ImmediateDataRequestMonitor<MIInfo>(rm));
		}
		else {
			rm.done();
		}
	}
}
