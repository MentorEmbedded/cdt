package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.ui.grid.StringReflectionPresentationModel;


public class ProjectPresentationModel extends StringReflectionPresentationModel {
	
	public ProjectPresentationModel(String name, Object element, String get, String set) {
		super(name, element, get, set);
	}
	
	public ICProject getCProject() {
		String projectName = getValue().trim();
		if (projectName.isEmpty())
			return null;
		return CoreModel.getDefault().getCModel().getCProject(projectName);
	}
	
}
