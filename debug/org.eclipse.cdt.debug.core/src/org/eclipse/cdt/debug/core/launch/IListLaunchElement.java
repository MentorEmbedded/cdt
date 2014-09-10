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

/**
 * @since 7.6
 */
public interface IListLaunchElement extends ILaunchElement {

	/**
	 * Returns the upper limit of the number of children allowed by this element 
	 * or 0 is the number of children is unlimited.
	 */
	public int getUpperLimit();

	/**
	 * Returns the lower limit of the number of children allowed by this element.
	 */
	public int getLowerLimit();

	public void removeElement(ILaunchElement element);
	
	public void moveElementUp(ILaunchElement element);
	
	public void moveElementDown(ILaunchElement element);
}
