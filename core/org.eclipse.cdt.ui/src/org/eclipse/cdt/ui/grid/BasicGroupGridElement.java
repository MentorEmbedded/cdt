package org.eclipse.cdt.ui.grid;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;

/** GridElement that combines several leaf grid elements
 *  with various presentation options.
 *  @since 5.7
 */
public class BasicGroupGridElement extends GridElement {
	
	public BasicGroupGridElement(String name)
	{
		this.name = name;
	}
	
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
		
		Label topLabel = (Label)getChildElements().get(0).getChildControls().get(0);
		
		for (int i = (indentFirst ? 0 : 1); i < getChildElements().size(); ++i) {
			
			GridElement child = getChildElements().get(i);
					
			Control label = child.getChildControls().get(0);
			Control content = child.getChildControls().get(2);
			
			Label newLabel = new Label(parent, SWT.NONE);
			newLabel.moveAbove(label);
			
			label.moveAbove(content);
			
			CDTUITools.getGridLayoutData(content).horizontalSpan = 1;
			
			if (i == 0)
				topLabel = newLabel;
		}
		
		topLabel.setText(name);
		topLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
	}
	
	private String name;

	private boolean indentFirst = false;
}
