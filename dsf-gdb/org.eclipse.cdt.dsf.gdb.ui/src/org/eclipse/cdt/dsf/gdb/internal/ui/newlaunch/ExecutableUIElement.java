package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.debug.ui.dialogs.IAttributeStore;
import org.eclipse.cdt.debug.ui.dialogs.UIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.IGdbUIConstants;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.ui.CElementLabelProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

/**
 * @since 7.4
 */
public class ExecutableUIElement extends UIElement {

	final private static String ELEMENT_ID = ".executable"; //$NON-NLS-1$

	// Summary view widgets
	private StyledText fSummaryText;

	// Details view widgets
	private Text fProgText;
	private Text fProjText;
	private Button fSearchButton;

	private String fPlatformFilter = ""; //$NON-NLS-1$

	public ExecutableUIElement(UIElement parentElement) {
		super(parentElement.getId() + ELEMENT_ID, parentElement, "Executable", "Executable to debug");
	}

	protected ExecutableUIElement(UIElement parentElement, String label, String description) {
		super(parentElement.getId() + ELEMENT_ID, parentElement, label, description);
	}

	@Override
	protected boolean isRemovable() {
		return true;
	}

	@Override
	protected void doCreateChildren(IAttributeStore store) {
		addChildren(new UIElement[] {
			new BuildSettingsUIElement(this),
			new RuntimeUIElement(this),
		});
	}

	@Override
	protected void doCreateSummaryContent(Composite parent, IAttributeStore store) {
		Composite base = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		base.setLayout(layout);
		base.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		fSummaryText = new StyledText(base, SWT.NONE);
		fSummaryText.setBackground(fSummaryText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	}

	@Override
	public void initializeFrom(IAttributeStore store, Composite parent) {
		fPlatformFilter = getPlatform(store);
		super.initializeFrom(store, parent);
	}

	@Override
	protected void initializeSummaryContent(IAttributeStore store) {
		if (store == null || fSummaryText == null) {
			return;
		}
		try {
			String programName = store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, ""); //$NON-NLS-1$
			String projectName = store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
			StringBuilder sb = new StringBuilder(programName);
			if (projectName != null && !projectName.isEmpty()) {
				sb.append(" in project ");
				sb.append(projectName);
			}
			fSummaryText.setText(sb.toString());
			
			StyleRange style1 = new StyleRange();
		    style1.start = 0;
		    style1.length = programName.length();
		    style1.fontStyle = SWT.BOLD;
		    fSummaryText.setStyleRange(style1);
			    
			if (projectName != null && !projectName.isEmpty()) {
			    StyleRange style2 = new StyleRange();
			    style2.start = sb.length() - projectName.length();
			    style2.length = projectName.length();
			    style2.fontStyle = SWT.BOLD;
			    fSummaryText.setStyleRange(style2);
			}
		}
		catch(CoreException e) {
			getStatusListener().errorReported(e.getLocalizedMessage());
		}
	}

	@Override
	protected void initializeDetailsContent(IAttributeStore store) {
		if (store == null) {
			return;
		}
		if (fProgText != null) {
			String programName = ""; //$NON-NLS-1$
			try {
				programName = store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, ""); //$NON-NLS-1$
			} 
			catch(CoreException ce) {
				getStatusListener().errorReported(ce.getLocalizedMessage());
			}
			fProgText.setText(programName);
		}

