package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutablesListElement;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.ListPresentationModel;

/**
 * @since 4.3
 */
public class ExecutableListPresentationModel extends ListPresentationModel {
	
	private ExecutablesListElement element;
	private UIElementFactory factory;

	public ExecutableListPresentationModel(ExecutablesListElement executableListElement, UIElementFactory factory)
	{
		super("Executables");
		setId("executables");
		
		this.element = executableListElement;
		this.factory = factory;
		
		for (ILaunchElement e : executableListElement.getChildren()) {
			add(factory.createPresentationModel(e, true));
		}
	}
	
	public IPresentationModel newRunExecutable() {
		ExecutableElement e = element.addExecutable();
		IPresentationModel child = factory.createPresentationModel(e);
		add(child);
		notifyListeners(IPresentationModel.ACTIVATED, child);		
		return child;
	}

	public void newAttachExecutable() {
		ExecutableElement e = element.addAttachToProcess();
		IPresentationModel child = factory.createPresentationModel(e);
		add(child);
		notifyListeners(IPresentationModel.ACTIVATED, child);
	}
};
