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
import org.eclipse.cdt.dsf.gdb.newlaunch.ArgumentsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.CoreFileElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebugOptionsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.EnvironmentElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutablesListElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.RemoteBinaryElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.SerialConnectionElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.SharedLibrariesElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.StopOnStartupElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.TCPConnectionElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.WorkingDirectoryElement;

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
		if (element instanceof DebuggerElement) {
			return new DebuggerUIElement((DebuggerElement)element, showDetails);
		}
		if (element instanceof DebuggerSettingsElement) {
			return new DebuggerSettingsUIElement((DebuggerSettingsElement)element, showDetails);
		}
		if (element instanceof ConnectionElement) {
			return new ConnectionUIElement((ConnectionElement)element, showDetails);
		}
		if (element instanceof TCPConnectionElement) {
			return new TCPConnectionUIElement((TCPConnectionElement)element, showDetails);
		}
		if (element instanceof SerialConnectionElement) {
			return new SerialConnectionUIElement((SerialConnectionElement)element, showDetails);
		}
		if (element instanceof ArgumentsElement) {
			return new ArgumentsUIElement((ArgumentsElement)element, showDetails);
		}
		if (element instanceof RemoteBinaryElement) {
			return new RemoteBinaryUIElement((RemoteBinaryElement)element, showDetails);
		}
		if (element instanceof SharedLibrariesElement) {
			return new SharedLibrariesUIElement((SharedLibrariesElement)element, showDetails);
		}
		if (element instanceof StopOnStartupElement) {
			return new StopOnStartupUIElement((StopOnStartupElement)element, showDetails);
		}
		if (element instanceof WorkingDirectoryElement) {
			return new WorkingDirectoryUIElement((WorkingDirectoryElement)element, showDetails);
		}
		if (element instanceof EnvironmentElement) {
			return new EnvironmentUIElement((EnvironmentElement)element, showDetails);
		}
		if (element instanceof BuildSettingsElement) {
			return new BuildSettingsUIElement((BuildSettingsElement)element, showDetails);
		}
		if (element instanceof DebugOptionsElement) {
			return new DebugOptionsUIElement((DebugOptionsElement)element, showDetails);
		}
		return null;
	}

}
