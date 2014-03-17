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
import java.util.List;

import org.eclipse.cdt.debug.ui.dialogs.PillsControl;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.CoreFileElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.CoreFileElement.CoreFileType;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.PillSelectionViewElement;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CoreFileUIElement extends GridElement {

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
	
	private PillsControl fCoreTypeSelector;
	private Label fCoreLabel;
	private Text fCoreText;
	
	public CoreFileUIElement(final CoreFileElement launchElement, boolean showDetails) {
		
		SelectionPresentationModel coreType;
		coreType = new SelectionPresentationModel(LaunchMessages.getString("CMainTab.Post_mortem_file_type")) { //$NON-NLS-1$
			
			@Override
			public List<String> getPossibleValues() { return Arrays.asList(fgTypes); }
			
			@Override
			protected String doGetValue() {
				return fgTypes[launchElement.getCoreFileType().ordinal()];
			}
			
			@Override
			protected void doSetValue(String value) {
				int index = Arrays.asList(fgTypes).indexOf(value);
				CoreFileType t = CoreFileType.values()[index];
				launchElement.setType(t);
			}
		};
		
		addChild(new PillSelectionViewElement(coreType));
		
		/*
		Composite coreComp = new Composite(parent, SWT.NONE);
		GridLayout coreLayout = new GridLayout(2, false);
		coreLayout.marginHeight = 0;
		coreLayout.marginWidth = 0;
		coreComp.setLayout(coreLayout);
		coreComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridUtils.fillIntoGrid(coreComp, parent);
		
		Label comboLabel = new Label(coreComp, SWT.NONE);
		comboLabel.setText(LaunchMessages.getString("CMainTab.Post_mortem_file_type")); //$NON-NLS-1$
		comboLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		comboLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		fCoreTypeSelector = new PillsControl(coreComp, SWT.NONE);
		fCoreTypeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fCoreTypeSelector.setBackground(coreComp.getBackground());		
		fCoreTypeSelector.setAlignment(SWT.LEFT);
		fCoreTypeSelector.setItems(fgTypes);
		
		fCoreTypeSelector.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setType(CoreFileType.values()[fCoreTypeSelector.getSelection()]);
				updateCoreFileLabel();
			}
		});
		fCoreTypeSelector.setSelection(0); */

		/*
		fCoreLabel = new Label(parent, SWT.NONE);
		fCoreLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fCoreLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		GridUtils.fillIntoGrid(fCoreLabel, parent);

		fCoreText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		fCoreText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fCoreText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
				getLaunchElement().setCoreFile(fCoreText.getText().trim());;
			}
		});

		Button browseForCoreButton;
		browseForCoreButton = new Button(parent, SWT.PUSH);
		browseForCoreButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		browseForCoreButton.setToolTipText(LaunchMessages.getString("Launch.common.Browse_3")); //$NON-NLS-1$
		browseForCoreButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				String text = ""; //$NON-NLS-1$
				CoreFileType coreType = CoreFileType.values()[fCoreTypeSelector.getSelection()];
				if (coreType.equals(CoreFileType.CORE_FILE)) {
					text = handleBrowseButtonSelected(parent.getShell(), LaunchMessages.getString("CMaintab.Core_Selection")); //$NON-NLS-1$
				} 
				else if (coreType.equals(CoreFileType.TRACE_FILE)) {
					text = handleBrowseButtonSelected(parent.getShell(), LaunchMessages.getString("CMaintab.Trace_Selection")); //$NON-NLS-1$
				}
				if (text != null) {
					fCoreText.setText(text);
				}
			}
		});
		*/
		
	}
	
	@Override
	protected void createImmediateContent(Composite parent) {
	}

	/*
	@Override
	protected void initializeDetailsContent() {
		super.initializeDetailsContent();
		if (fCoreTypeSelector != null) {
			fCoreTypeSelector.setSelection(getLaunchElement().getCoreFileType().ordinal());
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
	}*/

	/*
	protected void updateCoreFileLabel() {
		CoreFileType type = CoreFileType.values()[fCoreTypeSelector.getSelection()];
		if (type.equals(CoreFileType.CORE_FILE)) {
			fCoreLabel.setText(LaunchMessages.getString("CMainTab.CoreFile_path")); //$NON-NLS-1$
		} else if (type.equals(CoreFileType.TRACE_FILE)) {
			fCoreLabel.setText(LaunchMessages.getString("CMainTab.TraceFile_path")); //$NON-NLS-1$
		} else {
			assert false : "Unknown post mortem file type"; //$NON-NLS-1$
			fCoreLabel.setText(LaunchMessages.getString("CMainTab.CoreFile_path")); //$NON-NLS-1$
		}
	}

	protected String handleBrowseButtonSelected(Shell shell, String title) {
		FileDialog fileDialog = new FileDialog(shell, SWT.NONE);
		fileDialog.setText(title);
		fileDialog.setFileName(getProgramName());
		return fileDialog.open();
	}

	
	private String getProgramName() {
		CoreExecutableElement exec = getLaunchElement().findAncestor(CoreExecutableElement.class);
		return (exec != null) ? exec.getFullProgramPath() : ""; //$NON-NLS-1$
	}*/
}
