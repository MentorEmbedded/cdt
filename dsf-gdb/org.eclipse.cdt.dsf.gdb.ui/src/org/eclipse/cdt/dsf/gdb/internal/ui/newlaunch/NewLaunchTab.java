package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.ui.CDebugUIPlugin;
import org.eclipse.cdt.debug.ui.dialogs.IAttributeStore;
import org.eclipse.cdt.debug.ui.dialogs.RootUIElement;
import org.eclipse.cdt.debug.ui.dialogs.UIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.CLaunchConfigurationTab;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.ICDTLaunchHelpContextIds;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchImages;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
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

	/**
	 * Wrapper around {@link ILaunchConfigurationWorkingCopy} to use it 
	 * with {@link UIElement}. 
	 */
	protected class AttributeStoreAdapter implements IAttributeStore {

		private ILaunchConfigurationWorkingCopy fConfiguration;

		private AttributeStoreAdapter(ILaunchConfigurationWorkingCopy config) {
			fConfiguration = config;
		}

		@Override
		public String getId() {
			try {
				return String.format("%s.%s", fConfiguration.getType().getIdentifier(), fConfiguration.getName()); //$NON-NLS-1$
			}
			catch(CoreException e) {
				// ????
			}
			return fConfiguration.getName();
		}

		@Override
		public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException {
			return fConfiguration.getAttribute(attributeName, defaultValue);
		}

		@Override
		public int getAttribute(String attributeName, int defaultValue) throws CoreException {
			return fConfiguration.getAttribute(attributeName, defaultValue);
		}

		@Override
		public List<?> getAttribute(String attributeName, List<?> defaultValue) throws CoreException {
			return fConfiguration.getAttribute(attributeName, defaultValue);
		}

		@Override
		public Set<?> getAttribute(String attributeName, Set<?> defaultValue) throws CoreException {
			return fConfiguration.getAttribute(attributeName, defaultValue);
		}

		@Override
		public Map<?, ?> getAttribute(String attributeName, Map<?, ?> defaultValue) throws CoreException {
			return fConfiguration.getAttribute(attributeName, defaultValue);
		}

		@Override
		public String getAttribute(String attributeName, String defaultValue) throws CoreException {
			return fConfiguration.getAttribute(attributeName, defaultValue);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, ?> getAttributes() throws CoreException {
			return fConfiguration.getAttributes();
		}

		@Override
		public boolean isDirty() {
			return fConfiguration.isDirty();
		}

		@Override
		public void setAttribute(String attributeName, int value) {
			fConfiguration.setAttribute(attributeName, value);
		}

		@Override
		public void setAttribute(String attributeName, String value) {
			fConfiguration.setAttribute(attributeName, value);
		}

		@Override
		public void setAttribute(String attributeName, List<?> value) {
			fConfiguration.setAttribute(attributeName, value);
		}

		@Override
		public void setAttribute(String attributeName, Map<?, ?> value) {
			fConfiguration.setAttribute(attributeName, value);
		}

		@Override
		public void setAttribute(String attributeName, Set<?> value) {
			fConfiguration.setAttribute(attributeName, value);
		}

		@Override
		public void setAttribute(String attributeName, boolean value) {
			fConfiguration.setAttribute(attributeName, value);
		}

		@Override
		public void setAttributes(Map<String, ?> attributes) {
			fConfiguration.setAttributes(attributes);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AttributeStoreAdapter)) {
				return false;
			}
			return fConfiguration.getOriginal().equals(((AttributeStoreAdapter)obj).fConfiguration.getOriginal());
		}
	}

	class OverviewUIElement extends UIElement {

		final private static String ELEMENT_ID = ".overview"; //$NON-NLS-1$

		OverviewUIElement() {
			super(GdbUIPlugin.PLUGIN_ID + ELEMENT_ID, null, "Overview", ""); //$NON-NLS-2$
			setShowDetails(true);
		}

		@Override
		protected void doCreateChildren(IAttributeStore store) {
			try {
				String programName = store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, ""); //$NON-NLS-1$
				if (!programName.isEmpty()) {
					addChildren(new UIElement[] {
						new ExecutableUIElement(this),
					});
				}
			}
			catch(CoreException e) {
				// TODO: report error
			}
			addChildren(new UIElement[] {
				new NewExecutableLinkElement(this),
				new DebuggerUIElement(this),
			});
		}
	}

	class NewLaunchRootElement extends RootUIElement {

		@Override
		public void errorReported(String errorMessage) {
			super.errorReported(errorMessage);
			NewLaunchTab.this.setErrorMessage(errorMessage);
		}

		@Override
		public void elementAdded(UIElement element) {
			super.elementAdded(element);
			NewLaunchTab.this.updateLaunchConfigurationDialog();
		}

		@Override
		public void elementRemoved(UIElement element) {
			super.elementRemoved(element);
			NewLaunchTab.this.updateLaunchConfigurationDialog();
		}

		@Override
		public void elementChanged(UIElement element) {
			super.elementChanged(element);
			NewLaunchTab.this.updateLaunchConfigurationDialog();
		}
	}

	private IAttributeStore fAttributeStore;
	private RootUIElement fRoot;

    public NewLaunchTab() {
		super();
		fRoot = new NewLaunchRootElement();
		fRoot.setTopElement(new OverviewUIElement());
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
		// We set empty attributes for project & program so that when one config is
		// compared to another, the existence of empty attributes doesn't cause and
		// incorrect result (the performApply() method can result in empty values
		// for these attributes being set on a config if there is nothing in the
		// corresponding text boxes)
		// plus getContext will use this to base context from if set.
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_ID, ""); //$NON-NLS-1$
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_COREFILE_PATH, ""); //$NON-NLS-1$

		// Set the auto choose build configuration to true for new configurations.
		// Existing configurations created before this setting was introduced will have this disabled.
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_AUTO, true);

		ICElement cElement = null;
		cElement = getContext(config, getPlatform(config));
		if (cElement != null) {
			initializeCProject(cElement, config);
			initializeProgramName(cElement, config);
		} else {
			// don't want to remember the interim value from before
			config.setMappedResources(null);
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			// The configuration is not a working copy means that a full revert of 
			// the current changes is requested. In this case we replace the old 
			// attribute store with the new one. 
			AttributeStoreAdapter newStore = createAdAttributeStore(configuration);
			if (!configuration.isWorkingCopy() || fAttributeStore == null || (fAttributeStore != null && !fAttributeStore.equals(newStore))) {
				fAttributeStore = newStore;
			}
			fRoot.initializeFrom(fAttributeStore);
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
			updateLaunchConfigurationDialog();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		try {
			AttributeStoreAdapter newStore = createAdAttributeStore(configuration);
			if (fAttributeStore != null && !fAttributeStore.equals(newStore)) {
				fAttributeStore = newStore;
			}
			fRoot.performApply(fAttributeStore);
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
			updateLaunchConfigurationDialog();
		}
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
		try {
			return fRoot.isValid(createAdAttributeStore(configuration));
		}
		catch(CoreException e) {
			// TODO: report error
			return false;
		}
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
			display = getShell().getDisplay();
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
	
	protected AttributeStoreAdapter createAdAttributeStore(ILaunchConfiguration configuration) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = null;
		if (configuration instanceof ILaunchConfigurationWorkingCopy) {
			wc = (ILaunchConfigurationWorkingCopy)configuration;
		}
		else {
			wc = configuration.getWorkingCopy();
		}
		return new AttributeStoreAdapter(wc);
	}
}
