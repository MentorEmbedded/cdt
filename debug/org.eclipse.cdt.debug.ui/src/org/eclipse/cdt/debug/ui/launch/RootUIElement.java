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

package org.eclipse.cdt.debug.ui.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement.IChangeListener;
import org.eclipse.cdt.debug.core.launch.IListLaunchElement;
import org.eclipse.cdt.debug.ui.dialogs.Breadcrumbs;
import org.eclipse.cdt.debug.ui.dialogs.Breadcrumbs.ILinkListener;
import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @since 7.4
 */
abstract public class RootUIElement implements ILinkListener, IChangeListener {

	@Override
	public void elementAdded(ILaunchElement element, int details) {
		if (isInitializing())
			return;
		ILaunchElement parent = element.getParent();
		if (parent instanceof IListLaunchElement) {
			parent = parent.getParent();
		}
		refresh(parent);
	}

	@Override
	public void elementRemoved(ILaunchElement element) {
		if (isInitializing())
			return;
		ILaunchElement parent = element.getParent();
		if (parent instanceof IListLaunchElement) {
			parent = parent.getParent();
		}
		refresh(parent);
	}

	@Override
	public void elementChanged(ILaunchElement element, int details) {
		if (isInitializing())
			return;
		if ((details & ILaunchElement.CHANGE_DETAIL_CONTENT) != 0) { 
			refresh(element);
		}
	}

	private IUIElementFactory fUIElementFactory;

	private Breadcrumbs fBreadcrumbs;
	private Composite fContent;
	
	private ILaunchElement fTopElement;
	
	private AbstractUIElement fCurrentUIElement;

	private Map<String, Map<String, String>> fElementIds = new HashMap<String, Map<String,String>>();

	private boolean fInitializing = false;

	public RootUIElement() {
		super();
	}

	public void dispose() {
		if (fContent != null) {
			fContent.dispose();
			fContent = null;
		}
		if (fTopElement != null) {
			fTopElement.dispose();
		}
		fElementIds.clear();
	}

	private void setTopElement(ILaunchElement topElement) {
		if (fTopElement != null) {
			fTopElement.removeChangeListener(this);
			fTopElement.dispose();
		}
		fTopElement = topElement;
		fTopElement.addChangeListener(this);
	}

	public void createControl(Composite parent) {
		Composite base = new Composite(parent, SWT.NONE);
		base.setLayout(new GridLayout());
		base.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		fBreadcrumbs = new Breadcrumbs(base, SWT.NONE, this);

		GridUtils.addHorizontalSeparatorToGrid(base, 1);
		
		fContent = new Composite(base, SWT.NONE);
		fContent.setLayout(new GridLayout(5, false));
		fContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	public void initializeFrom(Map<String, Object> attributes) throws CoreException {
		fInitializing = true;
		fUIElementFactory = createUIElementFactory(attributes);
		ILaunchElement topElement = createTopElement(attributes);
		setTopElement(topElement);
		if (topElement != null) {
			topElement.initialiazeFrom(attributes);
		}
		fInitializing = false;
	}

	public void performApply(Map<String, Object> attributes) {
		if (isInitializing())
			return;
		// VP: I would rather this block did not exist. We should have
		// up-to-date model at all times.
		if (getCurrentUIElement() != null) {
			getCurrentUIElement().save();
		}
		if (getTopElement() != null) {
			getTopElement().performApply(attributes);
		}
	}

	public void setDefaults(Map<String, Object> attributes) {
		if (getTopElement() != null) {
			getTopElement().setDefaults(attributes);
		}
	}

	public boolean isValid() {
		// TODO: validate unsaved changes in the UI element
		if (getTopElement() != null) {
			return getTopElement().isValid();
		}
		return true;
	}

	public Composite getControl() {
		return fContent;
	}

	public void refresh(ILaunchElement element) {
		activateElement(element);
	}

	public String getErrorMessage() {
		if (getTopElement() != null) {
			return getTopElement().getErrorMessage();
		}
		return null;
	}

	protected void activateElement(ILaunchElement element) {
		AbstractUIElement uiElement = getCurrentUIElement();
		if (uiElement != null) {
			uiElement.removeLinkListener(this);
			uiElement.dispose();
		}
		
		if (fCurrentGridElement != null) {
			fCurrentGridElement.dispose();
		}

		disposeContent();
		
		doActivateElement(element);
	}

	private void doActivateElement(ILaunchElement element) {
		if (element != null) {
			fBreadcrumbs.setCurrent(element.getId(), element.getName());
			AbstractUIElement uiElement = createUIElement(element, true);
			if (uiElement == null) {
				IUIElementFactory factory = getUIElementFactory();
				GridElement gridElement = factory.createUIElement2(element, true);
				// FIXME: revive;
				fCurrentGridElement = gridElement;
				gridElement.fillIntoGrid(getControl());
			}
			else
			{
				setCurrentUIElement(uiElement);
				uiElement.createContent(getControl());
			}
		}
		else {
			fBreadcrumbs.setCurrent(null, ""); //$NON-NLS-1$
		}
		getControl().layout();
	}

	protected String getCurrentElementId() {
		return (String)fBreadcrumbs.getCurrent();
	}

	public ILaunchElement getTopElement() {
		return fTopElement;
	}

	private void disposeContent() {
		if (fContent != null) {
			for (Control c : fContent.getChildren()) {
				c.dispose();
			}
		}
	}
	
	protected IUIElementFactory createUIElementFactory(Map<String, Object> attributes) {
		return null;
	}

	protected IUIElementFactory getUIElementFactory() {
		return fUIElementFactory;
	}
	
	private AbstractUIElement getCurrentUIElement() {
		return fCurrentUIElement;
	}
	
	private void setCurrentUIElement(AbstractUIElement uiElement) {
		fCurrentUIElement = uiElement;
	}
	
	private GridElement fCurrentGridElement;
	
	private AbstractUIElement createUIElement(ILaunchElement element, boolean showDetails) {
		IUIElementFactory factory = getUIElementFactory();
		AbstractUIElement uiElement = factory.createUIElement(element, showDetails);
		if (uiElement == null)
			return null;
		uiElement.createUIChildren(factory);
		uiElement.addLinkListener(this);
		return uiElement;
	}

	@Override
	public void linkActivated(Object obj) {
		if (obj instanceof String) {
			ILaunchElement element = getTopElement().findChild((String)obj);
			if (element != null) {
				activateElement(element);
			}
		}
	}
	
	protected boolean isInitializing() {
		return fInitializing;
	}

	protected void setInitializing(boolean initializing) {
		fInitializing = initializing;
	}

	abstract protected ILaunchElement createTopElement(Map<String, Object> attributes);
}
