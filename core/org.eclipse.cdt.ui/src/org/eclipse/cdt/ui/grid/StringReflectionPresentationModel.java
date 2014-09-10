package org.eclipse.cdt.ui.grid;

import java.lang.reflect.Method;

/**
 * @since 6.0
 */
public class StringReflectionPresentationModel extends StringPresentationModel {

	private Object under;
	
	private Method get;
	private Method set;
	
	
	public StringReflectionPresentationModel(String name, Object under, String getName, String setName) {
		super(name);
		this.under = under;
		
		try {
			get = under.getClass().getMethod(getName);
			set = under.getClass().getMethod(setName, String.class);
		} catch (Exception e) {
			throw new RuntimeException("Could not obtain get/set methodd", e);
		}
	}

	@Override
	protected String doGetValue() {
		
		try {
			return (String)get.invoke(under);
		} catch (Exception e) {
			throw new RuntimeException("Could not call specified get method", e);
		}
	}

	@Override
	protected void doSetValue(String value) {
		try {
			set.invoke(under, value);
		} catch (Exception e) {
			throw new RuntimeException("Could not call specified set method", e);
		}
	}

}
