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
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class OverviewUIElement extends AbstractUIElement {

	private static String[] fgTypes = new String[SessionType.values().length]; 

	static {
		for (int i = 0; i < fgTypes.length; ++i) {
			if (SessionType.values()[i].equals(SessionType.LOCAL)) {
				fgTypes[i] = "locally";
			}
			else if (SessionType.values()[i].equals(SessionType.REMOTE)) {
				fgTypes[i] = "using gdbserver";
			}
			if (SessionType.values()[i].equals(SessionType.CORE)) {
				fgTypes[i] = "core file";
			}
		}
	}

	private Combo fTypeCombo;
	private Button fAttachButton;

	public OverviewUIElement(OverviewElement launchElement) {
		super(launchElement, true);
	}

	@Override
	public OverviewElement getLaunchElement() {
		return (OverviewElement)super.getLaunchElement();
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		Composite base = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 0;
		base.setLayout(layout);
		base.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Composite comboComp = new Composite(base, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 0;
		comboComp.setLayout(layout);
		comboComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label label = new Label(comboComp, SWT.NONE);
		label.setText("Debug ");
		
		fTypeCombo = new Combo(comboComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		fTypeCombo.setItems(fgTypes);
		fTypeCombo.select(0);
		
		fTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SessionType type = SessionType.values()[fTypeCombo.getSelectionIndex()];
				if (getLaunchElement().getSessionType() == type) {
					return;
				}
				fAttachButton.setVisible(type != SessionType.CORE);
				sessionTypeChanged();
			}
		});

		fAttachButton = new Button(base, SWT.CHECK);
		fAttachButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		fAttachButton.setText("Attach to process");
		fAttachButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				attachChanged();
			}
		});

		GridUtils.addHorizontalSeparatorToGrid(parent, 4);
	}

	private void sessionTypeChanged() {
		save();
	}

	private void attachChanged() {
		save();		
	}

	@Override
	public void save() {
		if (fTypeCombo != null) {
			getLaunchElement().setSessionType(SessionType.values()[fTypeCombo.getSelectionIndex()]);
		}
		if (fAttachButton != null) {
			getLaunchElement().setAttach(fAttachButton.getSelection());
		}
	}

	@Override
	protected void initializeDetailsContent() {
		if (fTypeCombo != null) {
			fTypeCombo.select(getLaunchElement().getSessionType().ordinal());
		}
		if (fAttachButton != null) {
			fAttachButton.setSelection(getLaunchElement().isAttach());
		}
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fTypeCombo = null;
		fAttachButton = null;
	}
}
