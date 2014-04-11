package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.launch.LaunchUtils;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.cdt.ui.newui.CDTPropertyManager;

/* Model for build configuration selection. */
public class BuildConfigurationModel extends SelectionPresentationModel {
	
	private BuildSettingsElement element;
	public BuildSettingsElement getLaunchElement() {
		return element;
	}

	private int selection;
	private List<String> configurationName = new ArrayList<String>();
	
	
	public BuildConfigurationModel(String name, BuildSettingsElement element) {
		super(name);
		this.element = element;
		possibleValues = new ArrayList<String>();
		
		//fConfigCombo.setEnabled(!getLaunchElement().isConfigAuto());			
		possibleValues.add("Use Active");
		configurationName.add("");
		selection = 0;
		
		String progName = getLaunchElement().getProgramName();
		String projName = getLaunchElement().getProjectName();
		if (projName != null && !projName.isEmpty()) {
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
