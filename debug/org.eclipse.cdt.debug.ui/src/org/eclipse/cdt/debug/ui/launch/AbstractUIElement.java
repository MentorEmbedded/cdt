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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.internal.ui.CDebugImages;
import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.debug.ui.dialogs.Breadcrumbs.ILinkListener;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Widget;

/**
 * @since 7.4
 */
abstract public class AbstractUIElement {

	private ILaunchElement fLaunchElement;
	final private boolean fShowDetails;
	private List<AbstractUIElement> fChildren = new ArrayList<AbstractUIElement>();
	private ListenerList fLinkListeners = new ListenerList();

	private Composite fParent;
	private Set<Widget> fSummaryWidgets = new HashSet<Widget>();
	
	public AbstractUIElement(ILaunchElement launchElement, boolean showDetails) {
		super();
		fLaunchElement = launchElement;
		fShowDetails = showDetails;
	}

	public void dispose() {
		fLinkListeners.clear();
		disposeContent();
		for (AbstractUIElement child : fChildren) {
			child.dispose();
		}
		fChildren.clear();
		fParent = null;
	}

	public ILaunchElement getLaunchElement() {
		return fLaunchElement;
	}

	public AbstractUIElement[] getAllChildren() {
		return fChildren.toArray(new AbstractUIElement[fChildren.size()]);
	}

	@SuppressWarnings("unchecked")
	public<V> V findChild(Class<V> childClass) {
		if (this.getClass().equals(childClass)) {
			return (V)this;
		}
		for (AbstractUIElement el : getAllChildren()) {
			V child = el.findChild(childClass);
			if (child != null) {
				return child;
			}
		}
		return null;
	}

	public void createContent(Composite parent) {
		fParent = parent;
		disposeContent();
		if (fShowDetails) {
			createDetailsContent(parent);
		}
		else {
			createSummaryContent(parent);
		}
	}

	public void disposeContent() {
		for (AbstractUIElement child : fChildren) {
			child.disposeContent();
		}
		for (Widget w : fSummaryWidgets) {
			w.dispose();
		}
		fSummaryWidgets.clear();
	}

	protected void createSummaryContent(Composite parent) {		
		Link link = new Link(parent, SWT.BORDER);
		link.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, hasContent() ? 1 : 4, 1));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				linkActivated(getLaunchElement());
			}
		});
		link.setText(String.format("<a>%s</a>", getLabel())); //$NON-NLS-1$
		link.setToolTipText(getDescription());
		fSummaryWidgets.add(link);

		if (hasContent()) {
			if (hasMultipleRows()) {
				fSummaryWidgets.add(GridUtils.createBar(parent, 1));
			}
			Composite content = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = layout.marginWidth = 0;
			content.setLayout(layout);
			int horSpan = 1;
			if (!hasMultipleRows()) {
				++horSpan;
			}
			if (!isRemovable()) {
				++horSpan;
			}
			content.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, horSpan, 1));
			fSummaryWidgets.add(content);
			if (isRemovable()) {
				Button removeButton = new Button(parent, SWT.PUSH);
				removeButton.setImage(CDebugImages.get(CDebugImages.IMG_LCL_REMOVE_UIELEMENT));
				removeButton.setToolTipText("Remove");
				removeButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				removeButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						getLaunchElement().removeChild(getLaunchElement());
					}
				});
				fSummaryWidgets.add(removeButton);
			}
			
			doCreateSummaryContent(content);
		}
		
		fSummaryWidgets.add(GridUtils.createVerticalSpacer(parent, 1));

		initializeSummaryContent();
	}

	protected void createDetailsContent(Composite parent) {
		doCreateDetailsContent(parent);
		createChildrenContent(parent);
		initializeDetailsContent();
	}

	/**
	 * Saves the UI widgets data in the underlying launch element.
	 */
	public void save() {
	}
	
	public void refresh(boolean content) {
		if (content && fParent != null) {
			createContent(fParent);
		}
		else if (fShowDetails) {
			initializeDetailsContent();
		}
		else {
			initializeSummaryContent();
		}
		if (fParent != null) {
			fParent.layout();
		}
	}
	
	public void setChildren(AbstractUIElement[] children) {
		for (AbstractUIElement child : fChildren) {
			child.dispose();
		}
		fChildren.clear();
		fChildren.addAll(Arrays.asList(children));
	}
	
	public void addLinkListener(ILinkListener listener) {
		fLinkListeners.add(listener);
	}
	
	public void removeLinkListener(ILinkListener listener) {
		fLinkListeners.remove(listener);
	}
	
	protected void linkActivated(ILaunchElement element) {
		for (Object o : fLinkListeners.getListeners()) {
			((ILinkListener)o).linkActivated(element.getId());
		}
	}
	
	protected String getLabel() {
		return getLaunchElement().getName();
	}
	
	protected String getDescription() {
		return getLaunchElement().getDescription();
	}

	protected boolean hasContent() {
		return true;
	}

	protected boolean isRemovable() {
		return getLaunchElement().canRemove();
	}

	protected boolean hasMultipleRows() {
		return false;
	}
	
	protected void doCreateSummaryContent(Composite parent) {
	}
	
	protected void doCreateDetailsContent(Composite parent) {
	}
	
	protected void createChildrenContent(Composite parent) {
		for (AbstractUIElement child : getFiteredChildren()) {
			child.createContent(parent);
		}
	}
	
	protected void initializeSummaryContent() {
	}
	
	protected void initializeDetailsContent() {
	}
	
	protected AbstractUIElement[] getFiteredChildren() {
		return fChildren.toArray(new AbstractUIElement[fChildren.size()]);
	}
}
