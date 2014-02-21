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

package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import java.util.Arrays;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerSettingsElement;
import org.eclipse.cdt.ui.grid.BooleanPresentationModel;
import org.eclipse.cdt.ui.grid.CheckboxViewElement;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.PillSelectionViewElement;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.swt.widgets.Composite;

public class DebuggerSettingsUIElement extends GridElement {

	private static String[] fgTraceModeLabels = new String[] {
		LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_fast"), //$NON-NLS-1$
		LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_normal"), //$NON-NLS-1$
		LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_auto"), //$NON-NLS-1$
	};

	private DebuggerSettingsElement launchElement;
	private boolean showDetails;
	
	public DebuggerSettingsUIElement(DebuggerSettingsElement launchElement, boolean showDetails) {
		this.launchElement = launchElement;
		this.showDetails = showDetails;
	}
	
	@Override
	protected void createImmediateContent(Composite parent) {	
	}
	
	@Override
	protected void populateChildren() {
		BooleanPresentationModel reverseModel;
		reverseModel = new BooleanPresentationModel(LaunchUIMessages.getString("GDBDebuggerPage.reverse_Debugging")) {
			
			@Override
			protected void doSetValue(boolean value) {
				launchElement.enableReverse(value);
			}
			
			@Override
			protected boolean doGetValue() {
				return launchElement.isReverseEnabled();
			}
		};
		addChild((new CheckboxViewElement(reverseModel)));
		
		BooleanPresentationModel debugOnFork;
		debugOnFork = new BooleanPresentationModel(LaunchUIMessages.getString("GDBDebuggerPage.Automatically_debug_forked_processes")) {
			
			@Override
			protected void doSetValue(boolean value) { launchElement.setDebugOnFork(value); }
			
			@Override
			protected boolean doGetValue() { return launchElement.isDebugOnFork(); }
		};
		
		addChild((new CheckboxViewElement(debugOnFork)));
		
		SelectionPresentationModel tracepointModel;
		tracepointModel = new SelectionPresentationModel(LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_label"), Arrays.asList(fgTraceModeLabels)) {
			
			@Override
			protected String doGetValue() { return fgTraceModeLabels[launchElement.getTracepointMode().ordinal()]; }
			
			@Override
			protected void doSetValue(String value) {
				int index = Arrays.asList(fgTraceModeLabels).indexOf(value);
				launchElement.setTracepointMode(DebuggerSettingsElement.TracepointMode.values()[index]);				
			}
		};
		addChild(new PillSelectionViewElement(tracepointModel));
	};	
}
