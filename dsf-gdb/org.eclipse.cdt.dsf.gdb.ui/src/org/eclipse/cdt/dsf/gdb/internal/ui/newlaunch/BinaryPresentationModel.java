package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.StringReflectionPresentationModel;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * @since 5.7
 */
public class BinaryPresentationModel extends StringReflectionPresentationModel {
	
	IPresentationModel.Listener listener = new IPresentationModel.Listener() {
		public void changed(int what, Object object) {
			if ((what & IPresentationModel.VALUE_CHANGED) != 0) {
				if (lastSeenProjectName.isEmpty() && !getProjectModel().getValue().isEmpty()) {
					// Switching from no project name to some project name for the first time.
					// Automatically pick a binary.
					// FIXME: I am not entirely sure this is the right place to mess with UI.
					IBinary[] bins = getBinaryFiles(Display.getCurrent().getActiveShell(), getProjectModel().getCProject());
					if (bins != null && bins.length == 1) {				
						setValue(bins[0].getResource().getProjectRelativePath().toOSString());
					}					
				}
			}
		};
	};
	
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
	
	public BinaryPresentationModel(String name, Object element, String get, String set) {
		super(name, element, get, set);
	}
	
	public ProjectPresentationModel getProjectModel()
	{
		return projectModel;
	}
	
	public void setProjectModel(ProjectPresentationModel model)
	{
		if (projectModel != null)
			projectModel.removeListener(listener);
		
		projectModel = model;
		lastSeenProjectName = projectModel.getValue();
		projectModel.addAndCallListener(listener);
	}
	
	private ProjectPresentationModel projectModel;
	private String lastSeenProjectName;
}
