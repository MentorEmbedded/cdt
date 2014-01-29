/*******************************************************************************
 * Copyright (c) 2014 Mentor Graphics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mentor Graphics - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.debug.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

/**
 * Implementation of the "breadcrumbs" link navigator.
 * 
 * @since 7.4
 */
public class Breadcrumbs extends Composite {

	public interface ILinkListener {

		public void linkActivated(Object element);
	}

	private class ComponentData {
		
		Object fComponent;
		String fLabel;
		
		ComponentData(Object component, String label) {
			fComponent = component;
			fLabel = label;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ComponentData)) {
				return false;
			}
			return fComponent.equals(((ComponentData)obj).fComponent);
		}
	}

	private Link fNavigator;
	private ILinkListener fLinkListener;
	private List<ComponentData> fComponents = new ArrayList<ComponentData>();

	public Breadcrumbs(Composite parent, int style, ILinkListener listener) {
		super(parent, style);
		fLinkListener = listener;
		setLayout(new GridLayout());
		setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fNavigator = new Link(this, SWT.NONE);
		fNavigator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fNavigator.setFont(getLinkFont());
		fNavigator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (fLinkListener != null) {
					Object component = getComponentFromText(e.text);
					if (component != null) {
						fLinkListener.linkActivated(component);
					}
				}
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		fComponents.clear();
		fLinkListener = null;
	}

	public Object getCurrent() {
		return (fComponents.size() > 0) ? fComponents.get(fComponents.size() - 1).fComponent : null;
	}
	
	/**
	 * If the given component is part of the existing link it will be activated. 
	 * Otherwise it will be added to the end of the the link.
	 */
	public void setCurrent(Object component, String label) {
		String text = ""; //$NON-NLS-1$
		if (component != null) {
			ComponentData data = new ComponentData(component, label);
			int index = getComponentIndex(data);
			if (index >= 0) {
				fComponents.subList(index + 1, fComponents.size()).clear();
			}
			else {
				fComponents.add(data);
			}
			text = data.fLabel;
			for (int i = fComponents.size() - 2; i >= 0; --i) {
				ComponentData current = fComponents.get(i);
				text = String.format("<a href=\"%d\">%s</a> / %s", i, current.fLabel, text); //$NON-NLS-1$				
			}
		}
		else {
			fComponents.clear();
		}
		fNavigator.setText(text);
	}

	protected Font getLinkFont() {
		return JFaceResources.getHeaderFont();
	}

	private Object getComponentFromText(String text) {
		try {
			Integer index = Integer.parseInt(text);
			if (index >= 0 && index < fComponents.size()) {
				ComponentData data = fComponents.get(index);
				return (data != null) ? data.fComponent : null;
			}
		}
		catch(NumberFormatException e) {
		}
		return null;
	}

	private int getComponentIndex(ComponentData data) {
		return fComponents.indexOf(data);
	}
}
