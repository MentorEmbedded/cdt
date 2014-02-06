package org.eclipse.cdt.ui.grid;

public class StringPresentationModel extends PresentationModel implements IStringPresentationModel {

	public StringPresentationModel(String name)
	{
		super(name);
		this.value = "Test";
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
		notifyListeners(PresentationModel.CHANGED, this);
	}
	
	private String value;
}
