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

package org.eclipse.cdt.debug.ui.dialogs;

import java.util.HashMap;
import java.util.Map;

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
 * This class controls the underlying UI element hierarchy. It contains the navigator 
 * that allows users to switch from one UI element to another.
 * @since 7.4
 */
public class RootUIElement implements ILinkListener, IChangeListener, IStatusListener {

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
					UIElement element = getUIElement(e.text);
					activateUIElement((element != null) ? element : getTopElement());
				}
			});
		}

		@Override
		public void dispose() {
			super.dispose();
		}
		
		public void setCurrent(UIElement element) {
			fCurrent = element.getId();
			UIElement current = element;
			String text = current.getLabel();
			while (current.getParent() != null) {
				current = current.getParent();
				text = String.format("<a href=\"%s\">%s</a> / %s", current.getId(), current.getLabel(), text); //$NON-NLS-1$
			}		
			fNavigator.setText(text);
		}
		
		public String getCurrentElementId() {
			return fCurrent;
		}
	}

	private Breadcrumbs fBreadcrumbs;
	private Composite fContent;
	
	private UIElement fTopElement;

	private IAttributeStore fAttributeStore;
	
	private Map<String, String> fElementIds = new HashMap<String, String>();

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

	public void setTopElement(UIElement topElement) {
		if (fTopElement != null) {
			fTopElement.dispose();
		}
		fTopElement = topElement;
		fTopElement.setLinkListener(this);
		fTopElement.setChangeListener(this);
		fTopElement.setStatusListener(this);
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

	public void initializeFrom(IAttributeStore store) {
		UIElement topElement = getTopElement();
		UIElement current = getCurrent();
		if (!store.equals(getAttributeStore()) && current != null) {
			// Store the element path for the old attribute store
			fElementIds.put(getAttributeStore().getId(), current.getId());
			// Apply current modifications and dispose of the old content
			current.performApply(getAttributeStore());
			topElement.disposeContent();
			
			// Create children for the new store
			topElement.createChildren(store);

			// Restore the active element for the new store
			String elementId = fElementIds.get(store.getId());
			current = (elementId != null) ? topElement.find(elementId) : topElement;
		}

		if (current == null) {
			current = topElement;
		}

		setAttributeStore(store);

		doActivateUIElement(current);
	}

	public void performApply(IAttributeStore store) {
		getTopElement().performApply(store);
	}

	public void setDefaults(IAttributeStore store) {
		getTopElement().setDefaults(store);
	}

	@Override
	public void linkActivated(UIElement element) {
		activateUIElement(element);
	}

	@Override
	public void errorReported(String errorMessage) {
	}

	@Override
	public void elementAdded(UIElement element) {
		refresh();
	}

	@Override
	public void elementRemoved(UIElement element) {
		element.remove(getAttributeStore());
		refresh();
	}

	@Override
	public void elementChanged(UIElement element) {
		element.performApply(getAttributeStore());
		refresh();
	}

	public Composite getControl() {
		return fContent;
	}

	public void refresh() {
		disposeContent();
		getTopElement().initializeFrom(getAttributeStore(), getControl());
		activateUIElement(getCurrent());
		fContent.layout();
	}

	protected void activateUIElement(UIElement element) {
		UIElement current = getCurrent();
		if (current != null) {
			current.performApply(getAttributeStore());
			current.disposeContent();;
		}
		doActivateUIElement(element);
	}

	private void doActivateUIElement(UIElement element) {
		disposeContent();

		fBreadcrumbs.setCurrent(element);

		element.setShowDetails(true);;
		element.initializeFrom(getAttributeStore(), getControl());
		
		getControl().layout();
	}

	private UIElement getUIElement(String id) {
		return getTopElement().find(id);
	}

	private UIElement getCurrent() {
		String id = fBreadcrumbs.getCurrentElementId();
		if (id == null) {
			return null;
		}
		return getTopElement().find(fBreadcrumbs.getCurrentElementId());
	}

	private UIElement getTopElement() {
		return fTopElement;
	}

	private IAttributeStore getAttributeStore() {
		return fAttributeStore;
	}

	private void disposeContent() {
		if (fContent != null) {
			for (Control c : fContent.getChildren()) {
				c.dispose();
			}
		}
	}
	
	private void setAttributeStore(IAttributeStore store) {
		fAttributeStore = store;
	}
}
