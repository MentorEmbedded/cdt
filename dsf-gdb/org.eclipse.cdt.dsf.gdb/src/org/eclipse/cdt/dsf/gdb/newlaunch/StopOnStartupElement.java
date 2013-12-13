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

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 4.3
 */
public class StopOnStartupElement extends AbstractLaunchElement {
	final private static String ELEMENT_ID = ".stopOnStartup"; //$NON-NLS-1$

	final private static String ATTR_STOP = ".stop"; //$NON-NLS-1$
	final private static String ATTR_STOP_SYMBOL = ".symbol"; //$NON-NLS-1$

	final private static String DEFAULT_STOP_SYMBOL = "main"; //$NON-NLS-1$

	private boolean fStop = true;
	private String fStopSymbol = DEFAULT_STOP_SYMBOL;

	public StopOnStartupElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Stop On Startup", "Stop on startup");
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		try {
			fStop = config.getAttribute(getId() + ATTR_STOP, true);
			fStopSymbol = config.getAttribute(getId() + ATTR_STOP_SYMBOL, DEFAULT_STOP_SYMBOL);
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(getId() + ATTR_STOP, fStop);
		config.setAttribute(getId() + ATTR_STOP_SYMBOL, fStopSymbol);
	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		fStop = true;
		fStopSymbol = DEFAULT_STOP_SYMBOL;
		config.setAttribute(getId() + ATTR_STOP, fStop);
		config.setAttribute(getId() + ATTR_STOP_SYMBOL, fStopSymbol);
	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		if (fStop && fStopSymbol.isEmpty()) {
			setErrorMessage("Stop symbol is not specified");
			return false;
		}
		return true;
	}

	public boolean isStop() {
		return fStop;
	}

	public void setStop(boolean stop) {
		if (fStop == stop)
			return;
		fStop = stop;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public String getStopSymbol() {
		return fStopSymbol;
	}

	public void setStopSymbol(String stopSymbol) {
		if (fStopSymbol.equals(stopSymbol))
			return;
		fStopSymbol = stopSymbol;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof SessionTypeChangeEvent) {
			handleSessionTypeChange((SessionTypeChangeEvent)event);
		}
		super.update(event);
	}
	
	private void handleSessionTypeChange(SessionTypeChangeEvent event) {
		 setEnabled(!SessionType.CORE.equals(event.getNewType()));
	}
}
