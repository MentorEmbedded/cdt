package org.eclipse.cdt.ui.grid;

/**
 * @since 5.7
 */
public abstract class BooleanPresentationModel extends PresentationModel implements IBooleanPresentationModel {

	public BooleanPresentationModel(String name)
	{
		super(name);
	}
	
	@Override
	public boolean getValue() {
		return doGetValue();
	}
	
	protected abstract boolean doGetValue();
	
	@Override
	public void setValue(boolean value) {
		doSetValue(value);
		notifyListeners(PresentationModel.VALUE_CHANGED, this);
	}
	
	protected abstract void doSetValue(boolean value);
}