		String projectName = ""; //$NON-NLS-1$
		try {
			projectName = store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
		} 
		catch(CoreException ce) {
			getStatusListener().errorReported(ce.getLocalizedMessage());
		}
		if (fProjText != null && !fProjText.getText().equals(projectName)) {
			fProjText.setText(projectName);
		}
	}

	@Override
	protected Composite createDetailsContent(Composite parent, IAttributeStore store) {
		Composite base = super.createDetailsContent(parent, store);
		createExecFileGroup(base);
		createProjectGroup(base);
		GridUtils.addHorizontalSeparatorToGrid(parent, 4);
		return base;
	}

	@Override
	public void performApply(IAttributeStore store) {
		if (fProgText != null) {
			store.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, fProgText.getText());
		}
		if (fProjText != null) {
			store.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText());
		}
	}

	@Override
	public void setDefaults(IAttributeStore store) {
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fSummaryText = null;
		fProgText = null;
		fProjText = null;
		fSearchButton = null;
	}

	protected void createExecFileGroup(final Composite parent) {
		Label progLabel = new Label(parent, SWT.NONE);
		progLabel.setText(LaunchMessages.getString("CMainTab.C/C++_Application")); //$NON-NLS-1$
		GridData gd = new GridData();
		progLabel.setLayoutData(gd);

		Composite progTextComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = layout.marginWidth = 0;
		progTextComp.setLayout(layout);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		progTextComp.setLayoutData(gd);
		
		fProgText = new Text(progTextComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProgText.setLayoutData(gd);
		fProgText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
            	if (!isInitializing()) {
            		getChangeListener().elementChanged(ExecutableUIElement.this);
            	}
			}
		});

		Button browseButton = new Button(progTextComp, SWT.PUSH);
		browseButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		browseButton.setToolTipText(LaunchMessages.getString("Launch.common.Browse_2")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				fProgText.setText(handleBrowseButtonSelected(
					parent.getShell(), 
					LaunchMessages.getString("CMaintab.Application_Selection"))); //$NON-NLS-1$
				getChangeListener().elementChanged(ExecutableUIElement.this);
			}
		});
		
		fSearchButton = new Button(progTextComp, SWT.PUSH);
		fSearchButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_SEARCH_PROJECT));
		fSearchButton.setToolTipText(LaunchMessages.getString("CMainTab.Search...")); //$NON-NLS-1$
		fSearchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleSearchButtonSelected(parent.getShell());
			}
		});
		
		Button varButton = new Button(progTextComp, SWT.PUSH);
		varButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_PATH_VARIABLES));
		varButton.setToolTipText(LaunchMessages.getString("CMainTab.Variables")); //$NON-NLS-1$
		varButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleVariablesButtonSelected(parent.getShell(), fProgText);
			}
		});
	}

	protected void createProjectGroup(final Composite parent) {
//		Composite projComp = new Composite(parent, SWT.NONE);
//		GridLayout projLayout = new GridLayout();
//		projLayout.numColumns = 2;
//		projLayout.marginHeight = 0;
//		projLayout.marginWidth = 0;
//		projComp.setLayout(projLayout);
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		projComp.setLayoutData(gd);

		Label fProjLabel = new Label(parent, SWT.NONE);
		fProjLabel.setText(LaunchMessages.getString("CMainTab.&ProjectColon")); //$NON-NLS-1$
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		fProjLabel.setLayoutData(gd);

		Composite projTextComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = layout.marginWidth = 0;
		projTextComp.setLayout(layout);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		projTextComp.setLayoutData(gd);
		
		fProjText = new Text(projTextComp, SWT.SINGLE | SWT.BORDER);
		fProjText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fProjText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent evt) {
				// if project changes, invalidate program name cache
//				fPreviouslyCheckedProgram = null;
//				updateBuildConfigCombo(EMPTY_STRING);
//				updateLaunchConfigurationDialog();
			}
		});

		Button browseButton = new Button(projTextComp, SWT.PUSH);
		browseButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		browseButton.setToolTipText(LaunchMessages.getString("Launch.common.Browse_2")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleProjectButtonSelected(parent.getShell());
			}
		});
