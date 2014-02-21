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

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.ui.dialogs.PillsControl;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.debug.ui.launch.IUIElementFactory;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.StopModeElement;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.ui.grid.BooleanPresentationModel;
import org.eclipse.cdt.ui.grid.CheckboxViewElement;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.LinkViewElement;
import org.eclipse.cdt.ui.grid.PillSelectionViewElement;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.cdt.ui.grid.StringPresentationModel;
import org.eclipse.cdt.ui.grid.StringViewElement;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class OverviewUIElement extends AbstractUIElement {

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

	private PillsControl fTypeSelector;
	private Composite bar;

	public OverviewUIElement(OverviewElement launchElement) {
		super(launchElement, true);
	}

	@Override
	public OverviewElement getLaunchElement() {
		return (OverviewElement)super.getLaunchElement();
	}
	
	
	@Override
	public void createUIChildren(IUIElementFactory factory) {
		
		DebuggerElement debugger = getLaunchElement().findChild(DebuggerElement.class);
		
		fChildren = new ArrayList<AbstractUIElement>(getLaunchElement().getChildren().length);
		for (ILaunchElement child : getLaunchElement().getChildren()) {
			if (!child.isEnabled())
				continue;
			if (child == debugger)
				continue;
			AbstractUIElement uiChild = factory.createUIElement(child, false);
			fChildren.add(uiChild);
		}
		// TODO Auto-generated method stub
		//super.createUIChildren(factory);
	}
	
	@Override
	protected void createDetailsContent(Composite parent) {
		// TODO Auto-generated method stub
		
		//doCreateDetailsContent(parent);
		final DebuggerElement debugger = getLaunchElement().findChild(DebuggerElement.class);
		final StopModeElement stopMode = debugger.findChild(StopModeElement.class);
		
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
		
		BooleanPresentationModel stopModeModel = new BooleanPresentationModel("Non Stop") {
			
			@Override
			protected void doSetValue(boolean value) {
				stopMode.setNonStop(value);
			}
			
			@Override
			protected boolean doGetValue() {
				return stopMode.isNonStop();
			}
		};
		
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
		};
		summaryModel.addAndCallListener(new IPresentationModel.Listener() {
			@Override
			public void changed(int what, Object object) {
				if (what == IPresentationModel.ACTIVATED)
					linkActivated(debugger);
			}
		});
		final LinkViewElement summary = new LinkViewElement(summaryModel);
				
		GridElement boldFirstLabel = new GridElement() {
			@Override
			protected void populateChildren() {
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
		
		boldFirstLabel.fillIntoGrid(parent);
		
		/*
		//DebuggerUIElement debugger = findChild(DebuggerUIElement.class);
		DebuggerElement debugger = getLaunchElement().findChild(DebuggerElement.class);
		
		// At this point, we need combine ui created by these children together.
		// One option is to make createContent return GridElement.
		// Another option is to make 
	
		for (AbstractUIElement child : getFiteredChildren()) {
			if (child != debugger)
				child.createContent(parent);
		}*/
		
		createChildrenContent(parent);
		initializeDetailsContent();
		
		//super.createDetailsContent(parent);
		
		
		int num_rows = 2;
		
		//CDTUITools.getGridLayoutData(bar).verticalSpan = 1 + num_rows;
	}
	
	protected void createChildrenContent(Composite parent) {
		for (AbstractUIElement child : getFiteredChildren()) {
			child.createContent(parent);
		}
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		
		/*
		
		Label label = new Label(parent, SWT.NONE);
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		label.setText("Debug: ");
		
		bar = GridUtils.createBar(parent, 1);

		fTypeSelector = new PillsControl(parent, SWT.NONE);
		fTypeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fTypeSelector.setBackground(parent.getBackground());		
		fTypeSelector.setAlignment(SWT.LEFT);
		fTypeSelector.setItems(fgTypes);
		fTypeSelector.setSelection(0);
		fTypeSelector.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SessionType type = SessionType.values()[fTypeSelector.getSelection()];
				if (getLaunchElement().getSessionType() == type) {
					return;
				}
				sessionTypeChanged();
			}
		});
		
		new Label(parent, SWT.NONE); */
	}

	private void sessionTypeChanged() {
		save();
	}

	@Override
	public void save() {
		if (fTypeSelector != null) {
			getLaunchElement().setSessionType(SessionType.values()[fTypeSelector.getSelection()]);
		}
	}

	@Override
	protected void initializeDetailsContent() {
		if (fTypeSelector != null) {
			fTypeSelector.setSelection(getLaunchElement().getSessionType().ordinal());
		}
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fTypeSelector = null;
	}
}
