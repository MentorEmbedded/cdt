package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;

/**
 * @since 5.7
 */
public abstract class GridElement {
	
	static public int DEFAULT_WIDTH = 5;
	
	public GridElement()
	{
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
		begin = parent.getChildren().length;
		createImmediateContent(parent);
		int end = parent.getChildren().length;
		
		childControls = new ArrayList<Control>(end - begin);
		for (int i = begin; i < end; ++i)
			childControls.add(parent.getChildren()[i]);
		
		begin = -1;
		
		for (final Control c: childControls) {
			c.addDisposeListener(disposeListener);
			if (!visible) {
				c.setVisible(false);
				CDTUITools.getGridLayoutData(c).exclude = true;
			}
		}
				
		createChildrenContent(parent);
		adjustChildren(parent);
	}
	
	public List<Control> getChildControls()
	{
		if (begin == -1)		
			return Collections.unmodifiableList(childControls);
		
		// We're indirectly called from createImmediateContent,
		// so obtain the list from parent.
		return Arrays.asList(Arrays.copyOfRange(parent.getChildren(), begin, parent.getChildren().length));		
	}
	
	public List<GridElement> getChildElements()
	{
		return Collections.unmodifiableList(childElements);
	}
	
	// Get the controls that consitute the first row - either
	// created by createImmediateContent or by a child element.
	public List<Control> getFirstRow()
	{
		if (!childControls.isEmpty()) {
			return Collections.unmodifiableList(childControls);
		} else {
			for (GridElement child: childElements) {
				List<Control> maybeThese = child.getFirstRow();
				if (!maybeThese.isEmpty())
					return maybeThese;
			}
			return Collections.emptyList();
		}			
	}
	
	public void setVisible(boolean v)
	{
		visible = v;
		for (Control c: getChildControls()) {
			c.setVisible(v);
			CDTUITools.getGridLayoutData(c).exclude = !v;
		}
		for (GridElement c: childElements) {
			c.setVisible(v);
		}
		if (this.parent != null)
			this.parent.layout();
	}
	
	private int getGridWidth()
	{
		return DEFAULT_WIDTH;
	}
	
	// Makes the content be indented, by creating empty
	// label in the column 0, moving previous content of
	// column 0 to column 2 and reducing span of the column
	// 3.
	// Repeats same for child elements.
	public Label indent()
	{
		assert !childControls.isEmpty() || !childElements.isEmpty();
		
		Label result = indentChildControls();
		
		for (GridElement c: childElements) {
			Label result2 = c.indent();
			if (result == null)
				result = result2;
		}
		
		return result;
	}

	private Label indentChildControls() {
		
		if (childControls.isEmpty())
			return null;
		
		// If setVisible was previously called on this GridElement, we want
		// any extra labels we add below to have the same visibility.
		
		Label result = null;
		
		List<Control> children = getChildControls();
		for (int labelIndex = 0, contentIndex = 2, buttonsIndex = 3; buttonsIndex < children.size();)
		{
			Control label = children.get(labelIndex);
			Control content = children.get(contentIndex);
			Control buttons = children.get(buttonsIndex);
			
			Label newLabel = new Label(parent, SWT.NONE);
			newLabel.setVisible(visible);
			CDTUITools.getGridLayoutData(newLabel).exclude = !visible;
			childControls.add(newLabel);
			newLabel.moveAbove(label);
			label.moveAbove(content);
			
			if (labelIndex == 0)
				result = newLabel;
			
			assert CDTUITools.getGridLayoutData(content).horizontalSpan == 2;
			CDTUITools.getGridLayoutData(content).horizontalSpan = 1;
			
			assert CDTUITools.getGridLayoutData(buttons).horizontalSpan == 1;
			
			labelIndex += 5;
			contentIndex = labelIndex + 2;
			buttonsIndex = contentIndex + 1;
		}
		return result;
	}
	
	public void dispose()
	{
		for(Control c: getChildControls()) {
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
		assert child != null;
		childElements.add(child);
		// FIXME: review visibility hierarchy.
		if (!visible)
			child.setVisible(visible);
	}
	
	/** Called by fillIntoGrid and must create controls that
	 * constitute this element.
	 */
	protected void createImmediateContent(Composite parent)
	{		
	}
	
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
	
	/** Makes 'c' be part of this GridElement. 
	 * 
	 * @param c
	 */
	public void addChildControlFromOutside(Control c) {
		childControls.add(c);
	}
	
	private Composite parent;
	private List<Control> childControls = new ArrayList<Control>();
	private List<GridElement> childElements = new ArrayList<GridElement>();
	private DisposeListener disposeListener;
	// While createImmediateContent is executing, index of the first control
	// we'd create in our parent. -1 otherwise.
	private int begin = -1;
	private boolean visible = true;
}
