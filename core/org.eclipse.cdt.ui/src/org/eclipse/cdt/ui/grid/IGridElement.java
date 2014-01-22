package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.widgets.Composite;

public interface IGridElement {
	
	static public int DEFAULT_WIDTH = 5;
	
	/* Adds elements of this one into composite.
	 * 
	 * 
	 */
	public void fillIntoGrid(Composite parent);
	
	//public void fillIntoGrid()
	

}
