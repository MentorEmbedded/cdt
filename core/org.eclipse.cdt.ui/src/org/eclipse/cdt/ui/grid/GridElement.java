package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;

/** GridElement is a collection of UI controls that are aligned on a master grid of a
 *  dialog box or other window.
 *  
 *  Normally, SWT widgets are combined used Composite class, and layout happens independently
 *  inside each Composite. As the result, controls that are part of same dialog, but different
 *  Composite instances, are often not aligned to each other. Various property editors exhibit
 *  the issue most often.
 *  
 *  JFace FieldEditor class attempted to address this problem, but does not implement a lot
 *  of useful functionality. This class is a new attempt, with support for
 *  
 *  - Multi-row elements and general hierarchy of elements
 *  - Showing and hiding
 *  - Indentation
 *  - Spacing between rows
 *  
 *  It's useful to compare SWT Composite, HTML and this implementation. All these have
 *  rectangular boxes. With SWT Composite, children boxes are always inside parent box, and
 *  laid out locally. With HTML, children boxes are initially laid out locally, but they
 *  can be placed outside of parent box, and be laid out from outside. With GridElement,
 *  only leafs in the hierarchy have boxes, everything else is represented by GridElement.
 *  Those leaf boxes are put on a single grid. This is not yet as flexible as HTML, but
 *  it does workaround local layout constraint of Composite to achive global alignment. 
 *  
 *  HTML analogy does not stop here - we want GridElement to basically behave like HTML
 *  div element - in particular, it can be manipulated at all times. Adding or removing
 *  children, or changing properties, takes effect immediately.
 *  
 *  Key design points:
 *  
 *  0. When in doubt, make this behave like HTML div
 *  
 *  1. The construction is two step - first one invoke constructor, which does 
 *     almost nothing, then one invokes fillIntoGrid, passing the parent control. 
 *     At this point, controls are created and children GridElements are added.
 *     We do this in two step so that derived classes can safely customize the
 *     behaviour. 
 *     
 *     We could do this in three steps - where the second steps creates
 *     a hierarchy of GridElement instances and the third step creates controls.
 *     It would permit manipulation of children before controls are already created,
 *     but it would also complicate the logic a fair bit - where most code needs to
 *     care whether controls are created or not.
 *     
 *  2. GridLayout does not permit putting elements in specific rows or columns. Rather,
 *     it just iterates over children assigning rows and columns to them. For that
 *     reason, GridElement will only look OK when the order of children GridElements
 *     matches the order of their children. I.e. we shall add children in the same order
 *     we create them. We also don't have a way to reorder children right now.
 *     
 *     We can implement limited manipulation using Control.moveAbove, but it's rather
 *     inefficient. At this point, the best future direction appears to rewrite
 *     GridLayout to support explicit row/column specification for children.
 *     
 *    
 * @since 6.0
 */
public abstract class GridElement {
	
	static public int DEFAULT_WIDTH = 5;
	private int spacing;
	
