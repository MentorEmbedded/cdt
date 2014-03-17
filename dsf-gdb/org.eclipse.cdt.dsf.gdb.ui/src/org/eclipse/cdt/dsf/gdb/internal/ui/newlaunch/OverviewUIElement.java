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

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.ui.launch.IUIElementFactory;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.ui.grid.BooleanPresentationModel;
import org.eclipse.cdt.ui.grid.BooleanReflectionPresentationModel;
import org.eclipse.cdt.ui.grid.CheckboxViewElement;
import org.eclipse.cdt.ui.grid.CompositePresentationModel;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.LinkViewElement;
import org.eclipse.cdt.ui.grid.PillSelectionViewElement;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.cdt.ui.grid.StringPresentationModel;
import org.eclipse.cdt.ui.grid.StringViewElement;
import org.eclipse.cdt.ui.grid.ViewElement;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class OverviewUIElement extends ViewElement {

	private static String[] fgTypes = new String[SessionType.values().length]; 

	static {
		for (int i = 0; i < fgTypes.length; ++i) {
			if (SessionType.values()[i].equals(SessionType.LOCAL)) {
				fgTypes[i] = "Locally";
			}
			else if (SessionType.values()[i].equals(SessionType.REMOTE)) {
				fgTypes[i] = "Using gdbserver";
			}
			else if (SessionType.values()[i].equals(SessionType.CORE)) {
				fgTypes[i] = "Core file";
			}
		}
	}

	private OverviewElement launchElement;
	
	@Override
	public CompositePresentationModel getModel() {
		return (CompositePresentationModel)super.getModel();
	}
		
	public OverviewUIElement(OverviewElement launchElement, UIElementFactory factory) {
		super(new CompositePresentationModel("Overview"));
		this.launchElement = launchElement;
		
			
		final DebuggerElement debugger = getLaunchElement().findChild(DebuggerElement.class);
		final DebuggerSettingsElement debuggerSettings = debugger.findChild(DebuggerSettingsElement.class);
		
		SelectionPresentationModel types = new SelectionPresentationModel("Debug", Arrays.asList(fgTypes)) {
			
			protected String doGetValue() {
				return fgTypes[getLaunchElement().getSessionType().ordinal()];
			}
			
			protected void doSetValue(String value) {
				OverviewElement element = getLaunchElement();
				int i;
				for (i =  0; i < fgTypes.length; ++i) 
					if (fgTypes[i] == value)
						break;
				if (i < fgTypes.length) {
					SessionType type = SessionType.values()[i];
					element.setSessionType(type);	
				}
			}
		};
		final PillSelectionViewElement typeSelector = new PillSelectionViewElement(types);
		
		BooleanPresentationModel stopModeModel = 
				new BooleanReflectionPresentationModel("Non Stop", debuggerSettings, "isNonStop", "setNonStop");
		
		/*
		BooleanPresentationModel stopModeModel = new BooleanPresentationModel("Non Stop") {
			
			@Override
			protected void doSetValue(boolean value) {
				stopMode.setNonStop(value);
			}
			
			@Override
			protected boolean doGetValue() {
				return stopMode.isNonStop();
			}
		};*/
		
		final CheckboxViewElement stopModeView = new CheckboxViewElement(stopModeModel);
		stopModeView.labelInContentArea();
		
		final StringPresentationModel connection = new StringPresentationModel("Connection") {
			@Override
			protected String doGetValue() {
				return "192.168.0.150";
			}
		};
		
		final StringViewElement connectionView = new StringViewElement(connection);
		connectionView.indentLabel();
		
		StringPresentationModel summaryModel = new StringPresentationModel("") {
			@Override
			protected String doGetValue() {
				return "Advanced details";
			}
			
			@Override
			public void activate() {
				notifyListeners(IPresentationModel.ACTIVATED, debugger.getId());
			}
		};
		getModel().add(summaryModel);
		final LinkViewElement summary = new LinkViewElement(summaryModel);
				
		GridElement boldFirstLabel = new GridElement() {
			{
				addChild(typeSelector);
				addChild(connectionView);
				addChild(stopModeView);
				addChild(summary);
			}
			
			@Override
			protected void createChildrenContent(Composite parent) {
				// TODO Auto-generated method stub
				super.createChildrenContent(parent);
			}
			
			@Override
			protected void createImmediateContent(Composite parent) {	
			}
			
			@Override
			protected void adjustChildren(Composite parent) {
				Control c = getChildElements().get(0).getChildControls().get(0);
				
				assert c instanceof Label;
				((Label)c).setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
			}
		};
		
		addChild(boldFirstLabel);
		
		createUIChildren(factory);
		
	}
	
	public OverviewElement getLaunchElement() {
		return launchElement;
	}
		
	public void createUIChildren(IUIElementFactory factory) {
		
		DebuggerElement debugger = getLaunchElement().findChild(DebuggerElement.class);
		
		for (ILaunchElement child : getLaunchElement().getChildren()) {
			if (child == debugger)
				continue;
			GridElement uiChild = factory.createUIElement2(child, false);
			addChild(uiChild);
			if (uiChild instanceof ViewElement) {
				getModel().add(((ViewElement)uiChild).getModel());
			}
		}
	}

	@Override
	protected void createImmediateContent(Composite parent) {	
	}
}
