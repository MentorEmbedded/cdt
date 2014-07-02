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
	
	// Specify whether first element must be indented (default) or not.
	// When the first element is not indented, its left-top label is
	// changed to be equal to the name of this group. This effect
	// is to be used sparingly.
	public void setIndentFirst(boolean indentFirst)
	{
		this.indentFirst = indentFirst;
		// FIXME: this must be able to adjust already created things.
	}
	
	@Override
	public void addChild(GridElement child) {
		
		super.addChild(child);
		
		if (getChildElements().size() == 1) {
			child.setIndented(indentFirst);		
		}
		else
			child.setIndented(true);
	}
	
	@Override
	protected void createImmediateContent(Composite parent) {
		
	}
	
	@Override
	protected void adjustChildren(Composite parent) {
		if (getChildElements().size() != 0)
		{
			// Decorate first child with our own name, in bold.
			// This means that if the first child is removed, this label will
			// also be gone. If necessary, derived classes can handle removal
			// specifically.
			GridElement child = getChildElements().get(0);
			Label topLabel;
			if (indentFirst)
				topLabel = child.getIndentationLabel();
			else
				topLabel = (Label) child.getChildControls().get(0);
			topLabel.setText(name);
			topLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		}
	}
		
	private String name;

	private boolean indentFirst = true;
}
