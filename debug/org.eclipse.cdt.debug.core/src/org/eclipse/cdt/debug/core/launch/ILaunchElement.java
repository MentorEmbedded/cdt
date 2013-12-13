/*******************************************************************************
 * Copyright (c) 2013 Mentor Graphics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mentor Graphics - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.debug.core.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * Represents a logically related group of launch configuration attributes
 * which is meant to be presented by a separate UI element in the launch 
 * configuration dialog.
 * <p>
 * The element model of the launch configuration of a certain type is 
 * structured as a tree with <code>ILaunchElement</code> objects as nodes. 
 * The launch configuration structure is predefined for each launch configuration 
 * type. 
 * </p>
 * 
 * @since 7.4
 */
public interface ILaunchElement {

	final public static int ADD_DETAIL_ACTIVATE = 0x1; 
	
	final public static int CHANGE_DETAIL_STATE = 0x1; 
	final public static int CHANGE_DETAIL_CONTENT = 0x2; 
	
	public interface IChangeListener {

		public void elementAdded(ILaunchElement element, int details);
		
		public void elementRemoved(ILaunchElement element);
		
		public void elementChanged(ILaunchElement element, int details);
	}
	
	public interface IChangeEvent {
		ILaunchElement getSource();
	}

	/**
	 * The unique identifier that is used as a part of the attribute key
	 * for node's attributes.  
	 */
	public String getId();
	
	public String getName();

	public String getDescription();
	
	public ILaunchElement getParent();
	
	public void initialiazeFrom(ILaunchConfiguration config);
	
	public void performApply(ILaunchConfigurationWorkingCopy config);
	
	public void setDefaults(ILaunchConfigurationWorkingCopy config);
	
	public boolean isValid(ILaunchConfiguration config);
	
	public String getErrorMessage();	
	
	public ILaunchElement[] getChildren();

	public void addChildren(ILaunchElement[] children);
	
	public void removeAllChildren();
	
	public void insertChild(int index, ILaunchElement child);
	
	public void insertChild(ILaunchElement element, ILaunchElement child);
	
	public void removeChild(ILaunchElement child);
	
	public ILaunchElement findChild(String id);

	public<V> V findChild(Class<V> childClass);

	public int getChildIndex(ILaunchElement child);

	public void dispose();
	
	public void addChangeListener(IChangeListener listener);
	
	public void removeChangeListener(IChangeListener listener);
	
	public boolean isEnabled();
	
	public boolean setEnabled(boolean enabled);
	
	public void update(IChangeEvent event);
	
	public boolean canRemove();
}