/*
		Button fProjButton = SWTFactory.createPushButton(projComp, LaunchMessages.getString("Launch.common.Browse_1"), null);  //$NON-NLS-1$
		fProjButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
//				handleProjectButtonSelected();
//				updateLaunchConfigurationDialog();
			}
		});
*/
	}

	@Override
	protected void remove(IAttributeStore store) {
		super.remove(store);
		store.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, ""); //$NON-NLS-1$
		store.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
	}

	protected String handleBrowseButtonSelected(Shell shell, String title) {
		FileDialog fileDialog = new FileDialog(shell, SWT.NONE);
		fileDialog.setText(title);
		fileDialog.setFileName(fProgText.getText());
		return fileDialog.open();
	}

	protected void handleSearchButtonSelected(Shell shell) {
		if (getCProject() == null) {
			MessageDialog.openInformation(shell, LaunchMessages.getString("CMainTab.Project_required"), //$NON-NLS-1$
					LaunchMessages.getString("CMainTab.Enter_project_before_searching_for_program")); //$NON-NLS-1$
			return;
		}

		ILabelProvider programLabelProvider = new CElementLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IBinary) {
					IBinary bin = (IBinary)element;
					StringBuffer name = new StringBuffer();
					name.append(bin.getPath().lastSegment());
					return name.toString();
				}
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element) {
				if (! (element instanceof ICElement)) {
					return super.getImage(element);
				}
				ICElement celement = (ICElement)element;

				if (celement.getElementType() == ICElement.C_BINARY) {
					IBinary belement = (IBinary)celement;
					if (belement.isExecutable()) {
						return DebugUITools.getImage(IDebugUIConstants.IMG_ACT_RUN);
					}
				}

				return super.getImage(element);
			}
		};

		ILabelProvider qualifierLabelProvider = new CElementLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IBinary) {
					IBinary bin = (IBinary)element;
					StringBuffer name = new StringBuffer();
					name.append(bin.getCPU() + (bin.isLittleEndian() ? "le" : "be")); //$NON-NLS-1$ //$NON-NLS-2$
					name.append(" - "); //$NON-NLS-1$
					name.append(bin.getPath().toString());
					return name.toString();
				}
				return super.getText(element);
			}
		};

		TwoPaneElementSelector dialog = new TwoPaneElementSelector(shell, programLabelProvider, qualifierLabelProvider);
		dialog.setElements(getBinaryFiles(shell, getCProject()));
		dialog.setMessage(LaunchMessages.getString("CMainTab.Choose_program_to_run")); //$NON-NLS-1$
		dialog.setTitle(LaunchMessages.getString("CMainTab.Program_Selection")); //$NON-NLS-1$
		dialog.setUpperListLabel(LaunchMessages.getString("Launch.common.BinariesColon")); //$NON-NLS-1$
		dialog.setLowerListLabel(LaunchMessages.getString("Launch.common.QualifierColon")); //$NON-NLS-1$
		dialog.setMultipleSelection(false);
		// dialog.set
		if (dialog.open() == Window.OK) {
			IBinary binary = (IBinary)dialog.getFirstResult();
			fProgText.setText(binary.getResource().getProjectRelativePath().toString());
		}
	}

	protected ICProject getCProject() {
		if (fProjText == null)
			return null;
		String projectName = fProjText.getText().trim();
		if (projectName.length() < 1) {
			return null;
		}
		return CoreModel.getDefault().getCModel().getCProject(projectName);
	}

	protected IBinary[] getBinaryFiles(Shell shell, final ICProject cproject) {
		if (cproject == null || !cproject.exists()) {
			return null;
		}
		final Display display = shell.getDisplay();
		final IBinary[][] ret = new IBinary[1][];
		BusyIndicator.showWhile(display, new Runnable() {
			@Override
			public void run() {
				try {
					ret[0] = cproject.getBinaryContainer().getBinaries();
				} catch (CModelException e) {
					LaunchUIPlugin.errorDialog("Launch UI internal error", e); //$NON-NLS-1$
				}
			}
		});

		return ret[0];
	}

	/**
	 * A variable entry button has been pressed for the given text
	 * field. Prompt the user for a variable and enter the result
	 * in the given field.
	 */
	private void handleVariablesButtonSelected(Shell shell, Text textField) {
		String variable = getVariable(shell);
		if (variable != null) {
			textField.insert(variable);
		}
	}

	/**
	 * Prompts the user to choose and configure a variable and returns
	 * the resulting string, suitable to be used as an attribute.
	 */
	private String getVariable(Shell shell) {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(shell);
		dialog.open();
		return dialog.getVariableExpression();
	}

	/**
	 * Show a dialog that lets the user select a project. This in turn provides context for the main
	 * type, allowing the user to key a main type name, or constraining the search for main types to
	 * the specified project.
	 */
	protected void handleProjectButtonSelected(Shell shell) {
		String currentProjectName = fProjText.getText();
		ICProject project = chooseCProject(shell);
		if (project == null) {
			return;
		}

		String projectName = project.getElementName();
		fProjText.setText(projectName);
		if (currentProjectName.length() == 0) {
			// New project selected for the first time, set the program name default too.
			IBinary[] bins = getBinaryFiles(shell, project);
			if (bins != null && bins.length == 1) {				
				fProgText.setText(bins[0].getResource().getProjectRelativePath().toOSString());
			}
		}
	}

	protected ICProject chooseCProject(Shell shell) {
		try {
			ICProject[] projects = getCProjects();

			ILabelProvider labelProvider = new CElementLabelProvider();
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, labelProvider);
			dialog.setTitle("Project Selection"); 
			dialog.setMessage("Choose project to constrain search for program"); 
			dialog.setElements(projects);

			ICProject cProject = getCProject();
			if (cProject != null) {
				dialog.setInitialSelections(new Object[] { cProject });
			}
			if (dialog.open() == Window.OK) {
				return (ICProject)dialog.getFirstResult();
			}
		} catch (CModelException e) {
			LaunchUIPlugin.errorDialog("Launch UI internal error", e); //$NON-NLS-1$			
		}
		return null;
	}

	/**
	 * Return an array a ICProject whose platform match that of the runtime env.
	 */
	protected ICProject[] getCProjects() throws CModelException {
		ICProject cproject[] = CoreModel.getDefault().getCModel().getCProjects();
		ArrayList<ICProject> list = new ArrayList<ICProject>(cproject.length);

		for (int i = 0; i < cproject.length; i++) {
			ICDescriptor cdesciptor = null;
			try {
				cdesciptor = CCorePlugin.getDefault().getCProjectDescription((IProject) cproject[i].getResource(), false);
				if (cdesciptor != null) {
					String projectPlatform = cdesciptor.getPlatform();
					if (fPlatformFilter.equals("*") //$NON-NLS-1$
						|| projectPlatform.equals("*") //$NON-NLS-1$
						|| fPlatformFilter.equalsIgnoreCase(projectPlatform) == true) {
						list.add(cproject[i]);
					}
				} else {
					list.add(cproject[i]);
				}
			} catch (CoreException e) {
				list.add(cproject[i]);
			}
		}
		return list.toArray(new ICProject[list.size()]);
	}

	protected String getPlatform(IAttributeStore store) {
		String platform = Platform.getOS();
		try {
			return store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PLATFORM, platform);
		} catch (CoreException e) {
			return platform;
		}
	}

	@Override
	public boolean isContentValid(IAttributeStore store) {
		try {
			String programName = (fProgText != null) ? 
					fProgText.getText().trim() : 
					store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, ""); //$NON-NLS-1$
			String projectName = (fProjText != null) ? 
					fProjText.getText().trim() : 
					store.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
			if (!isProgramNameValid(store, programName, projectName)) {
				return false;
			}
		}
		catch(CoreException e) {
			setErrorMessage(e.getLocalizedMessage());
			return false;
		}
		return true;
	}

	protected boolean isProgramNameValid(IAttributeStore store, String programName, String projectName) {
   		try {
			programName = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(programName);
		} catch (CoreException e) {
			// Silently ignore substitution failure (for consistency with "Arguments" and "Work directory" fields)
		}
		if (programName.length() == 0) {
			setErrorMessage(LaunchMessages.getString("CMainTab.Program_not_specified")); //$NON-NLS-1$
			return false;
		}
		if (programName.equals(".") || programName.equals("..")) { //$NON-NLS-1$ //$NON-NLS-2$
			setErrorMessage(LaunchMessages.getString("CMainTab.Program_does_not_exist")); //$NON-NLS-1$
			return false;
		}
		IPath exePath = new Path(programName);
		if (exePath.isAbsolute()) {
			// For absolute paths, we don't need a project, we can debug the binary directly
			// as long as it exists
			File executable = exePath.toFile();
			if (!executable.exists()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Program_does_not_exist")); //$NON-NLS-1$
				return false;
			}
			if (!executable.isFile()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Selection_must_be_file")); //$NON-NLS-1$
				return false;
			}
		} else {
			// For relative paths, we need a proper project
			if (projectName.length() == 0) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Project_not_specified")); //$NON-NLS-1$
				return false;
			}
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (!project.exists()) {
				setErrorMessage(LaunchMessages.getString("Launch.common.Project_does_not_exist")); //$NON-NLS-1$
				return false;
			}
			if (!project.isOpen()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Project_must_be_opened")); //$NON-NLS-1$
				return false;
			}
			if (!project.getFile(programName).exists()) {
				setErrorMessage(LaunchMessages.getString("CMainTab.Program_does_not_exist")); //$NON-NLS-1$
				return false;
			}
		}
		// Notice that we don't check if exePath points to a valid executable since such
		// check is too expensive to be done on the UI thread.
		// See "https://bugs.eclipse.org/bugs/show_bug.cgi?id=328012".
		return true;
	}
}
