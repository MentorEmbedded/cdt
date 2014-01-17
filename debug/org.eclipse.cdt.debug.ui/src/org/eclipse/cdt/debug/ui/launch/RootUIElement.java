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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.core.launch.ILaunchElement.IChangeListener;
import org.eclipse.cdt.debug.core.launch.IListLaunchElement;
import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;

/**
 * @since 7.4
 */
abstract public class RootUIElement implements ILinkListener, IChangeListener {

	class Breadcrumbs extends Composite {

		private Link fNavigator;
		private String fCurrent;

		public Breadcrumbs(Composite parent, int style) {
			super(parent, style);
			setLayout(new GridLayout());
			setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			fNavigator = new Link(this, SWT.NONE);
			fNavigator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			fNavigator.setFont(JFaceResources.getHeaderFont());
			fNavigator.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ILaunchElement element = getLaunchElement(e.text);
					activateElement((element != null) ? element : getTopElement());
				}
			});
		}
		
		public void setCurrent(ILaunchElement element) {
			if (element != null) {
				fCurrent = element.getId();
				ILaunchElement current = element;
				String text = current.getName();
				while (current.getParent() != null) {
					current = current.getParent();
					if (current instanceof IListLaunchElement)
						continue;
					text = String.format("<a href=\"%s\">%s</a> / %s", current.getId(), current.getName(), text); //$NON-NLS-1$
				}		
				fNavigator.setText(text);
			}
			else {
				fNavigator.setText(""); //$NON-NLS-1$
			}
		}
		
		public String getCurrentElementId() {
			return fCurrent;
		}
	}

	@Override
	public void elementAdded(ILaunchElement element, int details) {
		if (isInitializing())
			return;
		refresh(element.getParent());
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

		fBreadcrumbs = new Breadcrumbs(base, SWT.NONE);

		GridUtils.addHorizontalSeparatorToGrid(base, 1);
		
		fContent = new Composite(base, SWT.NONE);
		fContent.setLayout(new GridLayout(4, false));
		fContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@SuppressWarnings("unchecked")
	public void initializeFrom(ILaunchConfigurationWorkingCopy config) throws CoreException {
		fInitializing = true;
		fUIElementFactory = createUIElementFactory(config);
		ILaunchElement topElement = createTopElement(config);
		setTopElement(topElement);
		if (topElement != null) {
			topElement.initialiazeFrom(config.getAttributes());
		}
		// Restore previous state
		String id = getStoredElementId(config);
		ILaunchElement current = null;
		if (id != null && topElement != null) {
			current = topElement.findChild(id);
		}
		if (current == null) {
			current = topElement;
		}
		if (current != null) {
			activateElement(current);
		}
		fInitializing = false;
	}

	public void performApply(ILaunchConfigurationWorkingCopy config) {
		if (isInitializing())
			return;
		if (getCurrentUIElement() != null) {
			getCurrentUIElement().save();
		}
		if (getTopElement() != null) {
			Map<String, Object> attributes = new HashMap<String, Object>();
			getTopElement().performApply(attributes);
			config.setAttributes(attributes);
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		if (getTopElement() != null) {
			Map<String, Object> attributes = new HashMap<String, Object>();
			getTopElement().setDefaults(attributes);
			config.setAttributes(attributes);
		}
	}

	public boolean isValid(ILaunchConfiguration config) {
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

		disposeContent();
		
		doActivateElement(element);
	}

	private void doActivateElement(ILaunchElement element) {
		fBreadcrumbs.setCurrent(element);
		if (element != null) {
			AbstractUIElement uiElement = createUIElement(element, true);
			setCurrentUIElement(uiElement);
			uiElement.createContent(getControl());
		}		
		getControl().layout();
	}

	private ILaunchElement getLaunchElement(String id) {
		return (id != null) ? getTopElement().findChild(id) : null;
	}

	private String getCurrentElementId() {
		return fBreadcrumbs.getCurrentElementId();
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
	
	protected IUIElementFactory createUIElementFactory(ILaunchConfiguration configuration) {
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
	
	private AbstractUIElement createUIElement(ILaunchElement element, boolean showDetails) {
		IUIElementFactory factory = getUIElementFactory();
		AbstractUIElement uiElement = factory.createUIElement(element, showDetails);
		uiElement.setChildren(createUIChildren(element));
		uiElement.addLinkListener(this);
		return uiElement;
	}

	private AbstractUIElement[] createUIChildren(ILaunchElement element) {
		IUIElementFactory factory = getUIElementFactory();
		List<AbstractUIElement> list = new ArrayList<AbstractUIElement>(element.getChildren().length);
		for (ILaunchElement child : element.getChildren()) {
			if (!child.isEnabled())
				continue;
			AbstractUIElement uiChild = factory.createUIElement(child, false);
			uiChild.addLinkListener(this);
			list.add(uiChild);
		}
		return list.toArray(new AbstractUIElement[list.size()]);
	}

	@Override
	public void linkActivated(ILaunchElement element) {
		activateElement(element);
	}
	
	protected boolean isInitializing() {
		return fInitializing;
	}

	public void storeState(ILaunchConfigurationWorkingCopy config) {
		if (config == null)
			return;
		String currentId = getCurrentElementId();
		if (currentId == null)
			return;
		try {
			String typeId = config.getType().getIdentifier();
			Map<String, String> map = fElementIds.get(typeId);
			if (map == null) {
				map = new HashMap<String, String>();
				fElementIds.put(typeId, map);
			}
			map.put(config.getName(), currentId);
		}
		catch(CoreException e) {
			CDebugCorePlugin.log(e.getStatus());
		}
	}
	
	private String getStoredElementId(ILaunchConfiguration config) {
		try {
			String typeId = config.getType().getIdentifier();
			Map<String, String> map = fElementIds.get(typeId);
			return (map != null) ? map.get(config.getName()) : null;
		}
		catch(CoreException e) {
			CDebugCorePlugin.log(e.getStatus());
		}
		return null;
	}

	abstract protected ILaunchElement createTopElement(ILaunchConfiguration config);
}
