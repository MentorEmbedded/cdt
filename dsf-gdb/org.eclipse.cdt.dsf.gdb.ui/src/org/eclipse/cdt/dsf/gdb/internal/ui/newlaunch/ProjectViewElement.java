package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import java.util.ArrayList;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.IGdbUIConstants;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.ui.CElementLabelProvider;
import org.eclipse.cdt.ui.grid.StringViewElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class ProjectViewElement extends StringViewElement {
	
	public ProjectViewElement(ProjectPresentationModel model)
	{
		super(model);
	}
	
	@Override
	public ProjectPresentationModel getModel() {
		return (ProjectPresentationModel)super.getModel();
	}
	
	@Override
	protected void createButton(final Composite parent) {
		
		Button browseButton = new Button(parent, SWT.PUSH);
		browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		browseButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		browseButton.setToolTipText(LaunchMessages.getString("Launch.common.Browse_2")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleProjectButtonSelected(parent.getShell());
			}
		});
	}
	
	protected void handleProjectButtonSelected(Shell shell) {
		String currentProjectName = text.getText();
		ICProject project = chooseCProject(shell);
		if (project == null) {
			return;
		}

		getModel().setValue(project.getElementName());
		/* FIXME: move into executable view element.
		String projectName = project.getElementName();
		text.setText(projectName);
		if (currentProjectName.length() == 0) {
			// New project selected for the first time, set the program name default too.
			IBinary[] bins = getBinaryFiles(shell, project);
			if (bins != null && bins.length == 1) {				
				text.setText(bins[0].getResource().getProjectRelativePath().toOSString());
			}
		}*/
	}
	
	protected ICProject chooseCProject(Shell shell) {
		try {
			ICProject[] projects = getCProjects();

			ILabelProvider labelProvider = new CElementLabelProvider();
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, labelProvider);
			dialog.setTitle("Project Selection"); 
			dialog.setMessage("Choose project to constrain search for program"); 
			dialog.setElements(projects);

			ICProject cProject = getModel().getCProject();
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
		// TODO: I am unsure how this ends up used.
		return Platform.getOS();
	}	
}
