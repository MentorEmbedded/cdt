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

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement.BuildBeforeLaunch;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.launch.LaunchUtils;
import org.eclipse.cdt.ui.newui.CDTPropertyManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class BuildSettingsUIElement extends AbstractUIElement {

	private static final String LAUNCHING_PREFERENCE_PAGE_ID = "org.eclipse.debug.ui.LaunchingPreferencePage"; //$NON-NLS-1$

	// Summary widgets
	private Label fConfigLabel;
	private Label fBuildOptionLabel;

	// Detail widgets
	private Group fBuildGroup;
	private Combo fConfigCombo;
	private Button fConfigAutoButton;
	private Button fEnableBuildButton;
	private Button fDisableBuildButton;
	private Button fWorkspaceSettingsButton;
	
	public BuildSettingsUIElement(BuildSettingsElement launchElement, boolean showDetails) {
		super(launchElement, showDetails);
	}

	@Override
	public BuildSettingsElement getLaunchElement() {
		return (BuildSettingsElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		if (fBuildGroup != null) {
			fBuildGroup.dispose();
			fBuildGroup = null;
		}
		fConfigLabel = null;
		fBuildOptionLabel = null;
	}

	@Override
	protected void doCreateSummaryContent(Composite parent) {
		fConfigLabel = new Label(parent, SWT.NONE);
		fBuildOptionLabel = new Label(parent, SWT.NONE);
	}

	@Override
	protected void doCreateDetailsContent(final Composite parent) {
		fBuildGroup = new Group(parent, SWT.NONE);
		fBuildGroup.setLayout(new GridLayout(2, false));
		fBuildGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fBuildGroup.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		fBuildGroup.setText("Build (if required) before launching");
		GridUtils.fillIntoGrid(fBuildGroup, parent);

		Label comboLabel = new Label(fBuildGroup, SWT.NONE);
		comboLabel.setText("Build configuration: ");
		comboLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		comboLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		fConfigCombo = new Combo(fBuildGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		fConfigCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fConfigCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = fConfigCombo.getSelectionIndex();
				if (index >= 0) {
					getLaunchElement().setConfigId((String)fConfigCombo.getData(Integer.toString(index)));
				}
			}
		});
		
		GridUtils.createHorizontalSpacer(fBuildGroup, 1);

		fConfigAutoButton = new Button(fBuildGroup, SWT.CHECK);
		fConfigAutoButton.setText("Select configuration using 'C/C++ Application'");
		fConfigAutoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setConfigAuto(fConfigAutoButton.getSelection());
			}
		});

		GridUtils.addHorizontalSeparatorToGrid(fBuildGroup, 2);

		fEnableBuildButton = new Button(fBuildGroup, SWT.RADIO);
		fEnableBuildButton.setText("Enable auto build");
		fEnableBuildButton.setToolTipText("Always build project before launching (this may impact launch performance)");
		fEnableBuildButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setBuildBeforeLaunchOption(BuildBeforeLaunch.ENABLED);
			}
		});

		
		GridUtils.createHorizontalSpacer(fBuildGroup, 1);

		fDisableBuildButton = new Button(fBuildGroup, SWT.RADIO);
		fDisableBuildButton.setText("Disable auto build");
		fDisableBuildButton.setToolTipText("Requires manually building project before launching (this may improve launch performance)");
		fDisableBuildButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setBuildBeforeLaunchOption(BuildBeforeLaunch.DISABLED);
			}
		});

		
		GridUtils.createHorizontalSpacer(fBuildGroup, 1);

		fWorkspaceSettingsButton = new Button(fBuildGroup, SWT.RADIO);
		fWorkspaceSettingsButton.setText("Use workspace settings");
		fWorkspaceSettingsButton.setToolTipText("Use workspace settings");
		fWorkspaceSettingsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getLaunchElement().setBuildBeforeLaunchOption(BuildBeforeLaunch.USE_WORKSPACE_SETTING);
			}
		});

		
		Link link = new Link(fBuildGroup, SWT.NONE);
		link.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		link.setText(String.format("<a>%s</a>", "Configure Workspace Settings..."));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(
					parent.getShell(), 
					LAUNCHING_PREFERENCE_PAGE_ID,
					null, 
					null).open();
			}
		});
	}

	@Override
	protected void initializeSummaryContent() {
		if (fConfigLabel != null) {
			StringBuilder sb = new StringBuilder("Build configuration: ");
			if (getLaunchElement().isConfigAuto()) {
				sb.append("using active configuration from project");
			}
			else {
				String progName = getLaunchElement().getProgramName();
				String projName = getLaunchElement().getProjectName();
				if (projName != null && progName != null) {
					ICProject project = ExecutableElement.getProject(projName);
					if (project != null) {
						ICProjectDescription projDes = CDTPropertyManager.getProjectDescription(project.getProject());
						if (projDes != null) {
							ICConfigurationDescription selectedConfig = 
								projDes.getConfigurationById(getLaunchElement().getConfigId());
							sb.append(selectedConfig.getName());
						}
					}
				}
			}
			fConfigLabel.setText(sb.toString());
		}
		if (fBuildOptionLabel != null) {
			StringBuilder sb = new StringBuilder("Auto build: ");
			BuildBeforeLaunch buildOption = getLaunchElement().getBuildBeforeLaunchOption();
			if (BuildBeforeLaunch.ENABLED == buildOption) {
				sb.append("enabled");
			}
			else if (BuildBeforeLaunch.DISABLED == buildOption) {
				sb.append("disabled");
			}
			else if (BuildBeforeLaunch.USE_WORKSPACE_SETTING == buildOption) {
				sb.append("using workspace settings");
			}
			fBuildOptionLabel.setText(sb.toString());
		}
	}

	@Override
	protected void initializeDetailsContent() {
		if (fConfigAutoButton != null) {
			fConfigAutoButton.setSelection(getLaunchElement().isConfigAuto());
		}
		if (fConfigCombo != null) {
			fConfigCombo.setEnabled(!getLaunchElement().isConfigAuto());
			fConfigCombo.removeAll();
			fConfigCombo.add("Use Active");
			fConfigCombo.setData(Integer.toString(0), ""); //$NON-NLS-1$
			int selIndex = 0;
			String progName = getLaunchElement().getProgramName();
			String projName = getLaunchElement().getProjectName();
			if (projName != null && progName != null) {
				ICProject project = ExecutableElement.getProject(projName);
				if (project != null) {
					ICProjectDescription projDes = CDTPropertyManager.getProjectDescription(project.getProject());
					if (projDes != null) {
						String selectedConfigId = getLaunchElement().getConfigId();
						// Find the configuration that should be automatically selected
						String autoConfigId = null;
						if (getLaunchElement().isConfigAuto()) {
							ICConfigurationDescription autoConfig = LaunchUtils.getBuildConfigByProgramPath(project.getProject(), progName);
							if (autoConfig != null)
								autoConfigId = autoConfig.getId();
						}

						ICConfigurationDescription[] configurations = projDes.getConfigurations();
						ICConfigurationDescription selectedConfig = projDes.getConfigurationById(selectedConfigId);
						for (int i = 0; i < configurations.length; i++) {
							String configName = configurations[i].getName();
							fConfigCombo.add(configName);
							fConfigCombo.setData(Integer.toString(i + 1), configurations[i].getId());
							if (selectedConfig != null && selectedConfigId.equals(configurations[i].getId()) ||
								getLaunchElement().isConfigAuto() && configurations[i].getId().equals(autoConfigId)) {
								selIndex = i + 1;
							}
						}
					}
				}
			}
			fConfigCombo.select(selIndex);
		}
		if (fEnableBuildButton != null && fDisableBuildButton != null && fWorkspaceSettingsButton != null) {
			BuildBeforeLaunch buildOption = getLaunchElement().getBuildBeforeLaunchOption();
			fEnableBuildButton.setSelection(BuildBeforeLaunch.ENABLED == buildOption);
			fDisableBuildButton.setSelection(BuildBeforeLaunch.DISABLED == buildOption);
			fWorkspaceSettingsButton.setSelection(BuildBeforeLaunch.USE_WORKSPACE_SETTING == buildOption);
		}
	}
	
	@Override
	protected boolean hasMultipleRows() {
		return true;
	}

	protected ICProject getCProject() {
		String projName = getLaunchElement().getProjectName();
		return (projName != null) ? ExecutableElement.getProject(projName) : null;
	}
}
