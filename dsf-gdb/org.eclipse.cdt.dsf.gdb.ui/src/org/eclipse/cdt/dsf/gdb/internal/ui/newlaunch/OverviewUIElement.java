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

import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.debug.ui.dialogs.PillsControl;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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

	public OverviewUIElement(OverviewElement launchElement) {
		super(launchElement, true);
	}

	@Override
	public OverviewElement getLaunchElement() {
		return (OverviewElement)super.getLaunchElement();
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		GridLayout layout = (GridLayout)parent.getLayout();
		int horSpan = layout.numColumns;
		Composite comp = new Composite(parent, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridUtils.fillIntoGrid(comp, parent);
		
		Label label = new Label(comp, SWT.NONE);
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		label.setText("Debug: ");

		fTypeSelector = new PillsControl(comp, SWT.NONE);
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

		GridUtils.addHorizontalSeparatorToGrid(parent, horSpan);
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
