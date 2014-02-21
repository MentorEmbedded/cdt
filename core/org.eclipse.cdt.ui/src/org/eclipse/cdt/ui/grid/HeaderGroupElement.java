package org.eclipse.cdt.ui.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @since 5.7
 */
public class HeaderGroupElement extends GridElement {
	
	public HeaderGroupElement(ICompositePresentationModel model, IViewElementFactory factory)
	{
		this.model = model;
		this.factory = factory;
	}
	
	
	@Override
	public void createImmediateContent(Composite parent) {
		
		GridLayout layout = (GridLayout)parent.getLayout();
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(model.getName());
		
		Font f = l.getFont();
		FontData[] fd = f.getFontData();
		fd[0].setStyle(SWT.BOLD);
		Font bold = new Font(f.getDevice(), fd);
		l.setFont(bold);
		// FIXME: need to release the font.
		
		
		GridData gd = new GridData();
		gd.horizontalSpan = layout.numColumns;
		l.setLayoutData(gd);
						
		for (ISomePresentationModel cm: model.getChildren()) {
			
			GridElement element = factory.createElement(cm);
			
			element.fillIntoGrid(parent);			
		}
		
		
		// TODO Auto-generated method stub

	}
	
	ICompositePresentationModel model;
	IViewElementFactory factory;

}
