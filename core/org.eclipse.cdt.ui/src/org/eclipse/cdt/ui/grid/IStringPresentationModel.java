package org.eclipse.cdt.ui.grid;

/* String model can be also a composite model. Huh. */
/**
 * @since 5.7
 */
public interface IStringPresentationModel extends IPresentationModel {
	
	public String getValue();
	public void setValue(String value);
}

