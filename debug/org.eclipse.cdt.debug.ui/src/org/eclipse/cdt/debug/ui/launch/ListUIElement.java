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

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.core.launch.IListLaunchElement;
import org.eclipse.cdt.debug.internal.ui.CDebugImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;

/**
 * @since 7.4
 */
public abstract class ListUIElement extends AbstractUIElement {

	final static public int SHOW_REMOVE_BUTTON = 0x1;
	final static public int SHOW_UP_BUTTON = 0x2;
	final static public int SHOW_DOWN_BUTTON = 0x4;

	private Composite fContent;
	
	public ListUIElement(IListLaunchElement launchElement) {
		super(launchElement, true);
	}

	@Override
	protected void createDetailsContent(Composite parent) {
//		fContent = new Composite(parent, SWT.BORDER);
//		GridLayout layout = new GridLayout(2, false);
//		layout.marginHeight = layout.marginWidth = 0;
//		fContent.setLayout(layout);
//		int horSpan = (parent.getLayout() instanceof GridLayout) ? ((GridLayout)parent.getLayout()).numColumns : 1;
//		fContent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, horSpan, 1));
		fContent = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
//		layout.marginHeight = layout.marginWidth = 0;
		fContent.setLayout(layout);
		int horSpan = (parent.getLayout() instanceof GridLayout) ? ((GridLayout)parent.getLayout()).numColumns : 1;
		fContent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, horSpan, 1));
		((Group)fContent).setText(getLaunchElement().getName());

		IListLaunchElement listElement = getLaunchElement();
		int length = listElement.getChildren().length;
		for (int i = 0; i < length; ++i) {
			ILaunchElement child = listElement.getChildren()[i];
			int showButtons = 0;
			if (i+1 < length) {
				showButtons |= SHOW_DOWN_BUTTON;
			}
			if (i > 0) {
				showButtons |= SHOW_UP_BUTTON;
			}
			if (child.canRemove() && listElement.getLowerLimit() < length) {
				showButtons |= SHOW_REMOVE_BUTTON;
			}
			createListElementContent(child, fContent, showButtons);
		}
		if (length < listElement.getLowerLimit() || listElement.getUpperLimit() == 0) {
			createAddButton(fContent);
		}
	}

	@Override
	public IListLaunchElement getLaunchElement() {
		return (IListLaunchElement)super.getLaunchElement();
	}

	protected void createListElementContent(final ILaunchElement element, Composite parent, int flags) {
		Link link = new Link(parent, SWT.NONE);
		link.setText(String.format("<a>%s</a>", getLinkLabel(element))); //$NON-NLS-1$
		link.setToolTipText(getLinkDescription(element));
		link.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				linkActivated(element);
			}
		});

		Composite buttonsComp = new Composite(parent, SWT.NONE);
		boolean showUpButton = (flags & SHOW_UP_BUTTON) != 0;
		boolean showDownButton = (flags & SHOW_DOWN_BUTTON) != 0;
		boolean showRemoveButton = (flags & SHOW_REMOVE_BUTTON) != 0;
		int columns = 0;
		if (showUpButton) {
			++columns;
		}
		if (showDownButton) {
			++columns;
		}
		if (showRemoveButton) {
			++columns;
		}
		GridLayout layout = new GridLayout(columns, true);
		layout.marginHeight = layout.marginWidth = 0;
		buttonsComp.setLayout(layout);
		buttonsComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		if (showUpButton) {
			Button button = createButton(buttonsComp, CDebugImages.IMG_LCL_UP_UIELEMENT, "Up", 1, 1);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					upButtonPressed(element);
				}
			});
		}
		if (showDownButton) {
			Button button = createButton(buttonsComp, CDebugImages.IMG_LCL_DOWN_UIELEMENT, "Down", 1, 1);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					downButtonPressed(element);
				}
			});
		}
		if (showRemoveButton) {
			Button button = createButton(buttonsComp, CDebugImages.IMG_LCL_REMOVE_UIELEMENT, "Delete", 1, 1);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					removeButtonPressed(element);
				}
			});
		}
	}

	protected void createAddButton(Composite parent) {
		int horSpan = 1;
		Layout layout = parent.getLayout();
		if (layout instanceof GridLayout) {
			horSpan = ((GridLayout)layout).numColumns;
		}
		Button button = createButton(parent, CDebugImages.IMG_LCL_ADD_UIELEMENT, "Add", horSpan, 1);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addButtonPressed();
			}
		});
	}
	
	protected String getLinkLabel(ILaunchElement element) {
		return element.getName();
	}
		
	protected String getLinkDescription(ILaunchElement element) {
		return element.getDescription();
	}
	
	protected Button createButton(Composite parent, String imageId, String tooltip, int horSpan, int verSpan) {
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, horSpan, verSpan));
		button.setImage(CDebugImages.get(imageId));
		button.setToolTipText(tooltip);
		return button;
	}
	
	protected void upButtonPressed(ILaunchElement element) {
		getLaunchElement().moveElementUp(element);
	}
	
	protected void downButtonPressed(ILaunchElement element) {
		getLaunchElement().moveElementDown(element);
	}
	
	protected void removeButtonPressed(ILaunchElement element) {
		getLaunchElement().removeElement(element);;
	}
	
	protected void addButtonPressed() {
		getLaunchElement().addNewElement();
	}
}
