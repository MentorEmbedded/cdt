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

import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutablesListElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.ui.grid.CompositePresentationModel;
import org.eclipse.cdt.ui.grid.IPresentationModel;
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
			ViewElementFactory viewElementFactory, UIElementFactory factory, Composite parent) {
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
		//final GridElement stopModeView = viewElementFactory
		//		.createViewElement(stopModeModel);

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

		final CompositePresentationModel options = new CompositePresentationModel(
				"Options");
		options.add(stopModeModel);
		options.add(summaryModel);


		ExecutablesListElement executableListElement = launchElement
				.findChild(ExecutablesListElement.class);

		final CompositePresentationModel executables = new ExecutableListPresentationModel(executableListElement, factory);

		

		getModel().add(types);
		getModel().add(connection);
		getModel().add(options);
		getModel().add(executables);

		addChild(viewElementFactory.create(getModel(), parent));
	}

	public OverviewElement getLaunchElement() {
		return launchElement;
	}

	@Override
	protected void createImmediateContent(Composite parent) {
	}
}
