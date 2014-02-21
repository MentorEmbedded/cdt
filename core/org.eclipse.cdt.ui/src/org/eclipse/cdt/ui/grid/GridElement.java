package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @since 5.7
 */
public abstract class GridElement {
	
	static public int DEFAULT_WIDTH = 5;
	
	public GridElement()
	{
		populateChildren();
		disposeListener = new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				childControls.remove((Control)e.getSource());
			}
		};
	}
	
	/* Adds elements of this one into composite.
	 * 
	 * 
	 */
	public void fillIntoGrid(Composite parent)
	{
		this.parent = parent;
		
		// Create immediate content, and remember all created
		// controls. Since control can be moved around in the
		// parent, we can't just store indices.
		int begin = parent.getChildren().length;
		createImmediateContent(parent);
		int end = parent.getChildren().length;
		
		childControls = new ArrayList<Control>(end - begin);
		for (int i = begin; i < end; ++i)
			childControls.add(parent.getChildren()[i]);
		
		for (final Control c: childControls) {
			c.addDisposeListener(disposeListener);
		}
				
		createChildrenContent(parent);
		adjustChildren(parent);
	}
	
	public List<Control> getChildControls()
	{
		return Collections.unmodifiableList(childControls);
	}
	
	public List<GridElement> getChildElements()
	{
		return Collections.unmodifiableList(childElements);
	}
	
	public void dispose()
	{
		for(Control c: childControls) {
			// FIXME: how can this ever happen?
			if (c.isDisposed())
				continue;
			c.removeDisposeListener(disposeListener);
			c.dispose();
		}
		for (GridElement c: childElements) {
			c.dispose();
		}
	}
	
	/** Add a children element that will be filled into
	 *  grid by a later call to fillIntoGrid.
	 */
	protected void addChild(GridElement child)
	{
		childElements.add(child);
	}
	
	/** Called by the constructor. Can create child element
	 *  via addChild.
	 */
	protected void populateChildren()
	{
	}

	/** Called by fillIntoGrid and must create controls that
	 * constitute this element.
	 */
	protected abstract void createImmediateContent(Composite parent);
	
	/** Called by fillIntoGrid and creates control for children
	 *  GridElement instances. There should be normally no need
	 *  to override this method.
	 */
	protected void createChildrenContent(Composite parent)
	{
		for (GridElement c: childElements) {
			c.fillIntoGrid(parent);
		}
	}
	
	/** Called by fillIntoGrid after both immediate content and
	 *  children are created, and can adjust the content, for
	 *  example creating grouping bars or headers, or changing
	 *  fonts of children. This method is normally overridden
	 *  by grid elements that combine other grid elements.
	 */
	protected void adjustChildren(Composite parent) {
		
	}
	
	private Composite parent;
	private List<Control> childControls = new ArrayList<Control>();
	private List<GridElement> childElements = new ArrayList<GridElement>();
	private DisposeListener disposeListener;
}
