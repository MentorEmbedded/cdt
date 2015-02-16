package org.eclipset.cdt.flatui;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

/* Complete description of box style, in different states. The
 * rule for Box.State.NORMAL must always be specified.
 * 
 * Some properties are described relative to font size, and
 * therefore are not known until style is bound to particular
 * context, via 
 * */
public class DeclaredBoxStyle {
	
	public void add(EnumSet<Box.State> states, StyleRule rule)
	{
		if (states.isEmpty()) {
			normalStateRule = rule;
		} else if (states.size() == 1) {
			if (states.contains(Box.State.ACTIVE))
				rules.put(Box.State.ACTIVE, rule);
			else if (states.contains(Box.State.HOVER)) 
				rules.put(Box.State.HOVER, rule);
			else
				throw new RuntimeException("Unknown box state");			
		} else {
			throw new RuntimeException("More than one state for a rule");
		}		
	}
	
	public ComputedBoxStyle getComputed(GC gc)
	{		
		// FIXME: property cache based on gc.getDevice and font metrics?
		// Also reset it when (in future) I add setFont method.
		if (computed == null) {
			FontMetrics fm = gc.getFontMetrics();

			computed = new ComputedBoxStyle(this, gc.getDevice(), fm.getHeight());
		}
		
		return computed;
	}
	
	public Integer getBorderWidth(EnumSet<Box.State> state) {
		Integer result = getRule(state).getBorderWidth();			
		if (result == null) {
			result = normalStateRule.getBorderWidth();
		}
		return result;
	}
	
	public Integer getBorderRadius(EnumSet<Box.State> state) {
		Integer result = getRule(state).getBorderRadius();
		if (result == null) {
			result = normalStateRule.getBorderRadius();
		}
		return result;
	}
	
	public Double getPaddingWidthEm(EnumSet<Box.State> state) {
		Double paddingWidthEm = getRule(state).getPaddingWidthEm();
		if (paddingWidthEm == null) {
			paddingWidthEm = normalStateRule.getPaddingWidthEm();
		}
		return paddingWidthEm;
	}
	
	public Double getPaddingHeightEm(EnumSet<Box.State> state) {
		Double r = getRule(state).getPaddingHeightEm();
		if (r == null) {
			r = normalStateRule.getPaddingHeightEm();
		}
		return r;
	}
	
	public Color getColor(EnumSet<Box.State> state) {
		Color c = getRule(state).getColor();
		if (c == null) {
			c = normalStateRule.getColor();
		}
		return c;
	}
	
	public Integer getColorIndex(EnumSet<Box.State> state) {
		Integer ci = getRule(state).getColorIndex();
		if (ci == null) {
			ci = normalStateRule.getColorIndex();
		}
		return ci;
	}
	
	public Color getBackgroundColor(EnumSet<Box.State> state) {
		Color c = getRule(state).getBackgroundColor();
		if (c == null) {
			c = normalStateRule.getBackgroundColor();
		}
		return c;
	}
	
	public Integer getBackgroundColorIndex(EnumSet<Box.State> state) {
		Integer ci = getRule(state).getBackgroundColorIndex();
		if (ci == null) {
			ci = normalStateRule.getBackgroundColorIndex();
		}
		return ci;
	}
	
	public Color getBorderColor(EnumSet<Box.State> state) {
		Color c = getRule(state).getBorderColor();
		if (c == null) {
			c = normalStateRule.getBorderColor();
		}
		return c;
	}
	
	public Integer getBorderColorIndex(EnumSet<Box.State> state) {
		Integer ci = getRule(state).getBorderColorIndex();
		if (ci == null) {
			ci = normalStateRule.getBorderColorIndex();
		}
		return ci;
	}
	
	private StyleRule getRule(EnumSet<Box.State> state) {
		
		if (state.contains(Box.State.ACTIVE) && rules.containsKey(Box.State.ACTIVE))
			return rules.get(Box.State.ACTIVE);
		
		if (state.contains(Box.State.HOVER) && rules.containsKey(Box.State.HOVER))
			return rules.get(Box.State.HOVER);
		
		return normalStateRule;
	}
	
	private Map<Box.State, StyleRule> rules = new HashMap<Box.State, StyleRule>();
	private StyleRule normalStateRule;
	
	private ComputedBoxStyle computed = null;


}
