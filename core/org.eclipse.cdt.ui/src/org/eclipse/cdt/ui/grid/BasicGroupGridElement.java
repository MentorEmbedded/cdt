package org.eclipse.cdt.ui.grid;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/** GridElement that combines several other GridElements,
 *  by indenting them and putting a bold label on the first
 *  row.
 *  @since 5.7
 */
public class BasicGroupGridElement extends GridElement {
	
	public BasicGroupGridElement(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	// Specify whether first element must be indented (default) or not.
	// When the first element is not indented, its left-top label is
	// changed to be equal to the name of this group. This effect
	// is to be used sparingly.
	// FIXME: this must be able to adjust already created things.
	public void setIndentFirst(boolean indentFirst)
	{
		this.indentFirst = indentFirst;
	}
	
	@Override
	public void addChild(GridElement child) {
		
		boolean firstChild = getChildElements().size() == 0;
		
		super.addChild(child);
		
		if (firstChild) {
			child.setIndented(indentFirst);
			
			// Decorate first child with our own name, in bold.
			// This means that if the first child is removed, this label will
			// also be gone. If necessary, derived classes can handle removal
			// specifically.
			Label topLabel = child.getTopLeftLabel();
			topLabel.setText(name);
			topLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));			
		}
		else
			child.setIndented(true);
	}
	
	@Override
	protected void createImmediateContent(Composite parent) {
		
	}

	private String name;

	private boolean indentFirst = true;
}
