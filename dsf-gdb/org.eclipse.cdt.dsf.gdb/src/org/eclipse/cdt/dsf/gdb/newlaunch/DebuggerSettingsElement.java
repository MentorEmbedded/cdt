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

import java.util.Map;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement.SessionTypeChangeEvent;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.Platform;

/**
 * @since 4.3
 */
public class DebuggerSettingsElement extends AbstractLaunchElement {

	public enum TracepointMode {
		FAST_ONLY,
		NORMAL_ONLY,
		AUTOMATIC,
	}

	final private static String ELEMENT_ID = ".advancedSettings"; //$NON-NLS-1$
	final private static String ATTR_NON_STOP = ".nonStop"; //$NON-NLS-1$
	final private static String ATTR_REVERSE = ".reverse"; //$NON-NLS-1$
	final private static String ATTR_DEBUG_ON_FORK = ".debugOnFork"; //$NON-NLS-1$
	final private static String ATTR_TRACEPOINT_MODE = ".tracepointMode"; //$NON-NLS-1$

	private boolean fNonStop = isNonStopDefault();
	private boolean fReverse = getDefaultReverseValue();
	private boolean fDebugOnFork = getDefaultDebugOnForkValue();
	private TracepointMode fTracepointMode = getDefaultTracepointMode();

	public DebuggerSettingsElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Advanced Settings", "Debugger advanced settings");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
		addChildren(new ILaunchElement[] {
		});
	}

	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		fNonStop = getAttribute(attributes, getId() + ATTR_NON_STOP, fNonStop);
		fReverse = getAttribute(attributes, getId() + ATTR_REVERSE, getDefaultReverseValue());
		fDebugOnFork = getAttribute(attributes, getId() + ATTR_DEBUG_ON_FORK, getDefaultDebugOnForkValue());
		fTracepointMode = TracepointMode.values()[getAttribute(attributes, getId() + ATTR_TRACEPOINT_MODE, getDefaultTracepointMode().ordinal())];
	}

	@Override
	protected void doPerformApply(Map<String, Object> attributes) {
		attributes.put(getId() + ATTR_NON_STOP, fNonStop);		
		attributes.put(getId() + ATTR_REVERSE, fReverse);
		attributes.put(getId() + ATTR_DEBUG_ON_FORK, fDebugOnFork);
		attributes.put(getId() + ATTR_TRACEPOINT_MODE, fTracepointMode.ordinal());
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		fNonStop = isNonStopDefault();
		fReverse = getDefaultReverseValue();
		fDebugOnFork = getDefaultDebugOnForkValue();
		fTracepointMode = getDefaultTracepointMode();
		attributes.put(getId() + ATTR_NON_STOP, fNonStop);
		attributes.put(getId() + ATTR_REVERSE, fReverse);
		attributes.put(getId() + ATTR_DEBUG_ON_FORK, fDebugOnFork);
		attributes.put(getId() + ATTR_TRACEPOINT_MODE, fTracepointMode.ordinal());
		
	}

	@Override
	protected boolean isContentValid() {
		return true;
	}
	
	public static boolean isNonStopDefault() {
		return Platform.getPreferencesService().getBoolean(
			GdbPlugin.PLUGIN_ID, IGdbDebugPreferenceConstants.PREF_DEFAULT_NON_STOP, false, null);
	}
	
	public static boolean getDefaultReverseValue() {
		return IGDBLaunchConfigurationConstants.DEBUGGER_REVERSE_DEFAULT;
	}
	
	public static boolean getDefaultUpdateValue() {
		return IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT;
	}
	
	public static boolean getDefaultDebugOnForkValue() {
		return IGDBLaunchConfigurationConstants.DEBUGGER_DEBUG_ON_FORK_DEFAULT;
	}
	
	public static TracepointMode getDefaultTracepointMode() {
		if (IGDBLaunchConfigurationConstants.DEBUGGER_TRACEPOINT_MODE_DEFAULT.equals(
				IGDBLaunchConfigurationConstants.DEBUGGER_TRACEPOINT_FAST_ONLY)) {
			return TracepointMode.FAST_ONLY;
		}
		if (IGDBLaunchConfigurationConstants.DEBUGGER_TRACEPOINT_MODE_DEFAULT.equals(
				IGDBLaunchConfigurationConstants.DEBUGGER_TRACEPOINT_NORMAL_ONLY)) {
			return TracepointMode.NORMAL_ONLY;
		}
		return TracepointMode.AUTOMATIC;
	}
	
	
	// FIXME: non-stop is not sensible for core sessions. Restore this check:
	// setEnabled(!SessionType.CORE.equals(event.getNewType()));
	public boolean isNonStop() {
		return fNonStop;
	}

	public void setNonStop(boolean nonStop) {
		if (fNonStop == nonStop)
			return;
		fNonStop = nonStop;
		elementChanged(CHANGE_DETAIL_STATE);
	}	

	public boolean isReverseEnabled() {
		return fReverse;
	}

	public void enableReverse(boolean reverse) {
		if (reverse == fReverse) {
			return;
		}
		fReverse = reverse;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public boolean isDebugOnFork() {
		return fDebugOnFork;
	}

	public void setDebugOnFork(boolean debugOnFork) {
		if (fDebugOnFork == debugOnFork) {
			return;
		}
		fDebugOnFork = debugOnFork;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public TracepointMode getTracepointMode() {
		return fTracepointMode;
	}

	public void setTracepointMode(TracepointMode tracepointMode) {
		if (fTracepointMode == tracepointMode ) {
			return;
		}
		fTracepointMode = tracepointMode;
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
