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
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.ViewElementFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @since 7.6
 */
public abstract class IUIElementFactory {

	public abstract GridElement createUIElement2(ILaunchElement l, ViewElementFactory viewElementFactory, boolean b, Composite parent);
	
	// FIXME: temporary hack, remove.
	public abstract IPresentationModel createPresentationModel(ILaunchElement element, boolean summary);
	
	public IPresentationModel createPresentationModel(ILaunchElement element)
	{
		return createPresentationModel(element, false);
	}
}
