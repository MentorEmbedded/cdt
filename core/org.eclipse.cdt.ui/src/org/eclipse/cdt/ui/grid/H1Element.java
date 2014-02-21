package org.eclipse.cdt.ui.grid;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.cdt.ui.CDTUITools;

/**
 * @since 5.7
 */
public class H1Element extends GridElement {

	private static boolean fontRegistryInitialized = false;
	private static FontRegistry demandFontRegistry()
	{
		if (!fontRegistryInitialized) {
			FontData fd[] = new FontData[]{StringConverter.asFontData("Sans-bold-14")};
			JFaceResources.getFontRegistry().put("h1", fd);
		}
		
		return JFaceResources.getFontRegistry();
	}
	

	public H1Element(String text)
	{
		this.text = text;
	}
	
	@Override
	public void createImmediateContent(Composite parent) {
		
		Label spacer = new Label(parent, SWT.NONE);
		CDTUITools.getGridLayoutData(spacer).horizontalSpan = GridElement.DEFAULT_WIDTH;
		CDTUITools.getGridLayoutData(spacer).heightHint = 14/2;
		
		Label l = new Label(parent, SWT.NONE);
		//l.setFont(demandFontRegistry().get("h1"));
		l.setFont(JFaceResources.getHeaderFont());
		
		

		l.setText(text);
		CDTUITools.grabAllWidth(l);
		CDTUITools.getGridLayoutData(l).horizontalSpan = GridElement.DEFAULT_WIDTH;
		
		// TODO Auto-generated method stub

	}
	
	private String text;

}
