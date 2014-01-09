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

import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.gdb.newlaunch.services.NewGDBBackend;
import org.eclipse.cdt.dsf.gdb.newlaunch.services.NewGDBControl;
import org.eclipse.cdt.dsf.gdb.newlaunch.services.NewGDBControl_7_0;
import org.eclipse.cdt.dsf.gdb.newlaunch.services.NewGDBControl_7_2;
import org.eclipse.cdt.dsf.gdb.newlaunch.services.NewGDBControl_7_4;
import org.eclipse.cdt.dsf.gdb.newlaunch.services.NewGDBProcesses_7_2_1;
import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactoryNS;
import org.eclipse.cdt.dsf.gdb.service.IGDBHardwareAndOS;
import org.eclipse.cdt.dsf.gdb.service.IGDBTraceControl;
import org.eclipse.cdt.dsf.gdb.service.command.CommandFactory_6_8;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @since 4.3
 */
public class NewGdbServicesFactoryNS extends GdbDebugServicesFactoryNS {

	public NewGdbServicesFactoryNS(String version) {
		super(version);
	}

	@Override
	protected ICommandControl createCommandControl(DsfSession session, ILaunchConfiguration config) {
		if (GDB_7_4_VERSION.compareTo(getVersion()) <= 0) {
			return new NewGDBControl_7_4(session, config, new CommandFactory_6_8());
		}
		if (GDB_7_2_VERSION.compareTo(getVersion()) <= 0) {
			return new NewGDBControl_7_2(session, config, new CommandFactory_6_8());
		}
		if (GDB_7_0_VERSION.compareTo(getVersion()) <= 0) {
			return new NewGDBControl_7_0(session, config, new CommandFactory_6_8());
		}
		if (GDB_6_8_VERSION.compareTo(getVersion()) <= 0) {
			return new NewGDBControl_7_0(session, config, new CommandFactory_6_8());
		}
		return new NewGDBControl(session, config, new CommandFactory());
	}

	@Override
	protected IMIBackend createBackendGDBService(DsfSession session, ILaunchConfiguration config) {
		return new NewGDBBackend(session, config);
	}

	@Override
	protected IProcesses createProcessesService(DsfSession session) {
		if (GDB_7_2_1_VERSION.compareTo(getVersion()) <= 0) {
			return new NewGDBProcesses_7_2_1(session);
		}
		return super.createProcessesService( session );
	}

	@Override
	protected IGDBTraceControl createTraceControlService(DsfSession session, ILaunchConfiguration config) {
		// TODO Auto-generated method stub
		return super.createTraceControlService( session, config );
	}

	@Override
	protected IGDBHardwareAndOS createHardwareAndOSService(DsfSession session, ILaunchConfiguration config) {
		// TODO Auto-generated method stub
		return super.createHardwareAndOSService( session, config );
	}
}
