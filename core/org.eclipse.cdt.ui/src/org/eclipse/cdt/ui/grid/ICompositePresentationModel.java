package org.eclipse.cdt.ui.grid;

import java.util.List;

/**
 * @since 5.7
 */
public interface ICompositePresentationModel extends ISomePresentationModel {
	
	public List<ISomePresentationModel> getChildren(); 

}
