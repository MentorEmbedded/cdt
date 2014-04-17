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
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutablesListElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.ui.grid.CompositePresentationModel;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.ListPresentationModel;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.cdt.ui.grid.StaticStringPresentationModel;
import org.eclipse.cdt.ui.grid.ViewElement;
import org.eclipse.cdt.ui.grid.ViewElementFactory;
import org.eclipse.swt.widgets.Composite;

public class OverviewUIElement extends ViewElement {

	private static String[] fgTypes = new String[SessionType.values().length];

	static {
		for (int i = 0; i < fgTypes.length; ++i) {
			if (SessionType.values()[i].equals(SessionType.LOCAL)) {
				fgTypes[i] = "Locally";
			} else if (SessionType.values()[i].equals(SessionType.REMOTE)) {
				fgTypes[i] = "Using gdbserver";
			} else if (SessionType.values()[i].equals(SessionType.CORE)) {
				fgTypes[i] = "Core file";
			}
		}
	}

	private OverviewElement launchElement;
	private ViewElementFactory viewElementFactory;

	@Override
	public CompositePresentationModel getModel() {
		return (CompositePresentationModel) super.getModel();
	}

	public OverviewUIElement(OverviewElement launchElement,
			ViewElementFactory viewElementFactory, UIElementFactory factory) {
		super(new CompositePresentationModel("Overview"));
		getModel().setId("overview"); //$NON-NLS-1$
		getModel().setClasses(new String[]{"top"});

		this.launchElement = launchElement;
		this.viewElementFactory = viewElementFactory;

		final DebuggerElement debugger = getLaunchElement().findChild(
				DebuggerElement.class);
		final DebuggerSettingsElement debuggerSettings = debugger
				.findChild(DebuggerSettingsElement.class);

		final IPresentationModel debuggerPresentation = factory
				.createPresentationModel(debugger);

		final IPresentationModel connection = debuggerPresentation
				.findChild("Connection");

		final SelectionPresentationModel types = new SelectionPresentationModel(
				"Debug", Arrays.asList(fgTypes)) {

			protected String doGetValue() {
				return fgTypes[getLaunchElement().getSessionType().ordinal()];
			}

			protected void doSetValue(String value) {
				OverviewElement element = getLaunchElement();
				int i;
				for (i = 0; i < fgTypes.length; ++i)
					if (fgTypes[i] == value)
						break;
				if (i < fgTypes.length) {
					SessionType type = SessionType.values()[i];
					element.setSessionType(type);
				}
			}
		};
		// final PillSelectionViewElement typeSelector = new
		// PillSelectionViewElement(types);

		final IPresentationModel stopModeModel = debuggerPresentation
				.findChild("Non Stop");

		// final CheckboxViewElement stopModeView = new
		// CheckboxViewElement(stopModeModel);
		// stopModeView.labelInContentArea();
		final GridElement stopModeView = viewElementFactory
				.createViewElement(stopModeModel);

		/*
		 * final StringPresentationModel connection = new
		 * StringPresentationModel("Connection") { {
		 * types.addAndCallListener(new IPresentationModel.Listener() {
		 * 
		 * @Override public void changed(int what, Object object) { if ((what &
		 * IPresentationModel.VALUE_CHANGED) != 0) {
		 * setVisible(types.getValue().equals("Using gdbserver")); } } }); }
		 * 
		 * @Override protected String doGetValue() { return "192.168.0.150"; }
		 * };
		 * 
		 * final StringViewElement connectionView = new
		 * StringViewElement(connection); connectionView.indentLabel();
		 */

		final GridElement connectionView = viewElementFactory
				.createViewElement(connection);

		StaticStringPresentationModel summaryModel = new StaticStringPresentationModel() {
			@Override
			public String getString() {
				return "Advanced details";
			}

			@Override
			public void activate() {
				notifyListeners(IPresentationModel.ACTIVATED,
						debuggerPresentation);
			}
		};
		// getModel().add(summaryModel);
		// final LinkViewElement summary = new LinkViewElement(summaryModel);

		final CompositePresentationModel options = new CompositePresentationModel(
				"Options");
		options.add(stopModeModel);
		options.add(summaryModel);

		final GridElement optionsView = viewElementFactory
				.createViewElement(options);

		ExecutablesListElement executableListElement = launchElement
				.findChild(ExecutablesListElement.class);

		final CompositePresentationModel executables = new ListPresentationModel(
				"Executables");
		executables.setId("executables");

		for (ILaunchElement e : executableListElement.getChildren()) {
			executables.add(factory.createPresentationModel(e, true));
		}

		getModel().add(types);
		getModel().add(connection);
		getModel().add(options);
		getModel().add(executables);

		/*
		 * GridElement boldFirstLabel = new GridElement() { {
		 * addChild(typeSelector); addChild(connectionView);
		 * addChild(optionsView); //addChild(stopModeView); //addChild(summary);
		 * }
		 * 
		 * @Override protected void createChildrenContent(Composite parent) { //
		 * TODO Auto-generated method stub super.createChildrenContent(parent);
		 * }
		 * 
		 * @Override protected void createImmediateContent(Composite parent) { }
		 * 
		 * @Override protected void adjustChildren(Composite parent) { Control c
		 * = getChildElements().get(0).getChildControls().get(0);
		 * 
		 * assert c instanceof Label;
		 * ((Label)c).setFont(JFaceResources.getFontRegistry
		 * ().getBold(JFaceResources.DIALOG_FONT)); } };
		 */

		// addChild(boldFirstLabel);
		addChild(viewElementFactory.createViewElement(getModel()));

		createUIChildren(factory);

	}

	public OverviewElement getLaunchElement() {
		return launchElement;
	}

	public void createUIChildren(IUIElementFactory factory) {

		DebuggerElement debugger = getLaunchElement().findChild(
				DebuggerElement.class);

		for (ILaunchElement child : getLaunchElement().getChildren()) {
			if (child == debugger)
				continue;
			//GridElement uiChild = factory.createUIElement2(child,
			// viewElementFactory, false);
			//addChild(uiChild);
			//if (uiChild instanceof ViewElement) {
			// getModel().add(((ViewElement)uiChild).getModel());
			//}
		}
	}

	@Override
	protected void createImmediateContent(Composite parent) {
	}
}
