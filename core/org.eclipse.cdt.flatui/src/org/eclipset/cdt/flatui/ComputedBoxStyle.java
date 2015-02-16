package org.eclipset.cdt.flatui;

import java.util.EnumSet;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

public class ComputedBoxStyle {
	
	public ComputedBoxStyle(DeclaredBoxStyle declared, Device device, int em)
	{
		this.declared = declared;
		this.device = device;
		this.em = em;
	}

	public int getBorderWidth(EnumSet<Box.State> state) {
		Integer r = declared.getBorderWidth(state);
		if (r == null)
			return 0;
		return r;
	}

	public Integer getBorderRadius(EnumSet<Box.State> state) {
		Integer r = declared.getBorderRadius(state);
		if (r == null)
			return 0;
		return r;
	}

	public int getPaddingWidth(EnumSet<Box.State> state) {
		Double w = declared.getPaddingWidthEm(state);
		if (w == null)
			w = 0.5;
		
		return (int)(w * em);
	}
	
	public int getPaddingHeight(EnumSet<Box.State> state) {
		Double h = declared.getPaddingHeightEm(state);
		if (h == null)
			h = 0.5;
		
		return (int)(h * em);
	}
	
	public Color getColor(EnumSet<Box.State> state) {
		Color c = declared.getColor(state);
		if (c != null)
			return c;
		
		Integer ci = declared.getColorIndex(state);
		if (ci != null)
			return device.getSystemColor(ci);
		
		return null;
	}
	
	public Color getBackgroundColor(EnumSet<Box.State> state) {
		Color c = declared.getBackgroundColor(state);
		if (c != null)
			return c;
		
		Integer ci = declared.getBackgroundColorIndex(state);
		if (ci != null)
			return device.getSystemColor(ci);
		
		return null; 
	}
	
	public Color getBorderColor(EnumSet<Box.State> state) {
		Color c = declared.getBorderColor(state);
		if (c != null) {
			return c;
		}
		
		Integer ci = declared.getBorderColorIndex(state);
		if (ci != null)
			return device.getSystemColor(ci);
		
		return null;
	}
	
	DeclaredBoxStyle declared;
	Device device;
	int em;

}
