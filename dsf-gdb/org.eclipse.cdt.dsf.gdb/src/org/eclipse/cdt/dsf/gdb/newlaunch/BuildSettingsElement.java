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

package org.eclipse.cdt.dsf.gdb.newlaunch;

import java.util.Map;

import org.eclipse.cdt.debug.core.launch.AbstractLaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement.ProjectChangeEvent;

/**
 * @since 4.6
 */
public class BuildSettingsElement extends AbstractLaunchElement {

	final private static String ELEMENT_ID = ".buildSettings"; //$NON-NLS-1$
	final private static String ATTR_CONFIG_ID = ".configId"; //$NON-NLS-1$
	final private static String ATTR_CONFIG_AUTO = ".configAuto"; //$NON-NLS-1$
	final private static String ATTR_BUILD_BEFORE_LAUNCH = ".buildBeforeLaunch"; //$NON-NLS-1$

	public enum BuildBeforeLaunch {
		ENABLED,
		DISABLED,
		USE_WORKSPACE_SETTING,
	};

	private String fConfigId = getDefaultConfigId();
	private boolean fConfigAuto = getDefaultConfigAutoValue();
	private BuildBeforeLaunch fBuildOption = getDefaultBuildBeforeLaunchValue();
	
	public BuildSettingsElement(ILaunchElement parent) {
		super(parent, parent.getId() + ELEMENT_ID, "Build Settings", "BuildSettings");
	}

	@Override
	protected void doCreateChildren(Map<String, Object> attributes) {
	}

	@Override
	protected void doInitializeFrom(Map<String, Object> attributes) {
		fConfigId = getAttribute(attributes, getId() + ATTR_CONFIG_ID, getDefaultConfigId());
		fConfigAuto = getAttribute(attributes, getId() + ATTR_CONFIG_AUTO, getDefaultConfigAutoValue());
		int buildOption = getAttribute(
				attributes, getId() + ATTR_BUILD_BEFORE_LAUNCH, getDefaultBuildBeforeLaunchValue().ordinal());
		if (buildOption < 0 || buildOption >= BuildBeforeLaunch.values().length) {
			setErrorMessage("Invalid build before launch option");
		}
		else {
			fBuildOption = BuildBeforeLaunch.values()[buildOption];
		}
	}

	@Override
	protected void doPerformApply(Map<String, Object> attributes) {
		attributes.put(getId() + ATTR_CONFIG_ID, fConfigId);
		attributes.put(getId() + ATTR_CONFIG_AUTO, fConfigAuto);
		attributes.put(getId() + ATTR_BUILD_BEFORE_LAUNCH, fBuildOption.ordinal());
	}

	@Override
	protected void doSetDefaults(Map<String, Object> attributes) {
		fConfigId = getDefaultConfigId();
		fConfigAuto = getDefaultConfigAutoValue();
		fBuildOption = getDefaultBuildBeforeLaunchValue();
		attributes.put(getId() + ATTR_CONFIG_ID, fConfigId);
		attributes.put(getId() + ATTR_CONFIG_AUTO, fConfigAuto);
		attributes.put(getId() + ATTR_BUILD_BEFORE_LAUNCH, fBuildOption.ordinal());
	}

	@Override
	protected boolean isContentValid() {
		return true;
	}
	
	public static String getDefaultConfigId() {
		return ""; //$NON-NLS-1$
	}
	
	public static boolean getDefaultConfigAutoValue() {
		return false;
	}
	
	public static BuildBeforeLaunch getDefaultBuildBeforeLaunchValue() {
		return BuildBeforeLaunch.USE_WORKSPACE_SETTING;
	}

	public String getConfigId() {
		return fConfigId;
	}

	public void setConfigId(String configId) {
		if (fConfigId.equals(configId)) {
			return;
		}
		fConfigId = configId;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public boolean isConfigAuto() {
		return fConfigAuto;
	}

	public void setConfigAuto(boolean configAuto) {
		if (fConfigAuto == configAuto) {
			return;
		}
		fConfigAuto = configAuto;
		elementChanged(CHANGE_DETAIL_STATE | CHANGE_DETAIL_CONTENT);
	}

	public BuildBeforeLaunch getBuildBeforeLaunchOption() {
		return fBuildOption;
	}

	public void setBuildBeforeLaunchOption(BuildBeforeLaunch buildOption) {
		if (fBuildOption == buildOption) {
			return;
		}
		fBuildOption = buildOption;
		elementChanged(CHANGE_DETAIL_STATE);
	}

	public String getProgramName() {
		ExecutableElement exec = findAncestor(ExecutableElement.class);
		return (exec != null) ? exec.getProgramName() : null;
	}

	public String getProjectName() {
		ExecutableElement exec = findAncestor(ExecutableElement.class);
		return (exec != null) ? exec.getProjectName() : null;
	}

	@Override
	public void update(IChangeEvent event) {
		if (event instanceof ProjectChangeEvent) {
			handleProjectChange((ProjectChangeEvent)event);
		}
		super.update(event);
	}

	private void handleProjectChange(ProjectChangeEvent event) {
		elementChanged(CHANGE_DETAIL_STATE);
	}
}
