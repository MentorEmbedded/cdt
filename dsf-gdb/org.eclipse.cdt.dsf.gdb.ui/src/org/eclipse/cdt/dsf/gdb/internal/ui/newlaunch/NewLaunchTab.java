package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.ui.CDebugUIPlugin;
import org.eclipse.cdt.debug.ui.launch.IUIElementFactory;
import org.eclipse.cdt.debug.ui.launch.RootUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.CLaunchConfigurationTab;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.ICDTLaunchHelpContextIds;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchImages;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class NewLaunchTab extends CLaunchConfigurationTab {
	
	private class NewLaunchUIRootElement extends RootUIElement {

		@Override
		protected IUIElementFactory createUIElementFactory(ILaunchConfiguration configuration) {
			return new UIElementFactory();
		}

		@Override
		public void elementAdded(ILaunchElement element, int details) {
			super.elementAdded(element, details);
			if ((details & ILaunchElement.ADD_DETAIL_ACTIVATE) != 0) {
				activateElement(element);
			}
			NewLaunchTab.this.updateLaunchConfigurationDialog();
		}

		@Override
		public void elementRemoved(ILaunchElement element) {
			super.elementRemoved(element);
			NewLaunchTab.this.updateLaunchConfigurationDialog();
		}

		@Override
		public void elementChanged(ILaunchElement element, int details) {
			super.elementChanged(element, details);
			NewLaunchTab.this.updateLaunchConfigurationDialog();
		}

		@Override
		protected ILaunchElement createTopElement(ILaunchConfiguration config) {
			return new OverviewElement();
		}
	}

	private ILaunchConfigurationWorkingCopy fLaunchConfiguration;
	private RootUIElement fRoot;

    public NewLaunchTab() {
		super();
		fRoot = new NewLaunchUIRootElement();
	}

	private static final String TAB_ID = "org.eclipse.cdt.dsf.gdb.launch.newLaunchTab"; //$NON-NLS-1$

    @Override
	public void createControl(Composite parent) {
    	Composite control = new Composite(parent, SWT.NONE);
    	setControl(control);
		
		GdbUIPlugin.getDefault().getWorkbench().getHelpSystem().setHelp(getControl(), ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);

		control.setLayout(new GridLayout());
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		fRoot.createControl(control);
	}

	@Override
	public void dispose() {
		fRoot.dispose();
		super.dispose();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		fRoot.setDefaults(config);
//		// We set empty attributes for project & program so that when one config is
//		// compared to another, the existence of empty attributes doesn't cause and
//		// incorrect result (the performApply() method can result in empty values
//		// for these attributes being set on a config if there is nothing in the
//		// corresponding text boxes)
//		// plus getContext will use this to base context from if set.
//		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
//		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_ID, ""); //$NON-NLS-1$
//		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_COREFILE_PATH, ""); //$NON-NLS-1$
//
//		// Set the auto choose build configuration to true for new configurations.
//		// Existing configurations created before this setting was introduced will have this disabled.
//		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_AUTO, true);
//
//		ICElement cElement = null;
//		cElement = getContext(config, getPlatform(config));
//		if (cElement != null) {
//			initializeCProject(cElement, config);
//			initializeProgramName(cElement, config);
//		} else {
//			// don't want to remember the interim value from before
//			config.setMappedResources(null);
//		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			
			// The given configuration is not a working copy means that a full revert of 
			// the current changes is requested. In this case we replace the old 
			// configuration with the new one and rebuild the launch element model. 
			if (!configuration.isWorkingCopy() || 
				getCurrentLaunchConfiguration() == null || 
				!((ILaunchConfigurationWorkingCopy)configuration).getOriginal().equals(getCurrentLaunchConfiguration().getOriginal())) {
				fRoot.storeState(fLaunchConfiguration);
				fLaunchConfiguration = (configuration.isWorkingCopy()) ? 
					(ILaunchConfigurationWorkingCopy)configuration : configuration.getWorkingCopy();
				fRoot.initializeFrom(fLaunchConfiguration);
			}
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
			updateLaunchConfigurationDialog();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		fRoot.performApply(configuration);
	}

	@Override
	public String getName() {
		return LaunchMessages.getString("CMainTab.Main"); //$NON-NLS-1$
	}

	@Override
	public String getId() {
		return TAB_ID;
	}

	@Override
	public Image getImage() {
		return LaunchImages.get(LaunchImages.IMG_VIEW_MAIN_TAB);
	}

	@Override
	public boolean isValid(ILaunchConfiguration configuration) {
		return fRoot.isValid(configuration);
	}

	@Override
	public String getErrorMessage() {
		return fRoot.getErrorMessage();
	}

	/**
	 * Set the program name attributes on the working copy based on the ICElement
	 */
	protected void initializeProgramName(ICElement cElement, ILaunchConfigurationWorkingCopy config) {
		boolean renamed = false;

		if (!(cElement instanceof IBinary))	{
			cElement = cElement.getCProject();
		}
		
		if (cElement instanceof ICProject) {
			IProject project = cElement.getCProject().getProject();
			String name = project.getName();
			ICProjectDescription projDes = CCorePlugin.getDefault().getProjectDescription(project);
			if (projDes != null) {
				String buildConfigName = projDes.getActiveConfiguration().getName();
				name = name + " " + buildConfigName; //$NON-NLS-1$
			}
			name = getLaunchConfigurationDialog().generateName(name);
			config.rename(name);
			renamed = true;
		}

		IBinary binary = null;
		if (cElement instanceof ICProject) {
			IBinary[] bins = getBinaryFiles((ICProject)cElement);
			if (bins != null && bins.length == 1) {
				binary = bins[0];
			}
		} else if (cElement instanceof IBinary) {
			binary = (IBinary)cElement;
		}

		if (binary != null) {
			String path;
			path = binary.getResource().getProjectRelativePath().toOSString();
			config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, path);
			if (!renamed)
			{
				String name = binary.getElementName();
				int index = name.lastIndexOf('.');
				if (index > 0) {
					name = name.substring(0, index);
				}
				name = getLaunchConfigurationDialog().generateName(name);
				config.rename(name);
				renamed = true;				
			}
		}
		
		if (!renamed) {
			String name = getLaunchConfigurationDialog().generateName(cElement.getCProject().getElementName());
			config.rename(name);
		}
	}

	/**
	 * Iterate through and suck up all of the executable files that we can find.
	 */
	protected IBinary[] getBinaryFiles(final ICProject cproject) {
		final Display display;
		if (cproject == null || !cproject.exists()) {
			return null;
		}
		if (getShell() == null) {
			display = Display.getDefault();
		} else {
			display = getShell().getDisplay();
		}
		final IBinary[][] ret = new IBinary[1][];
		BusyIndicator.showWhile(display, new Runnable() {
			@Override
			public void run() {
				try {
					ret[0] = cproject.getBinaryContainer().getBinaries();
				} catch (CModelException e) {
					CDebugUIPlugin.errorDialog("Launch UI internal error", e); //$NON-NLS-1$
				}
			}
		});

		return ret[0];
	}

	private ILaunchConfigurationWorkingCopy getCurrentLaunchConfiguration() {
		return fLaunchConfiguration;
	}
}
