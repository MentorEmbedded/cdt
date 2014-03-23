package org.eclipse.cdt.ui.grid;

import java.util.List;

/**
 * @since 5.7
 */
public interface ICompositePresentationModel extends IPresentationModel {
	
	public List<IPresentationModel> getChildren(); 

	public List<String> getClasses();
	
}
