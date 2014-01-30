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

import java.util.Hashtable;
import java.util.List;

import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.newlaunch.LaunchModel;
import org.eclipse.cdt.dsf.gdb.service.GDBBackend;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.mi.service.IMIBackend2;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;

public class NewGDBBackend extends GDBBackend {

	final private ILaunchConfiguration fLaunchConfiguration;
	private LaunchModel fLaunchModel;

	public NewGDBBackend(DsfSession session, ILaunchConfiguration config) {
		super(session, config);
		fLaunchConfiguration = config;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final RequestMonitor rm) {
		try {
			fLaunchModel = LaunchModel.create(fLaunchConfiguration.getAttributes());
		}
		catch(CoreException e) {
			rm.setStatus(e.getStatus());
			rm.done();
			return;
		}
		super.initialize(new ImmediateRequestMonitor(rm) {
			@Override
			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	private void doInitialize(RequestMonitor rm) {
		register(
			new String[] { 
				IMIBackend.class.getName(),
		        IMIBackend2.class.getName(),
				GDBBackend.class.getName(),
				NewGDBBackend.class.getName()
			},
			new Hashtable<String, String>());
		rm.done();
	}

	@Override
	public void shutdown(RequestMonitor requestMonitor) {
		unregister();
		if (fLaunchModel != null) {
			fLaunchModel.dispose();
		}
		super.shutdown(requestMonitor);
	}

	@Override
	protected IPath getGDBPath() {
		return new Path(fLaunchModel.getGDBPath());
	}

	@Override
	public String getGDBInitFile() throws CoreException {
		return fLaunchModel.getGDBInitFile();
	}

	@Override
	public IPath getGDBWorkingDirectory() throws CoreException {
		return new Path(fLaunchModel.getGDBWorkingDirectory());
	}

	@Override
	public List<String> getSharedLibraryPaths() throws CoreException {
		return fLaunchModel.getSharedLibraryPaths();
	}

	@Override
	public boolean getClearEnvironment() throws CoreException {
		// TODO Auto-generated method stub
		return super.getClearEnvironment();
	}

	@Override
	public boolean getUpdateThreadListOnSuspend() throws CoreException {
		// TODO Auto-generated method stub
		return super.getUpdateThreadListOnSuspend();
	}

	@Override
	public SessionType getSessionType() {
		return fLaunchModel.getSessionType();
	}
	
	public boolean isNonStop() {
		return fLaunchModel.isNonStop();
	}

	public LaunchModel getLaunchModel() {
		return fLaunchModel;
	}
}
