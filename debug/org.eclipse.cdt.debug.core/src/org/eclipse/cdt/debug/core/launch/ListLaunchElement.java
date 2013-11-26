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
 * @since 7.4
 */
abstract public class ListLaunchElement extends AbstractLaunchElement implements IListLaunchElement {

	public ListLaunchElement(ILaunchElement parent, String id, String name, String description) {
		super(parent, id, name, description);
	}

	@Override
	protected void doCreateChildren(ILaunchConfiguration config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doPerformApply(ILaunchConfigurationWorkingCopy config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doSetDefaults(ILaunchConfigurationWorkingCopy config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isContentValid(ILaunchConfiguration config) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUpperLimit() {
		return 0;
	}

	@Override
	public int getLowerLimit() {
		return 0;
	}

	@Override
	public void addNewElement() {
	}

	@Override
	public void removeElement(ILaunchElement element) {
		removeChild(element);
	}

	@Override
	public void moveElementUp(ILaunchElement element) {
		int index = getChildIndex(element);
		if (index > 0) {
			doRemoveChild(element);
			doInsertChild(--index, element);
		}
		elementChanged(getParent(), CHANGE_DETAIL_CONTENT);
	}

	@Override
	public void moveElementDown(ILaunchElement element) {
		int index = getChildIndex(element);
		if (index >= 0 && index + 1 < getChildren().length) {
			doRemoveChild(element);
			doInsertChild(++index, element);
		}
		elementChanged(getParent(), CHANGE_DETAIL_CONTENT);
	}
}
