package org.eclipse.cdt.ui.grid;

/* Factory that produces UI elements for IPresentationModel elements.
 * 
 * Whereas IPresentationModel will usually be composed of generic elements,
 * this class allows to customize visual rendering of these elements
 * according to specific situation.
 */
/**
 * @since 6.0
 */
public interface IViewElementFactory {
	
	public GridElement createElement(ISomePresentationModel model);

}
