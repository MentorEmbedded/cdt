package org.eclipse.cdt.ui.grid;

public class StringPresentationModel implements IPresentationModelString {

	public StringPresentationModel(String name)
	{
		this.name = name;
		this.value = "Test";
	}
		
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setValueListener(ValueListener listener) {
		this.listener = listener;
		listener.value(value);
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
		listener.value(value);
	}
	
	private String name;
	private String value;
	private ValueListener listener;

}
