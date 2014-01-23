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
import org.eclipse.cdt.dsf.gdb.newlaunch.RemoteBinaryElement;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RemoteBinaryUIElement extends AbstractUIElement {

	private Text fRemotePath;

	public RemoteBinaryUIElement(RemoteBinaryElement launchElement, boolean showDetails) {
		super(launchElement, true);
	}

	@Override
	public RemoteBinaryElement getLaunchElement() {
		return (RemoteBinaryElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Binary on target: ");
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		GridUtils.fillIntoGrid(label, parent);
		
		fRemotePath = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fRemotePath.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fRemotePath.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getLaunchElement().setRemotePath(fRemotePath.getText().trim());
			}
		});
	}

	@Override
	protected void initializeDetailsContent() {
		if (fRemotePath != null) {
			fRemotePath.setText(getLaunchElement().getRemotePath());
		}
	}
}
