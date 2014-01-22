package org.eclipse.cdt.ui.grid;

/* String model can be also a composite model. Huh. */
public interface IPresentationModelString extends IPresentationModel {
	
	public interface ValueListener
	{
		public void value(String value);
	}
	
	/** Calls listener.value with the current string value,
	 * and arranges to call it in future for any changes.
	 */
	public void setValueListener(ValueListener listener);
	public void setValue(String value);
}

