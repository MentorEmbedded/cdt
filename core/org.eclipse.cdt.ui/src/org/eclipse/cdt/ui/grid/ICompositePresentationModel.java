package org.eclipse.cdt.ui.grid;

import java.util.List;

/**
 * @since 6.0
 */
public interface ICompositePresentationModel extends IPresentationModel {
	
	public List<IPresentationModel> getChildren(); 

	public List<String> getClasses();
	
}
