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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.core.runtime.ListenerList;

/**
 * @since 7.4
 */
abstract public class AbstractLaunchElement implements ILaunchElement {

	/**
	 * This event notifies the element's children that an update may be required.
	 */
	abstract protected static class ChangeEvent implements IChangeEvent {

		final private AbstractLaunchElement fSource;
		
		public ChangeEvent(AbstractLaunchElement source) {
			super();
			fSource = source;
		}

		public ILaunchElement getSource() {
			return fSource;
		}
	}

	private String fId;
	private String fName;
	private String fDescription;
	private String fErrorMessage = null;
	private ILaunchElement fParent;
	private boolean fEnabled = true;
	private List<ILaunchElement> fChildren = new ArrayList<ILaunchElement>();
	private ListenerList fChangeListeners = new ListenerList();

	private boolean fIsInitializing = false;

	public AbstractLaunchElement(ILaunchElement parent, String id, String name, String description) {
		this(parent, id, name, description, true);
	}

	public AbstractLaunchElement(ILaunchElement parent, String id, String name, String description, boolean enabled) {
		super();
		fParent = parent;
		fId = id;
		fName = name;
		fDescription = description;
		fEnabled = enabled;
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
	public void initialiazeFrom(Map<String, Object> attributes) {
		fIsInitializing = true;
		setErrorMessage(null);
		removeAllChildren();
		createChildren(attributes);
		doInitializeFrom(attributes);
		for (ILaunchElement el : getChildren()) {
			el.initialiazeFrom(attributes);
		}
		fIsInitializing = false;
	}

	@Override
	public void performApply(Map<String, Object> attributes) {
		for (ILaunchElement el : getChildren()) {
			el.performApply(attributes);
		}
		doPerformApply(attributes);
	}

	@Override
	public void setDefaults(Map<String, Object> attributes) {
		for (ILaunchElement el : getChildren()) {
			el.setDefaults(attributes);
		}
		doSetDefaults(attributes);
	}

	@Override
	public boolean isValid() {
		if (!isEnabled())
			return true;
		setErrorMessage(null);
		for (ILaunchElement el : getChildren()) {
			if (el instanceof AbstractLaunchElement) {
				((AbstractLaunchElement)el).setErrorMessage(null);
			}
		}
		if (!isContentValid()) {
			return false;
		}
		for (ILaunchElement el : getChildren()) {
			if (!el.isValid()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		if (!isEnabled())
			return null;
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

	@SuppressWarnings("unchecked")
	@Override
	public<V> V findChild(Class<V> childClass) {
		if (this.getClass().equals(childClass)) {
			return (V)this;
		}
		for (ILaunchElement el : getChildren()) {
			V child = el.findChild(childClass);
			if (child != null) {
				return child;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public<V> V[] getChildren(Class<V> childClass) {
		List<V> list = new ArrayList<V>();
		for (ILaunchElement child : getChildren()) {
			if (child.getClass().equals(childClass)) {
				list.add((V)child);
			}
		}
    	V[] v = (V[])Array.newInstance(childClass, list.size());
		return list.toArray(v);
	}

	@Override
	public int getChildIndex(ILaunchElement child) {
		return Arrays.asList(getChildren()).indexOf(child);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V findAncestor(Class<V> ancestorClass) {
		if (this.getClass().equals(ancestorClass)) {
			return (V)this;
		}
		return (getParent() != null) ? getParent().findAncestor(ancestorClass) : null;
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

	protected void createChildren(Map<String, Object> attributes) {
		removeAllChildren();
		doCreateChildren(attributes);
		for (ILaunchElement child : getChildren()) {
			if (child instanceof AbstractLaunchElement) {
				((AbstractLaunchElement)child).createChildren(attributes);
			}
		}
	}

	protected void elementChanged(int details) {
		elementChanged(this, details);
	}
	
	protected boolean isInitializing() {
		return fIsInitializing;
	}

	protected void elementAdded(ILaunchElement element, int details) {
		if (isInitializing()) {
			return;
		}
		for (Object o : fChangeListeners.getListeners()) {
			((IChangeListener)o).elementAdded(element, details);
		}
	}

	protected void elementRemoved(ILaunchElement element) {
		if (isInitializing()) {
			return;
		}
		for (Object o : fChangeListeners.getListeners()) {
			((IChangeListener)o).elementRemoved(element);
		}
	}

	protected void elementChanged(ILaunchElement element, int details) {
		if (isInitializing()) {
			return;
		}
		for (Object o : fChangeListeners.getListeners()) {
			((IChangeListener)o).elementChanged(element, details);
		}
	}

	@Override
	public boolean isEnabled() {
		return fEnabled;
	}

	@Override
	public boolean setEnabled(boolean enabled) {
		return fEnabled = enabled;
	}

	@Override
	public void update(IChangeEvent event) {
		for (ILaunchElement element : getChildren()) {
			element.update(event);
		}
	}

	@Override
	public boolean canRemove() {
		return false;
	}

	protected<V> V getAttribute(Map<String, Object> attributes, String name, V defaultValue) {
		return CDebugUtils.getAttribute(attributes, name, defaultValue);
	}

	abstract protected void doCreateChildren(Map<String, Object> attributes);
	
	abstract protected void doInitializeFrom(Map<String, Object> attributes);
	
	abstract protected void doPerformApply(Map<String, Object> attributes);
	
	abstract protected void doSetDefaults(Map<String, Object> attributes);
	
	abstract protected boolean isContentValid();
}
