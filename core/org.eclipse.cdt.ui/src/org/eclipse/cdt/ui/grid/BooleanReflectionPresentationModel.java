package org.eclipse.cdt.ui.grid;

import java.lang.reflect.Method;

/**
 * @since 5.7
 */
public class BooleanReflectionPresentationModel extends BooleanPresentationModel {

	private Object under;
	
	private Method get;
	private Method set;
	
	
	public BooleanReflectionPresentationModel(String name, Object under, String getName, String setName) {
		super(name);
		this.under = under;
		
		try {
			get = under.getClass().getMethod(getName);
			set = under.getClass().getMethod(setName, boolean.class);
		} catch (Exception e) {
			throw new RuntimeException("Could not obtain get/set methodd", e);
		}
	}

	@Override
	protected boolean doGetValue() {
		
		try {
			Object r = get.invoke(under);
			return ((Boolean)r).booleanValue();
		} catch (Exception e) {
			throw new RuntimeException("Could not call specified get method", e);
		}
	}

	@Override
	protected void doSetValue(boolean value) {
		try {
			set.invoke(under, new Boolean(value));
		} catch (Exception e) {
			throw new RuntimeException("Could not call specified set method", e);
		}
	}

}
