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

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.model.IConnectHandler;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateDataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.gdb.actions.IConnect;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.launching.FinalLaunchSequence_7_2;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionType;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMIProcesses;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;

/**
 * @since 4.3
 */
public class NewFinalLaunchSequence_7_2 extends FinalLaunchSequence_7_2 {

	final private LaunchModel fLaunchModel;
	
	private IGDBControl fCommandControl;
	private IGDBBackend	fGDBBackend;
	private IMIProcesses fProcService;
	private CommandFactory fCommandFactory;
	
	public NewFinalLaunchSequence_7_2(DsfSession session, LaunchModel launchModel, RequestMonitorWithProgress rm) {
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
			orderList.add(orderList.indexOf("stepInitializeFinalLaunchSequence_7_2") + 1, "stepInitializeNewFinalLaunchSequence"); //$NON-NLS-1$ //$NON-NLS-2$
			
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

		fCommandFactory = fCommandControl.getCommandFactory();

		requestMonitor.done();
	}

	@Execute
	@Override
	public void stepRemoteConnection(RequestMonitor rm) {
		if (fGDBBackend.getSessionType() == SessionType.REMOTE) {
			ConnectionType type = getRemoteConnectionType();
			boolean isTcpConnection = ConnectionType.TCP == type;

			if (isTcpConnection) {
				String remoteTcpHost = getTcpHost();
				String remoteTcpPort = getTcpPort();

				fCommandControl.queueCommand(
						fCommandFactory.createMITargetSelect(fCommandControl.getContext(), 
								remoteTcpHost, remoteTcpPort, getLaunchModel().isNonStop()), 
								new ImmediateDataRequestMonitor<MIInfo>(rm));
			} else {
				String serialDevice = getSerialDevice();

				fCommandControl.queueCommand(
						fCommandFactory.createMITargetSelect(fCommandControl.getContext(), 
								serialDevice, true), 
								new ImmediateDataRequestMonitor<MIInfo>(rm));
			}
		} else {
			rm.done();
		}
	}

	@Execute
	@Override
	public void stepAttachToProcess(RequestMonitor requestMonitor) {
		// This step is not used
		requestMonitor.done();
	}

	@Execute
	@Override
	public void stepNewProcess(final RequestMonitor rm) {
		if (getLaunchModel().getSessionType() == SessionType.CORE) {
			createPostMortemProcess(rm);
		}
		else {
			createProcesses(rm);
		}
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

	@Override
	protected boolean debugOnFork() {
		// TODO Auto-generated method stub
		return super.debugOnFork();
	}
	
	protected void getBinaryAttributes(LaunchModel launchModel, String execId, Map<String, Object> attributes) {
		// Executable id
		attributes.put(NewDebugNewProcessSequence_7_2.ATTR_EXECUTABLE_ID, execId);
		
		// Arguments
		String args = launchModel.getArguments(execId);
		attributes.put(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (args != null) ? args : ""); //$NON-NLS-1$
		
		// Stop on startup 
		String stopOnStartupSymbol = launchModel.getStopOnStartupSymbol(execId);
		attributes.put(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, stopOnStartupSymbol != null);
		if (stopOnStartupSymbol != null) {
			attributes.put(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL, stopOnStartupSymbol);
		}
		
		String coreFile = launchModel.getCoreFile(execId);
		if (coreFile != null && !coreFile.isEmpty()) {
			attributes.put(ICDTLaunchConfigurationConstants.ATTR_COREFILE_PATH, coreFile);
		}
	}
	
	private void createProcesses(final RequestMonitor rm) {
		final LaunchModel launchModel = getLaunchModel();
		String[] execIds = launchModel.getExecutables();
		int count = execIds.length;
		// In all-stop mode we can only debug one process, so we chose the first executable; 
		if (count == 1) {
			String binary = null;
			Map<String, Object> attributes = new HashMap<String, Object>();
			boolean noBinarySpecified = isNoBinarySpecified();
			if (!noBinarySpecified) {
				if (count > 0) {
					binary = launchModel.getExecutablePath(execIds[0]);
					getBinaryAttributes(launchModel, execIds[0], attributes);
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
		else {
			final Step[] steps = new Step[count];
			for (int i = 0; i < count; ++i) {
				final String execId = execIds[i];
				steps[i] = new Step() {
					@Override
					public void execute(final RequestMonitor rm) {
						String binary = launchModel.getExecutablePath(execId);
						Map<String, Object> attributes = new HashMap<String, Object>();
						getBinaryAttributes(launchModel, execId, attributes);
						if (launchModel.isAttach(execId)) {
							int pid = getPid();
							if (pid != -1) {
								fProcService.attachDebuggerToProcess(
										fProcService.createProcessContext(fCommandControl.getContext(), Integer.toString(pid)),
										new DataRequestMonitor<IDMContext>(getExecutor(), rm));
							} 
							else {
								IConnectHandler connectCommand = (IConnectHandler)getSession().getModelAdapter(IConnectHandler.class);
								if (connectCommand instanceof IConnect) {
									((IConnect)connectCommand).connect(rm);
								} 
								else {
									rm.done();
								}
							}
						}
						else {
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
					}
				};
			}
			
			getExecutor().execute(new Sequence(getExecutor(), rm) {
				
				@Override
				public Step[] getSteps() {
					return steps;
				}
			});
		}
	}
	
	private void createPostMortemProcess(final RequestMonitor rm) {
		final LaunchModel launchModel = getLaunchModel();
		String execId = launchModel.getCoreExecutable();
		if (execId == null) {
			rm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, "Executable is not specified"));
			rm.done();
			return;
		}
		Map<String, Object> attributes = new HashMap<String, Object>();
		String binary = launchModel.getExecutablePath(execId);
		getBinaryAttributes(launchModel, execId, attributes);
		
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
}
