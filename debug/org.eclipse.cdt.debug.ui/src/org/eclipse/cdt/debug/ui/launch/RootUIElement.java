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
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.ViewElement;
import org.eclipse.cdt.ui.grid.ViewElementFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @since 7.6
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
	
	private Map<String, Map<String, String>> fElementIds = new HashMap<String, Map<String,String>>();

	private boolean fInitializing = false;

	private ILaunchElement fCurrentLaunchELement;

	private ViewElementFactory viewElementFactory;

	public RootUIElement() {
		super();
		viewElementFactory = createViewElementFactory();
	}
	
	protected ViewElementFactory createViewElementFactory()
	{
		// FIXME: this should be really part of debugger-specific code.
		return new ViewElementFactory();
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
		
		fContent.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F5)
					if (fCurrentLaunchELement != null)
						activateElement(fCurrentLaunchELement);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
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
		
		if (fCurrentGridElement != null) {
			fCurrentGridElement.dispose();
		}

		disposeContent();
		
		doActivateElement(element);
	}
	
	protected void activate(IPresentationModel model)
	{
		if (fCurrentGridElement != null) {
			fCurrentGridElement.dispose();
		}

		disposeContent();
		
		fBreadcrumbs.setCurrent(model, model.getName());
		GridElement gridElement = viewElementFactory.create(model, getControl());
		fCurrentGridElement = gridElement;
		getControl().layout();
		
		
		connectModel(model);
	}

	private void doActivateElement(ILaunchElement element) {
		fCurrentLaunchELement = element;
		if (element != null) {
//			fBreadcrumbs.setCurrent(element.getId(), element.getName());
			
			IUIElementFactory factory = getUIElementFactory();
			IPresentationModel model = factory.createPresentationModel(element);
				
			if (model != null) {
				activate(model);
			} else {
				fBreadcrumbs.setCurrent(element.getId(), element.getName());
				GridElement gridElement = factory.createUIElement2(element, viewElementFactory, true, getControl());
				
				fCurrentGridElement = gridElement;
				gridElement.create(getControl());
				if (gridElement instanceof ViewElement) {
					model = ((ViewElement)gridElement).getModel();
					connectModel(model);
				}
				getControl().layout();
			}
		}
		else {
			fBreadcrumbs.setCurrent(null, ""); //$NON-NLS-1$
		}
		getControl().layout();
	}

	private void connectModel(IPresentationModel model) {
		// FIXME: add code to disconnect this listener.
		model.addAndCallListener(new IPresentationModel.DefaultListener() {

			@Override
			public void changed(int what, Object object) {
				if (what == IPresentationModel.ACTIVATED) {
					if (object instanceof IPresentationModel) {
						activate((IPresentationModel)object);
					} else if (object instanceof String) {
						linkActivated(object);
					} else if (object instanceof ILaunchElement) {
						linkActivated(((ILaunchElement)object).getId());
					}
				}
			}
		});
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
		
	private GridElement fCurrentGridElement;
	
	@Override
	public void linkActivated(Object obj) {
		if (obj instanceof String) {
			ILaunchElement element = getTopElement().findChild((String)obj);
			if (element != null) {
				activateElement(element);
			}
		} else if (obj instanceof IPresentationModel) {
			activate((IPresentationModel)obj);
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
