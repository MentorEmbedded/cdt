package org.eclipset.cdt.flatui;

import java.util.EnumSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class InlineTextBox extends Box {
	
	private DeclaredBoxStyle declaredStyle;
	private EnumSet<State> states = EnumSet.noneOf(State.class);
		
	public InlineTextBox(String text, DeclaredBoxStyle style) {
		this.text = text;
		this.declaredStyle = style;		
		this.measurements = new Measurements();
	}

	@Override
	Measurements layout(GC gc) {
		
		ComputedBoxStyle style = this.declaredStyle.getComputed(gc);
		// FIXME: set the font.
			
		
		Point extent = gc.textExtent(text);
		
		extent.x += 2 * style.getBorderWidth(states) + 2 * style.getPaddingWidth(states);
		extent.y += 2 * style.getBorderWidth(states) + 2 * style.getPaddingHeight(states);
		
		measurements.width = extent.x;
		measurements.height = extent.y;
		
		// FIXME: fill in the baseline.

		return measurements;
	}

	@Override
	void paint(GC gc) {
		
		ComputedBoxStyle style = this.declaredStyle.getComputed(gc);
		
		gc.setAntialias(SWT.ON);
		
		
		
		// Boringly plain button
		// gc.setLineWidth(1);
		// gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
		// gc.drawRoundRectangle(0, 0, measurements.width - 2*border, measurements.height - 2*border , 10, 10);
		
		// No-border-pill
		
		
		//if (state == State.ACTIVE) {		
			//gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION));
		//	gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_CYAN));

		//} else {
			gc.setBackground(style.getBackgroundColor(states));
		//}
			
		
		int borderWidth = style.getBorderWidth(states);
		
		// SWT draws stroke at specified position + 0.5, with half of stroke on both sides.
		// So, to have start of stroke exactly at 0, we need to draw it certain offset.
		double offsetStartD = ((double)borderWidth)/2 - 0.5;
		// If offset is not integer, which can happen with even borderWidth, round up,
		// so that border eats into padding, but at least not clipped out.
		int offsetStart = (int)Math.ceil(offsetStartD);
		
		if (borderWidth > 0) {
			// Reduce fill dimensions to avoid filling showing up around stroke, which can happen either
			// if border width is odd, or on rounded corners.
			gc.fillRoundRectangle(1, 1, measurements.width - 2, measurements.height - 2, style.getBorderRadius(states), style.getBorderRadius(states));
		} else {
			gc.fillRoundRectangle(0, 0, measurements.width - 1, measurements.height - 1, style.getBorderRadius(states), style.getBorderRadius(states));
		}
					
		if (borderWidth != 0) {
			gc.setLineWidth(borderWidth);	
			gc.setForeground(style.getBorderColor(states));
			
			// For now, we want the outline to be inside the client area, 
			
		
			
			
			gc.drawRoundRectangle(offsetStart, offsetStart, measurements.width - borderWidth, measurements.height - borderWidth, style.getBorderRadius(states), style.getBorderRadius(states));
		}
		
		
		gc.setForeground(style.getColor(states));
				
		//gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_GREEN));
		//gc.setLineWidth(2);
		//gc.drawRoundRectangle(1, 1, measurements.width-2, measurements.height-2 , 0, 0);
		//gc.setLineWidth(1);
		//gc.drawRoundRectangle(1, 5, measurements.width-2, measurements.height-2 , 0, 0);
		// Add padding.
		gc.drawText(
				text, 
				style.getBorderWidth(states) + style.getPaddingWidth(states), 
				style.getBorderWidth(states) + style.getPaddingHeight(states), 
				true);
				
		
	}
	
	@Override
	Box setState(State state) {
		this.states.add(state);
		return this;
	}
	
	Box clearState(State state) {
		this.states.remove(state);
		return this;
	}
	
	private String text;	
	private Measurements measurements;

}
