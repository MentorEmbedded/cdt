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
import java.util.List;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement.BuildBeforeLaunch;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.launch.LaunchUtils;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.LinkViewElement;
import org.eclipse.cdt.ui.grid.PillSelectionViewElement;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.cdt.ui.grid.StringPresentationModel;
import org.eclipse.cdt.ui.newui.CDTPropertyManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class BuildSettingsUIElement extends GridElement {

	/* Model for build configuration selection. */
	private final class ConfigurationModel extends SelectionPresentationModel {
		
		private int selection;
		private List<String> configurationName = new ArrayList<String>();
		
		public ConfigurationModel(String name) {
			super(name);
			possibleValues = new ArrayList<String>();
			
			//fConfigCombo.setEnabled(!getLaunchElement().isConfigAuto());			
			possibleValues.add("Use Active");
			configurationName.add("");
			selection = 0;
			
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
							possibleValues.add(configName);
							configurationName.add(configurations[i].getId());
							//fConfigCombo.setData(Integer.toString(i + 1), configurations[i].getId());
							if (selectedConfig != null && selectedConfigId.equals(configurations[i].getId()) ||
								getLaunchElement().isConfigAuto() && configurations[i].getId().equals(autoConfigId)) {
								selection = i + 1;
							}
						}
					}
				}
			}						
		}	
		
		@Override
		protected String doGetValue() {
			return possibleValues.get(selection);
		};
		
		@Override
		protected void doSetValue(String value) {
			int index = possibleValues.indexOf(value);
			getLaunchElement().setConfigId(configurationName.get(index));
		}
		
	}

	private static final String LAUNCHING_PREFERENCE_PAGE_ID = "org.eclipse.debug.ui.LaunchingPreferencePage"; //$NON-NLS-1$

	// Summary widgets
	private Label fConfigLabel;
	private Label fBuildOptionLabel;

	// Detail widgets
	private Group fBuildGroup;
	
	//private Combo fConfigCombo;
	
	
	private Button fConfigAutoButton;
	private Button fEnableBuildButton;
	private Button fDisableBuildButton;
	private Button fWorkspaceSettingsButton;
	
	private BuildSettingsElement launchElement;
	
	public BuildSettingsUIElement(BuildSettingsElement launchElement, boolean showDetails) {
		this.launchElement = launchElement;
		
		if (showDetails) {
			doCreateDetailsContent();
		} else {
			doCreateSummaryContent();
		}		
	}

	public BuildSettingsElement getLaunchElement() {
		return launchElement;
	}

	protected void doCreateSummaryContent() {
		
		final StringPresentationModel m = new StringPresentationModel(getLaunchElement().getName()) {
			@Override
			protected String doGetValue() {
				StringBuilder sb = new StringBuilder("Build configuration: ");
				if (getLaunchElement().isConfigAuto()) {
					sb.append("using active configuration from project");
				}
				else {
					String progName = getLaunchElement().getProgramName();
					String projName = getLaunchElement().getProjectName();
					if (projName != null && !projName.isEmpty() && progName != null && !progName.isEmpty()) {
						ICProject project = ExecutableElement.getProject(projName);
						if (project != null) {
							ICProjectDescription projDes = CDTPropertyManager.getProjectDescription(project.getProject());
							if (projDes != null) {
								ICConfigurationDescription selectedConfig = 
									projDes.getConfigurationById(getLaunchElement().getConfigId());
								sb.append((selectedConfig != null) ? selectedConfig.getName() : "Use Active");
							}
						}
					}
				}
				return sb.toString();
			}
		};
		
		addChild(new LinkViewElement(m));
		
		final StringPresentationModel m2 = new StringPresentationModel(getLaunchElement().getName()) {
			@Override
			protected String doGetValue() {
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
				return sb.toString();
			}
		};
		addChild(new LinkViewElement(m2));
	}

	protected void doCreateDetailsContent() {
		
		final SelectionPresentationModel buildModel = new SelectionPresentationModel("Build on launch") {
			
			@Override
			public List<String> getPossibleValues() {
				return Arrays.asList("On", "Off", "Workspace");
			}
			
			@Override
			protected String doGetValue() {
				
				BuildBeforeLaunch v = getLaunchElement().getBuildBeforeLaunchOption();
				if (v == BuildBeforeLaunch.ENABLED)
					return "On";
				else if (v == BuildBeforeLaunch.DISABLED)
					return "Off";
				else
					return "Workspace";							
			}
			
			@Override
			protected void doSetValue(String value) {				
				if (value.equals("On"))
					getLaunchElement().setBuildBeforeLaunchOption(BuildBeforeLaunch.ENABLED);
				else if (value.equals("Off"))
					getLaunchElement().setBuildBeforeLaunchOption(BuildBeforeLaunch.DISABLED);
				else
					getLaunchElement().setBuildBeforeLaunchOption(BuildBeforeLaunch.USE_WORKSPACE_SETTING);			
			}					
		};
		
		addChild(new PillSelectionViewElement(buildModel));
		
		final SelectionPresentationModel configuration = new ConfigurationModel("Configuration");
		addChild(new PillSelectionViewElement(configuration));
				
		// FIXME: Add a link to configure workspace settings
		// FIXME: add checkbox to use executable.
		

		/*
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
		}); */
	}

	/*
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
				if (projName != null && !projName.isEmpty() && progName != null && !progName.isEmpty()) {
					ICProject project = ExecutableElement.getProject(projName);
					if (project != null) {
						ICProjectDescription projDes = CDTPropertyManager.getProjectDescription(project.getProject());
						if (projDes != null) {
							ICConfigurationDescription selectedConfig = 
								projDes.getConfigurationById(getLaunchElement().getConfigId());
							sb.append((selectedConfig != null) ? selectedConfig.getName() : "Use Active");
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
	}
	
	@Override
	protected boolean hasMultipleRows() {
		return true;
	}*/

	protected ICProject getCProject() {
		String projName = getLaunchElement().getProjectName();
		return (projName != null) ? ExecutableElement.getProject(projName) : null;
	}

	@Override
	protected void createImmediateContent(Composite parent) {
		// TODO Auto-generated method stub
		
	}
}
