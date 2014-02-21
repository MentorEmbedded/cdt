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

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.ui.launch.ListUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.IGdbUIConstants;
import org.eclipse.cdt.dsf.gdb.newlaunch.AttachToProcessElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutablesListElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class ExecutablesListUIElement extends ListUIElement {

	public ExecutablesListUIElement(ExecutablesListElement launchElement) {
		super(launchElement);
	}

	@Override
	public ExecutablesListElement getLaunchElement() {
		return (ExecutablesListElement)super.getLaunchElement();
	}

	@Override
	protected String getLinkLabel(ILaunchElement element) {
		String programName = null;
		if (element instanceof ExecutableElement) {
			programName = ((ExecutableElement)element).getProgramName();
			if (programName.isEmpty()) {
				programName = "Not specified";
			}
		}
		if (element instanceof AttachToProcessElement) {
			programName += " (attach)";
		}
		if (programName != null) {
			return programName;
		}
		return super.getLinkLabel(element);
	}

	@Override
	protected void createButtonBar(Composite parent) {
		
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		
		int horSpan = 1;
		Layout parentLayout = parent.getLayout();
		if (parentLayout instanceof GridLayout) {
			horSpan = ((GridLayout)parentLayout).numColumns;
		}
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		
		layout.marginWidth = layout.marginHeight = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		
		
		
		Button button = createButton(comp, GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_NEW_EXECUTABLE), "Add executable", 1, 1);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newExecutableButtonPressed();
			}
		});
		
		button = createButton(comp, GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_NEW_ATTACH), "Attach to process", 1, 1);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				attachButtonPressed();
			}
		});
	}

	protected void newExecutableButtonPressed() {
		getLaunchElement().addExecutable();;
	}

	protected void attachButtonPressed() {
		getLaunchElement().addAttachToProcess();
	}
}
