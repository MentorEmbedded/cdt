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
import org.eclipse.cdt.dsf.gdb.newlaunch.StopOnStartupElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class StopOnStartupUIElement extends AbstractUIElement {

	private Button fStopButton;
	private Text fSymbolText;

	public StopOnStartupUIElement(StopOnStartupElement launchElement, boolean showDetails) {
		super(launchElement, true);
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fStopButton = null;
		fSymbolText = null;
	}

	@Override
	public StopOnStartupElement getLaunchElement() {
		return (StopOnStartupElement)super.getLaunchElement();
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridUtils.fillIntoGrid(comp, parent);

		fStopButton = new Button(comp, SWT.CHECK);
		fStopButton.setText("Stop on startup at ");
		fStopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopButtonPressed();
			}
		});
		
		fSymbolText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		fSymbolText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				symbolChanged();
			}
		});
		
		GridUtils.createHorizontalSpacer(parent, 2);
	}

	@Override
	protected void initializeDetailsContent() {
		fStopButton.setSelection(getLaunchElement().isStop());
		fSymbolText.setEnabled(getLaunchElement().isStop());
		fSymbolText.setText(getLaunchElement().getStopSymbol());
	}

	@Override
	public void save() {
		if (fStopButton != null) {
			getLaunchElement().setStop(fStopButton.getSelection());
		}
		if (fSymbolText != null) {
			getLaunchElement().setStopSymbol(fSymbolText.getText().trim());
		}
	}

	private void stopButtonPressed() {
		save();
	}
	
	private void symbolChanged() {
		save();
	}
}
