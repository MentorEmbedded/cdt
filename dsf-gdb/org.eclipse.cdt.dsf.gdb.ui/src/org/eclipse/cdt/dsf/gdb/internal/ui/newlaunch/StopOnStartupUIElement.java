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

import org.eclipse.cdt.dsf.gdb.newlaunch.StopOnStartupElement;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class StopOnStartupUIElement extends GridElement {

	StopOnStartupElement launchElement;

	public StopOnStartupUIElement(StopOnStartupElement launchElement, boolean showDetails) {
		this.launchElement = launchElement;
	}

	@Override
	protected void createImmediateContent(Composite parent) {

		Label l = new Label(parent, SWT.NONE);
		l.setText("Stop on");
		
		Label spacer = new Label(parent, SWT.NONE);
			
		final Button stopButton = new Button(parent, SWT.CHECK);
		stopButton.setText("Function called");
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				launchElement.setStop(stopButton.getSelection());
			}
		});
		stopButton.setSelection(launchElement.isStop());
		
		final Text symbolText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		symbolText.setLayoutData(gd);
		symbolText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				launchElement.setStopSymbol(symbolText.getText());
			}
		});
		symbolText.setText(launchElement.getStopSymbol());
		
		new Label(parent, SWT.NONE);
	}
}
