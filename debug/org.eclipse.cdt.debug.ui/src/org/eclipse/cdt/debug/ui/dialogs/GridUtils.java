package org.eclipse.cdt.debug.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

/**
 * @since 7.4
 */
public class GridUtils {
	/** Return GridData that is set on 'c', creating one if necessary.
	 * 
	 * Caller should make sure that no layout data of another type is
	 * set on 'c' yet.
	 */
	public static GridData getGridLayoutData(Control c) {
		Object ld = c.getLayoutData();
		GridData gd;
		if (ld == null) {
			gd = new GridData();
			c.setLayoutData(gd);
		}
		else {
			assert ld instanceof GridData;
			gd = (GridData)ld;
		}
		return gd;
	}

	/**
	 * Add a horizontal separator to a grid layout.
	 * 
	 * @param c the composite to which the separator is added.
	 */
	public static Label addHorizontalSeparatorToGrid(Composite c, int span) {
		Label l = new Label(c, SWT.SEPARATOR | SWT.HORIZONTAL);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, span, 1));
		return l;
	}

	public static Composite createBar(Composite parent, int verticalSpan) {		
		Composite p1 = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, verticalSpan);
		data.widthHint = 6;
		// FIXME: for unknown reasons, without this pushed the height to be more that 2 text rows.
		data.heightHint = 1;
		p1.setLayoutData(data);
		p1.setBackground(p1.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		return p1;
	}

	public static void fillIntoGrid(Control control, Composite parent) {
		GridLayout layout = (GridLayout)parent.getLayout();
		int numColumns = layout.numColumns;
		Object data = control.getLayoutData();
		GridData gridData = (data instanceof GridData) ? gridData = (GridData)data : new GridData();
		gridData.horizontalSpan = numColumns;
		control.setLayoutData(gridData);
	}

	public static Control createVerticalSpacer(Composite parent, int numlines) {
		Label lbl = new Label(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		Layout layout = parent.getLayout();
		if(layout instanceof GridLayout) {
			gd.horizontalSpan = ((GridLayout)parent.getLayout()).numColumns;
		}
		gd.heightHint = numlines;
		lbl.setLayoutData(gd);
		return lbl;
	}
	
	public static void createHorizontalSpacer(Composite comp, int numlines) {
		Label lbl = new Label(comp, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numlines;
		lbl.setLayoutData(gd);
	}
}
