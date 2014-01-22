package org.eclipse.cdt.ui.grid;

import java.util.List;

public interface ICompositePresentationModel extends IPresentationModel {
	
	public List<IPresentationModel> getChildren(); 

}
