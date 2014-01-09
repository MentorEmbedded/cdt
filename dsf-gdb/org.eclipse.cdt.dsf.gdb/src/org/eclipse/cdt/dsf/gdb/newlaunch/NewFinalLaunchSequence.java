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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.launching.FinalLaunchSequence;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionType;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMIProcesses;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;

/**
 * @since 4.3
 */
public class NewFinalLaunchSequence extends FinalLaunchSequence {

	final private LaunchModel fLaunchModel;

	private IGDBControl fCommandControl;
	private IGDBBackend	fGDBBackend;
	private IMIProcesses fProcService;
	
	public NewFinalLaunchSequence(DsfSession session, LaunchModel launchModel, RequestMonitorWithProgress rm) {
		super(session, new HashMap<String, Object>(), rm);
		fLaunchModel = launchModel;
	}

	protected LaunchModel getLaunchModel() {
		return fLaunchModel;
	}

	@Override
	protected String[] getExecutionOrder(String group) {
		if (GROUP_TOP_LEVEL.equals(group)) {
			// Initialize the list with the base class' steps
			// We need to create a list that we can modify, which is why we create our own ArrayList.
			List<String> orderList = new ArrayList<String>(Arrays.asList(super.getExecutionOrder(GROUP_TOP_LEVEL)));

			// Now insert our steps right after the initialization of the base class.
			orderList.add(orderList.indexOf("stepInitializeFinalLaunchSequence") + 1, "stepInitializeNewFinalLaunchSequence"); //$NON-NLS-1$ //$NON-NLS-2$
			
			return orderList.toArray(new String[orderList.size()]);
		}

		return null;
	}

	@Execute
	public void stepInitializeNewFinalLaunchSequence(RequestMonitor requestMonitor) {
		DsfServicesTracker tracker = new DsfServicesTracker(GdbPlugin.getBundleContext(), getSession().getId());
		fGDBBackend = tracker.getService(IGDBBackend.class);
		fCommandControl = tracker.getService(IGDBControl.class);
		fProcService = tracker.getService(IMIProcesses.class);
		tracker.dispose();

		if (fGDBBackend == null) {
			requestMonitor.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, -1, "Cannot obtain GDBBackend service", null)); //$NON-NLS-1$
			requestMonitor.done();
			return;
		}

		if (fCommandControl == null) {
			requestMonitor.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, -1, "Cannot obtain control service", null)); //$NON-NLS-1$
			requestMonitor.done();
			return;
		}

		if (fProcService == null) {
			requestMonitor.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, -1, "Cannot obtain process service", null)); //$NON-NLS-1$
			requestMonitor.done();
			return;
		}

		requestMonitor.done();
	}

	@Override
	public void stepNewProcess(final RequestMonitor rm) {
		LaunchModel launchModel = getLaunchModel();
		String binary = null;
		Map<String, Object> attributes = new HashMap<String, Object>();
		boolean noBinarySpecified = isNoBinarySpecified();
		if (!noBinarySpecified) {
			int count = launchModel.getExecutablesCount();
			if (count > 0) {
				binary = getBinary(launchModel, 0);
				getBinaryAtrributes(launchModel, 0);
			}
		}
		
		// Even if binary is null, we must call this to do all the other steps
		// necessary to create a process.  It is possible that the binary is not needed
		fProcService.debugNewProcess(fCommandControl.getContext(), binary, attributes, 
				new DataRequestMonitor<IDMContext>(getExecutor(), rm) {
			@Override
			protected void handleCancel() {
				// If this step is cancelled, cancel the current sequence.
				// This is to allow the user to press the cancel button
				// when prompted for a post-mortem file.
				// Bug 362105
				rm.cancel();
    			rm.done();
			}
		});		
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
	
	protected String getBinary(LaunchModel launchModel, int index) {
		return null;
	}
	
	protected void getBinaryAtrributes(LaunchModel launchModel, int index) {
		
	}
}
