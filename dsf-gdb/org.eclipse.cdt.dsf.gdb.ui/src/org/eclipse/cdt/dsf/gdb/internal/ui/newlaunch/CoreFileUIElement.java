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

import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.IGdbUIConstants;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.CoreFileElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.CoreFileElement.CoreFileType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CoreFileUIElement extends AbstractUIElement {

	private static String[] fgTypes = new String[CoreFileType.values().length]; 

	static {
		for (int i = 0; i < fgTypes.length; ++i) {
			if (CoreFileType.values()[i].equals(CoreFileType.CORE_FILE)) {
				fgTypes[i] = "Core file";
			}
			else if (CoreFileType.values()[i].equals(CoreFileType.TRACE_FILE)) {
				fgTypes[i] = "Trace file";
			}
		}
	}
	
	private Combo fCoreTypeCombo;
	private Label fCoreLabel;
	private Text fCoreText;
	
	public CoreFileUIElement(CoreFileElement launchElement, boolean showDetails) {
		super(launchElement, true);
	}

	@Override
	public CoreFileElement getLaunchElement() {
		return (CoreFileElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fCoreTypeCombo = null;
		fCoreLabel = null;
		fCoreText = null;
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		Composite coreComp = new Composite(parent, SWT.NONE);
		GridLayout coreLayout = new GridLayout(3, false);
		coreLayout.marginHeight = 0;
		coreLayout.marginWidth = 0;
		coreComp.setLayout(coreLayout);
		coreComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label comboLabel = new Label(coreComp, SWT.NONE);
		comboLabel.setText(LaunchMessages.getString("CMainTab.Post_mortem_file_type")); //$NON-NLS-1$
		comboLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		fCoreTypeCombo = new Combo(coreComp, SWT.READ_ONLY | SWT.DROP_DOWN);
		fCoreTypeCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		fCoreTypeCombo.setItems(fgTypes);

		fCoreLabel = new Label(coreComp, SWT.NONE);
		fCoreLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		fCoreText = new Text(coreComp, SWT.SINGLE | SWT.BORDER);
		fCoreText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		fCoreText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
				getLaunchElement().setCoreFile(fCoreText.getText().trim());;
			}
		});

		Button browseForCoreButton;
		browseForCoreButton = new Button(coreComp, SWT.PUSH);
		browseForCoreButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		browseForCoreButton.setToolTipText(LaunchMessages.getString("Launch.common.Browse_3")); //$NON-NLS-1$
//		browseForCoreButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent evt) {
//				String text;
//				String coreType = getSelectedCoreType();
//				if (coreType.equals(IGDBLaunchConfigurationConstants.DEBUGGER_POST_MORTEM_CORE_FILE)) {
//					text = handleBrowseButtonSelected(LaunchMessages.getString("CMaintab.Core_Selection")); //$NON-NLS-1$
//				} else if (coreType.equals(IGDBLaunchConfigurationConstants.DEBUGGER_POST_MORTEM_TRACE_FILE)) {
//					text = handleBrowseButtonSelected(LaunchMessages.getString("CMaintab.Trace_Selection")); //$NON-NLS-1$
//				} else {
//					assert false : "Unknown core file type"; //$NON-NLS-1$
//					text = handleBrowseButtonSelected(LaunchMessages.getString("CMaintab.Core_Selection")); //$NON-NLS-1$
//				}
//
//				if (text != null) {
//					fCoreText.setText(text);
//				}
//				updateLaunchConfigurationDialog();
//			}
//		});
		
		fCoreTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setType(CoreFileType.values()[fCoreTypeCombo.getSelectionIndex()]);
				updateCoreFileLabel();
			}
		});
		fCoreTypeCombo.select(0);
	}

	@Override
	protected void initializeDetailsContent() {
		super.initializeDetailsContent();
		if (fCoreTypeCombo != null) {
			fCoreTypeCombo.select(getLaunchElement().getCoreFileType().ordinal());
		}
		if (fCoreText != null) {
			fCoreText.setText(getLaunchElement().getCoreFile());
		}
		updateCoreFileLabel();
	}

	@Override
	protected String getLabel() {
		if (CoreFileType.CORE_FILE.equals(getLaunchElement().getCoreFileType())) {
			return "Core File";
		}
		else if (CoreFileType.TRACE_FILE.equals(getLaunchElement().getCoreFileType())) {
			return "Trace File";
		}
		return super.getLabel();
	}

	protected void updateCoreFileLabel() {
		CoreFileType type = CoreFileType.values()[fCoreTypeCombo.getSelectionIndex()];
		if (type.equals(CoreFileType.CORE_FILE)) {
			fCoreLabel.setText(LaunchMessages.getString("CMainTab.CoreFile_path")); //$NON-NLS-1$
		} else if (type.equals(CoreFileType.TRACE_FILE)) {
			fCoreLabel.setText(LaunchMessages.getString("CMainTab.TraceFile_path")); //$NON-NLS-1$
		} else {
			assert false : "Unknown post mortem file type"; //$NON-NLS-1$
			fCoreLabel.setText(LaunchMessages.getString("CMainTab.CoreFile_path")); //$NON-NLS-1$
		}
	}
}
