package org.eclipset.cdt.flatui;

import org.eclipse.swt.graphics.Color;

/* Set of style attributes.
 * 
 * We have a fairly small set of supported attributes, so there's a setter and getter for each,
 * as opposed to more flexible, but slower, approach using attribute names. 
 */
public class StyleRule {
	
	public StyleRule setBorderWidth(int w) {
		borderWidth = w;
		return this;
	}
	
	public StyleRule setBorderRadius(int r) {
		borderRadius = r;
		return this;
	}
		
	public StyleRule setPaddingWidthEm(Double padding) {
		assert padding != null;
		paddingWidthEm = padding;
		return this;
	}
	
	public StyleRule setPaddingHeightEm(Double padding) {
		assert padding != null;
		paddingHeightEm = padding;
		return this;
	}
	
	public StyleRule setColor(int systemColor) {
		this.colorIndex = systemColor;
		this.color = null;
		return this;
	}
	
	public StyleRule setColor(Color color) {
		this.color = color;
		this.colorIndex = null;
		return this;
	}
	
	public StyleRule setBackgroundColor(int color) {
		this.backgroundColorIndex = color;
		this.backgroundColor = null;
		return this;
	}
	
	public StyleRule setBackgroundColor(Color color) {
		this.backgroundColor = color;
		this.backgroundColorIndex = -1;
		return this;
	}
	
	public StyleRule setBorderColor(int color) {
		this.borderColorIndex = color;
		this.borderColor = null;
		return this;
	}
	
	public StyleRule setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		this.borderColorIndex = -1;
		return this;
	}
	
	Integer getBorderWidth() {
		return borderWidth;
	}
	
	Integer getBorderRadius() {
		return borderRadius;
	}
	
	Double getPaddingWidthEm() {
		return paddingWidthEm;
	}
	
	Double getPaddingHeightEm() {
		return paddingHeightEm;
	}
		
	Integer getColorIndex() {
		return colorIndex;
	}
	
	Color getColor() {
		return color;
	}
	
	Integer getBackgroundColorIndex() {
		return backgroundColorIndex;
	}
	
	Color getBackgroundColor() {
		return backgroundColor;
	}
	
	Integer getBorderColorIndex() {
		return borderColorIndex;
	}
	
	Color getBorderColor() {
		return borderColor;
	}

	private Integer borderWidth;
	private Integer borderRadius;

	private Double paddingWidthEm;
	private Double paddingHeightEm;
	
	private Integer colorIndex;
	private Color color;
	
	private Integer backgroundColorIndex;
	private Color backgroundColor;
	
	private Integer borderColorIndex;
	private Color borderColor;
}
