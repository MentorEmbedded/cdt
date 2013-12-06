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

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.debug.internal.ui.CDebugImages;
import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.IGdbUIConstants;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.ui.CElementLabelProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

public class ExecutableUIElement extends AbstractUIElement {

	// Details view widgets
	private Text fProgText;
	private Text fProjText;
	private Button fSearchButton;

	public ExecutableUIElement(ExecutableElement launchElement, boolean showDetails ) {
		super(launchElement, showDetails);
	}

	@Override
	protected void createChildrenContent(Composite parent) {
		super.createChildrenContent(parent);
		if (isRemovable()) {
			createDeleteButton(parent);
		}
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		createExecFileGroup(parent);
		createProjectGroup(parent);
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fProgText = null;
		fProjText = null;
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
            	getExecutableElement().setProgramName(fProgText.getText().trim());
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
				getExecutableElement().setProjectName(fProjText.getText().trim());
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
					if (getPlatformFilter().equals("*") //$NON-NLS-1$
						|| projectPlatform.equals("*") //$NON-NLS-1$
						|| getPlatformFilter().equalsIgnoreCase(projectPlatform) == true) {
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
	
	protected String getPlatformFilter() {
		return ((ExecutableElement)getLaunchElement()).getPlatformFilter();
	}

	@Override
	protected void initializeDetailsContent() {
		fProgText.setText(getExecutableElement().getProgramName());
		String projName = getExecutableElement().getProjectName();
		if (projName != null) {
			fProjText.setText(projName);
		}
	}
	
	private ExecutableElement getExecutableElement() {
		return (ExecutableElement)getLaunchElement();
	}
	
	protected void createDeleteButton(Composite parent) {
		int horSpan = 1;
		Layout layout = parent.getLayout();
		if (layout instanceof GridLayout) {
			horSpan = ((GridLayout)layout).numColumns;
		}
		GridUtils.addHorizontalSeparatorToGrid(parent, horSpan);
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, horSpan, 1));
		button.setImage(CDebugImages.get(CDebugImages.IMG_LCL_REMOVE_UIELEMENT));
		button.setToolTipText("Delete this executable");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleDeleteButtonSelected();
			}
		});
	}
	
	protected void handleDeleteButtonSelected() {
		getLaunchElement().getParent().removeChild(getLaunchElement());
	}
}
