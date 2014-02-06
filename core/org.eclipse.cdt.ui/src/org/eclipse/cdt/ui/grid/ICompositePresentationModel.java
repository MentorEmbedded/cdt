package org.eclipse.cdt.ui.grid;

import java.util.List;

public interface ICompositePresentationModel extends ISomePresentationModel {
	
	public List<ISomePresentationModel> getChildren(); 

}
