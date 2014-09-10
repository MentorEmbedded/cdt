package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/** This is prototype of how grid element can be done if we had a custom layout.
 * @since 6.0
 */
public abstract class GridElement2 {
	
	public GridElement2(Composite parent)
	{
		this.parent = parent;
	}

	public void create()
	{
		doCreate();
	}
	
	abstract protected void doCreate();
	
	public void add(Control c, int row, int column) {}
	
	public void add(Control c, int row, int column, int span, int minSpan) {}
	
	public void add(GridElement2 child, int row, int column) {}
	
	public void setVisible(boolean visible) {}
	
	public int getNumRows() { return 0; }
	public int getNumColums() { return 0; }
	
	public int getRowMinWidth(int row) { return 0; }
	
	// Methods to enumerate children
	
	
	
	
	Composite parent;
}
