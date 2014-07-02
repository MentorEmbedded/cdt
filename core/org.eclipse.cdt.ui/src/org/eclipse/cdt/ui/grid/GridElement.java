package org.eclipse.cdt.ui.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
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
 *  It's useful to compare SWT Composite, HTML and this implementation. All of that have
 *  rectagular boxes. With SWT Composite, children boxes are always inside parent box, and
 *  layed out locally. With HTML, children boxes are initially laid out locally, but they
 *  can be placed outside of parent box, and be layed out from outside. With GridElement,
 *  only leafs in the hierarchy have boxes, everything else is represented by GridElement.
 *  Those leaf boxes are put on a single grid. This is not yet as flexible as HTML, but
 *  it does workaround local layout constraint of Composite to achive global alignment. 
 *  
 *  HTML analogy does not stop here - we want GridElement to basically behave like HTML
 *  div element - in particular, it can be manipulated at all times. Adding or removing
 *  children, or changing properties, takes effect immediately.
 *  
 *  Construction of this is two-step process - first an instance is created, then it's
 *  added to a grid using fillIntoGrid method. The motivation for that is to only do
 *  the layout once. New children might adjust column widths, and parent element might
 *  want to decorate children, so it's more convenient to delay rendering until entire
 *  tree is ready.
 *    
 * @since 5.7
 */
public abstract class GridElement {
	
	static public int DEFAULT_WIDTH = 5;
	private int spacing;
	
	public GridElement()
	{
		// FIXME: This insist that children are not disposed, except via our own
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
	
	// Makes the content be indented, by creating empty
	// label in the column 0, moving previous content of
	// column 0 to column 2 and reducing span of the column
	// 3.
	// Repeats same for child elements.
	// FIXME: this behaviour is rather specific and might
	// belong to a subclass.
	public void setIndented(boolean indented)
	{
		if (indented != this.indented) {
			if (controlsCreated) {
				if (indented) {
					indent();
				} else {
					throw new UnsupportedOperationException();
				}
			}
			this.indented = true;
		}
	}
	
	protected Label indent()
	{	
		assert !childControls.isEmpty() || !childElements.isEmpty();
		
		Label result = indentChildControls();
		
		for (GridElement c: childElements) {
			Label result2 = c.indent();
			if (result == null)
				result = result2;
		}
		
		indentationLabel = result;
		return result;
	}
	
	// Add a button to the rightmost column of this element.
	// The default implementation works only for single-row
	// elements.
	public GridElement addButton(Button b)
	{
		assert rowCount() == 1;
		assert getChildElements().isEmpty();
		
		Control last = getChildControls().get(getChildControls().size()-1);
		if (last instanceof Composite) {
			Composite composite = (Composite)last;
			b.setParent(composite);
		} else if (last instanceof Label) {
			Label label = (Label)last;
			assert label.getText().isEmpty();
			Composite composite = new Composite(parent, SWT.NONE);
			composite.moveAbove(label);
			FillLayout layout = new FillLayout(SWT.HORIZONTAL);
			layout.marginHeight = layout.marginWidth = 0;
			composite.setLayout(layout);
			b.setParent(composite);
			label.dispose();
			childControls.add(composite);
		}
		
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
	
	// FIXME: this method is suppose to be called before fillIntoGrid,
	// whereas 'indent' is supposed to be called after. It's not apparent
	// really which one should be used how. Need to decide to either use
	// one convention everywhere, or use more clear naming of methods.
	public GridElement spacing(int spacing)
	{
		this.spacing = spacing;
		return this;
	}
	
	public Label getIndentationLabel()
	{
		return indentationLabel;
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
		if (this.parent != null)
			this.parent.layout();
	}
	
	/** Add a children element that will be filled into
	 *  grid by a later call to fillIntoGrid.
	 */
	public void addChild(GridElement child)
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
		for (int i = 0; i < childElements.size(); ++i)
		{
			GridElement c = childElements.get(i);
			c.fillIntoGrid(parent);
			adjustCreatedChild(c, parent);
			
			if (i != childElements.size() - 1) {
				if (spacing != 0) {
					Label spacer = new Label(parent, SWT.NONE);
					GridData gd = new GridData();
					gd.horizontalSpan = DEFAULT_WIDTH;
					gd.heightHint = spacing;
					spacer.setLayoutData(gd);
				}
			}
		}
	}
	
	protected void adjustCreatedChild(GridElement element, Composite parent)
	{
		
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
	
	private boolean visible = true;
	private boolean indented = false;
	
	// Whether we're already linked to control, that is, fillIntoGrid was already
	// called.
	private boolean controlsCreated = false;
	
	private DisposeListener disposeListener;
	// While createImmediateContent is executing, index of the first control
	// we'd create in our parent. -1 otherwise.
	private int begin = -1;
	
	protected Label indentationLabel;
}
