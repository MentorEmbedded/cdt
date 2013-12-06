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

package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.debug.ui.launch.IUIElementFactory;
import org.eclipse.cdt.dsf.gdb.newlaunch.CoreFileElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutablesListElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;

public class UIElementFactory implements IUIElementFactory {

	@Override
	public AbstractUIElement createUIElement(ILaunchElement element, boolean showDetails) {
		if (element instanceof OverviewElement) {
			return new OverviewUIElement((OverviewElement)element);
		}
		if (element instanceof ExecutablesListElement) {
			return new ExecutablesListUIElement((ExecutablesListElement)element);
		}
		if (element instanceof ExecutableElement) {
			return new ExecutableUIElement((ExecutableElement)element, showDetails);
		}
		if (element instanceof CoreFileElement) {
			return new CoreFileUIElement((CoreFileElement)element, showDetails);
		}
		return null;
	}

}