	public GridElement()
	{
		// FIXME: Must insist that children are not disposed, except via our own
		// dispose method.
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
	public void create(Composite parent)
	{
		if (controlsCreated)
			return;
		
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
			if (!visible || !parentVisible) {
				c.setVisible(false);
				CDTUITools.getGridLayoutData(c).exclude = true;
			}
		}
		
		if (indented)
			indent();
		
		controlsCreated = true;
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
		
	public GridElement setVisible(boolean v)
	{
		visible = v;
		updateVisibility();
		return this;
	}
	
	public GridElement setParentVisible(boolean v)
	{
		parentVisible = v;
		updateVisibility();
		return this;
	}
	
	private void updateVisibility()
	{
		boolean v = visible && parentVisible;
		// FIXME: do nothing if no change.
		
		for (Control c: getChildControls()) {
			c.setVisible(v);
			CDTUITools.getGridLayoutData(c).exclude = !v;
		}
		for (GridElement c: childElements) {
			c.setParentVisible(v);
		}
		if (this.parent != null)
			this.parent.layout();		
	}
	
	// Makes the content be indented, by creating empty
	// label in the column 0, moving previous content of
	// column 0 to column 2 and reducing span of the column
	// 3.
	// Repeats same for child elements.
	// FIXME: this behaviour is rather specific and might
	// belong to a subclass.
	public GridElement setIndented(boolean indented)
	{
		if (indented != this.indented) {
			if (controlsCreated) {
				if (indented) {
					indentChildControls();
				} else {
					throw new UnsupportedOperationException();
				}
			}
			
			for (GridElement c: childElements) {
				c.setIndented(indented);
			}
			
			this.indented = true;
			
			if (parent != null)
				parent.layout();
		}
		
		return this;
	}
	
	protected void indent()
	{	
		assert !childControls.isEmpty() || !childElements.isEmpty();
		
		indentChildControls();
		
		for (GridElement c: childElements) {
			c.indent();			
		}
	}
	
	// Add a button to the rightmost column of this element.
	// The default implementation works only for single-row
	// elements.
	public GridElement setButton(Control b)
	{
		assert rowCount() == 1;
		assert getChildElements().isEmpty();
		
		Control last = getChildControls().get(getChildControls().size()-1);
		b.moveAbove(last);
		childControls.add(b);
		last.dispose();
		childControls.remove(last);
		
		return this;
	}
	
	private int rowCount()
	{
		int span = 0;
		for (Control c: getChildControls()) {
			span += CDTUITools.getGridLayoutData(c).horizontalSpan;
		}
		for (GridElement c: getChildElements()) 
			span += c.rowCount();
		
		return span/DEFAULT_WIDTH;
	}
	
	// FIXME: this method only works when invoked before calling addChild,
	// and therefore a typical code that creates GridElement using base factory
	// and then calls 'spacing' on the result just has no effect.
	//
	// One way to fix this is to make this method go an insert spacers between
	// grid elements. Another way is to just write custom layout, as suggested
	// above.
	public GridElement spacing(int spacing)
	{
		this.spacing = spacing;
		return this;
	}
		
	public Label getTopLeftLabel()
	{
		if (getChildControls().size() != 0)
		{
			Control c = getChildControls().get(0);
			assert c instanceof Label;
			return (Label)c;
		}
		else {
			assert getChildElements().size() != 0;
			return getChildElements().get(0).getTopLeftLabel();
		}
			
	}
		
	protected void indentChildControls() {
		
		if (childControls.isEmpty())
			return;
		
		// If setVisible was previously called on this GridElement, we want
		// any extra labels we add below to have the same visibility.
			
		List<Control> children = getChildControls();
		for (int labelIndex = 0, contentIndex = 2, buttonsIndex = 3; buttonsIndex < children.size();)
		{
			Control label = children.get(labelIndex);
			Control content = children.get(contentIndex);
			Control buttons = children.get(buttonsIndex);
			
			Label newLabel = new Label(parent, SWT.NONE);
			newLabel.setVisible(visible);
			CDTUITools.getGridLayoutData(newLabel).exclude = !visible;
			// FIXME: likely breaks if there's more than one row.
			childControls.add(labelIndex, newLabel);
			newLabel.moveAbove(label);
			label.moveAbove(content);
			
			assert CDTUITools.getGridLayoutData(content).horizontalSpan == 2;
			CDTUITools.getGridLayoutData(content).horizontalSpan = 1;
			
			assert CDTUITools.getGridLayoutData(buttons).horizontalSpan == 1;
			
			labelIndex += 5;
			contentIndex = labelIndex + 2;
			buttonsIndex = contentIndex + 1;
		}
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
		if (this.parent != null)
			this.parent.layout();
	}
	
	/** Add a children element that will be filled into
	 *  grid by a later call to fillIntoGrid.
	 */
	public void addChild(GridElement child)
	{
		assert child != null;
		
		boolean firstChild = childElements.size() == 0;
		childElements.add(child);
				
		child.setParentVisible(visible);
		child.setIndented(indented);
		
		if (!firstChild && spacing != 0) {
			Label spacer = new Label(parent, SWT.NONE);
			GridData gd = new GridData();
			gd.horizontalSpan = DEFAULT_WIDTH;
			gd.heightHint = spacing;
			spacer.setLayoutData(gd);
		}
	}
	
	/** Called by fillIntoGrid and must create controls that
	 * constitute this element.
	 */
	protected void createImmediateContent(Composite parent)
	{		
	}
					
	protected Composite parent;
	private List<Control> childControls = new ArrayList<Control>();
	private List<GridElement> childElements = new ArrayList<GridElement>();
	
	private boolean visible = true;
	private boolean indented = false;
	private boolean parentVisible = true;
	
	// Whether we're already linked to control, that is, fillIntoGrid was already
	// called.
	private boolean controlsCreated = false;
	
	private DisposeListener disposeListener;
	// While createImmediateContent is executing, index of the first control
	// we'd create in our parent. -1 otherwise.
	private int begin = -1;
}
