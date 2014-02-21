package org.eclipse.cdt.ui.grid;

/* String model can be also a composite model. Huh. */
/**
 * @since 5.7
 */
public interface IBooleanPresentationModel extends IPresentationModel {
	
	public boolean getValue();
	public void setValue(boolean value);
}

