package org.eclipse.cdt.ui.grid;

import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;

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
	
	// Make the first row of the first element indented.
	// If this is not called, will change the label of
	// the first row to be 'name'.
	public void indentFirst()
	{
		this.indentFirst = true;
	}
	
	@Override
	protected void createImmediateContent(Composite parent) {
	}
	
	@Override
	protected void adjustChildren(Composite parent) {
		
		if (getChildElements().isEmpty())
			return;
		
		// We want to indent all the content, so that first column,
		// normally taken up by labels, is empty.
		
		
		
		Label topLabel = null;
		
		for (int i = 0; i < getChildElements().size(); ++i) {
			GridElement child = getChildElements().get(i);
			Label l = child.indent();
			if (i == 0)
				topLabel = l;
		}
			
		
		/*
		for (int i = (indentFirst ? 0 : 1); i < getChildElements().size(); ++i) {
			
			GridElement child = getChildElements().get(i);

			List<Control> firstRow = child.getFirstRow();
			Control label = firstRow.get(0);
			Control content = firstRow.get(2);
			
			Label newLabel = new Label(parent, SWT.NONE);
			child.addChildControlFromOutside(newLabel);
			newLabel.moveAbove(label);
			
			label.moveAbove(content);
			
			CDTUITools.getGridLayoutData(content).horizontalSpan = 1;
			
			if (i == 0)
				topLabel = newLabel;
		}*/
		
		
		// FIXME: add the above inside first element? Or self?
		
		topLabel.setText(name);
		topLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
	}
	
	private String name;

	private boolean indentFirst = false;
}
