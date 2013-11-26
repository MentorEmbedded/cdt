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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 7.4
 */
abstract public class AbstractLaunchElement implements ILaunchElement {

	private String fId;
	private String fName;
	private String fDescription;
	private String fErrorMessage = null;
	private ILaunchElement fParent;
	private List<ILaunchElement> fChildren = new ArrayList<ILaunchElement>();
	private ListenerList fChangeListeners = new ListenerList();

	private boolean fIsInitializing = false;

	public AbstractLaunchElement(ILaunchElement parent, String id, String name, String description) {
		super();
		fParent = parent;
		fId = id;
		fName = name;
		fDescription = description;
	}

	@Override
	public String getId() {
		return fId;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public String getDescription() {
		return fDescription;
	}

	@Override
	public ILaunchElement getParent() {
		return fParent;
	}

	@Override
	public void initialiazeFrom(ILaunchConfiguration config) {
		fIsInitializing = true;
		setErrorMessage(null);
		removeAllChildren();
		createChildren(config);
		doInitializeFrom(config);
		for (ILaunchElement el : getChildren()) {
			el.initialiazeFrom(config);
		}
		fIsInitializing = false;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		for (ILaunchElement el : getChildren()) {
			el.performApply(config);
		}
		doPerformApply(config);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		for (ILaunchElement el : getChildren()) {
			el.setDefaults(config);
		}
		doSetDefaults(config);
	}

	@Override
	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		for (ILaunchElement el : getChildren()) {
			if (el instanceof AbstractLaunchElement) {
				((AbstractLaunchElement)el).setErrorMessage(null);
			}
		}
		if (!isContentValid(config)) {
			return false;
		}
		for (ILaunchElement el : getChildren()) {
			if (!el.isValid(config)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		if (fErrorMessage != null) {
			return fErrorMessage;
		}
		for (ILaunchElement el : getChildren()) {
			String errorMessage = el.getErrorMessage();
			if (errorMessage != null) {
				return errorMessage;
			}
		}
		return null;
	}
	
	protected String getInternalErrorMessage() {
		return fErrorMessage;
	}

	@Override
	public void dispose() {
		fChangeListeners.clear();
		for (ILaunchElement el : getChildren()) {
			el.dispose();
		}
	}

	@Override
	public ILaunchElement[] getChildren() {
		return fChildren.toArray(new ILaunchElement[fChildren.size()]);
	}

	@Override
	public void addChildren(ILaunchElement[] children) {
		fChildren.addAll(Arrays.asList(children));
		for (ILaunchElement child : children) {
			for (Object o : fChangeListeners.getListeners()) {
				IChangeListener listener = (IChangeListener)o;
				child.addChangeListener(listener);
			}
			elementAdded(child, 0);
		}
	}

	@Override
	public void removeAllChildren() {
		List<ILaunchElement> list = new ArrayList<ILaunchElement>(fChildren);
		fChildren.clear();
		for (ILaunchElement child : list) {
			for (Object o : fChangeListeners.getListeners()) {
				IChangeListener listener = (IChangeListener)o;
				child.removeChangeListener(listener);
			}
			elementRemoved(child);
		}
		for (ILaunchElement el : list) {
			el.dispose();
		}
	}

	@Override
	public void insertChild(int index, ILaunchElement child) {
		doInsertChild(index, child);
		elementAdded(child, 0);
	}

	@Override
	public void insertChild(ILaunchElement element, ILaunchElement child) {
		int index = fChildren.indexOf(element);
		insertChild(index, child);
	}

	protected void doInsertChild(int index, ILaunchElement child) {
		if (index >= 0 && index < fChildren.size()) {
			fChildren.add(index, child);
		}
		else {
			// TODO: not sure what to do in this case
			fChildren.add(child);
		}
		for (Object o : fChangeListeners.getListeners()) {
			IChangeListener listener = (IChangeListener)o;
			child.addChangeListener(listener);
		}
	}

	@Override
	public void removeChild(ILaunchElement child) {
		doRemoveChild(child);;
		elementRemoved(child);
	}

	protected void doRemoveChild(ILaunchElement child) {
		fChildren.remove(child);
		for (Object o : fChangeListeners.getListeners()) {
			IChangeListener listener = (IChangeListener)o;
			child.removeChangeListener(listener);
		}
	}

	@Override
	public ILaunchElement findChild(String id) {
		if (getId().equals(id)) {
			return this;
		}
		for (ILaunchElement el : getChildren()) {
			ILaunchElement child = el.findChild(id);
			if (child != null) {
				return child;
			}
		}
		return null;
	}

	@Override
	public int getChildIndex(ILaunchElement child) {
		return Arrays.asList(getChildren()).indexOf(child);
	}

	@Override
	public void addChangeListener(IChangeListener listener) {
		fChangeListeners.add(listener);
	}

	@Override
	public void removeChangeListener(IChangeListener listener) {
		fChangeListeners.remove(listener);
	}

	protected void setErrorMessage(String message) {
		fErrorMessage = message;
	}

	protected void createChildren(ILaunchConfiguration config) {
		removeAllChildren();
		doCreateChildren(config);
		for (ILaunchElement child : getChildren()) {
			if (child instanceof AbstractLaunchElement) {
				((AbstractLaunchElement)child).createChildren(config);
			}
		}
	}

	protected void elementChanged(int details) {
		if (isInitializing()) {
			return;
		}
		elementChanged(this, details);
	}
	
	protected boolean isInitializing() {
		return fIsInitializing;
	}

	protected void elementAdded(ILaunchElement element, int details) {
		for (Object o : fChangeListeners.getListeners()) {
			((IChangeListener)o).elementAdded(element, details);
		}
	}

	protected void elementRemoved(ILaunchElement element) {
		for (Object o : fChangeListeners.getListeners()) {
			((IChangeListener)o).elementRemoved(element);
		}
	}

	protected void elementChanged(ILaunchElement element, int details) {
		for (Object o : fChangeListeners.getListeners()) {
			((IChangeListener)o).elementChanged(element, details);
		}
	}

	abstract protected void doCreateChildren(ILaunchConfiguration config);
	
	abstract protected void doInitializeFrom(ILaunchConfiguration config);
	
	abstract protected void doPerformApply(ILaunchConfigurationWorkingCopy config);
	
	abstract protected void doSetDefaults(ILaunchConfigurationWorkingCopy config);
	
	abstract protected boolean isContentValid(ILaunchConfiguration config);
}
