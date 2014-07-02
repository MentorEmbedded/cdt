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

import java.util.Arrays;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.ui.launch.IUIElementFactory;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.ArgumentsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.AttachToProcessElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.BuildSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ConnectionElement.ConnectionType;
import org.eclipse.cdt.dsf.gdb.newlaunch.CoreFileElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerSettingsElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.EnvironmentElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.ExecutableElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.OverviewElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.RemoteBinaryElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.SerialConnectionElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.SharedLibrariesElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.StopOnStartupElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.TCPConnectionElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.WorkingDirectoryElement;
import org.eclipse.cdt.ui.grid.BooleanPresentationModel;
import org.eclipse.cdt.ui.grid.BooleanReflectionPresentationModel;
import org.eclipse.cdt.ui.grid.CompositePresentationModel;
import org.eclipse.cdt.ui.grid.DrilldownPresentationModel;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.LabeledCompositePresentationModel;
import org.eclipse.cdt.ui.grid.PathStringReflectionPresentationModel;
import org.eclipse.cdt.ui.grid.SelectionPresentationModel;
import org.eclipse.cdt.ui.grid.StaticStringPresentationModel;
import org.eclipse.cdt.ui.grid.StringReflectionPresentationModel;
import org.eclipse.cdt.ui.grid.ViewElementFactory;

/* VP: maybe we should use IAdaptable instead? */
public class UIElementFactory extends IUIElementFactory {
	
	@Override
	public GridElement createUIElement2(ILaunchElement element, ViewElementFactory viewElementFactory, boolean showDetails) {
		if (element instanceof ExecutableElement) {
			return new ExecutableUIElement((ExecutableElement)element, viewElementFactory, showDetails, this);
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
		if (element instanceof BuildSettingsElement) {
			return new BuildSettingsUIElement((BuildSettingsElement)element, showDetails);
		}		
		if (element instanceof EnvironmentElement) {
			return new EnvironmentUIElement((EnvironmentElement)element, showDetails);
		}
		if (element instanceof CoreFileElement) {
			return new CoreFileUIElement((CoreFileElement)element, showDetails);
		}
		if (element instanceof OverviewElement) {
			return new OverviewUIElement((OverviewElement)element, viewElementFactory, this);
		}

		return null;
	}
	
	@Override
	public IPresentationModel createPresentationModel(ILaunchElement element, boolean summary) {
		if (element instanceof DebuggerElement) {
			
			DebuggerElement launchElement = (DebuggerElement) element;
			
			CompositePresentationModel result = new CompositePresentationModel("Debugger");
			result.setClasses(new String[]{"top"});
			
			CompositePresentationModel paths = new CompositePresentationModel("Paths");
			result.add(paths);
			
			StringReflectionPresentationModel gdbPath = new PathStringReflectionPresentationModel("GDB", element,
						"getGDBPath", "setGDBPath");
			paths.add(gdbPath);
			
			StringReflectionPresentationModel scriptPath = new PathStringReflectionPresentationModel("Script", element, 
						"getGDBInitFile", "setGDBInitFile");
			paths.add(scriptPath);
			
			for (ILaunchElement child: launchElement.getChildren())
				result.add(createPresentationModel(child));
			
			return result;
			
		} else if (element instanceof DebuggerSettingsElement) {
			
			final DebuggerSettingsElement launchElement = (DebuggerSettingsElement) element;
			
			CompositePresentationModel result = new LabeledCompositePresentationModel("Options");
				
			BooleanPresentationModel stopModeModel = 
					new BooleanReflectionPresentationModel("Non Stop", element, "isNonStop", "setNonStop");
				
			result.add(stopModeModel);
				
			BooleanPresentationModel reverseModel;		
			reverseModel = new BooleanReflectionPresentationModel(LaunchUIMessages.getString("GDBDebuggerPage.reverse_Debugging"),
					element, "isReverseEnabled", "enableReverse");
			result.add(reverseModel);
			
			BooleanPresentationModel debugOnFork;
			debugOnFork = new BooleanReflectionPresentationModel(LaunchUIMessages.getString("GDBDebuggerPage.Automatically_debug_forked_processes"),
					element, "isDebugOnFork", "setDebugOnFork");
			result.add(debugOnFork);
			
			final String[] fgTraceModeLabels = new String[] {
				LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_fast"), //$NON-NLS-1$
				LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_normal"), //$NON-NLS-1$
				LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_auto"), //$NON-NLS-1$
			};
			
			SelectionPresentationModel tracepointModel;
			tracepointModel = new SelectionPresentationModel(LaunchUIMessages.getString("GDBDebuggerPage.tracepoint_mode_label"), Arrays.asList(fgTraceModeLabels)) {
					
				@Override
				protected String doGetValue() { return fgTraceModeLabels[launchElement.getTracepointMode().ordinal()]; }
					
				@Override
				protected void doSetValue(String value) {
					int index = Arrays.asList(fgTraceModeLabels).indexOf(value);
					launchElement.setTracepointMode(DebuggerSettingsElement.TracepointMode.values()[index]);				
				}
			};
			result.add(tracepointModel);
			return result;
		} 
		else if (element instanceof ConnectionElement) {
			
			final ConnectionElement launchElement = (ConnectionElement) element;
			
			CompositePresentationModel result = new CompositePresentationModel("Connection");
			result.setVisible(launchElement.isEnabled());
			
			TCPConnectionElement tcpElement = launchElement.findChild(TCPConnectionElement.class);
			final TcpConnectionPresentationModel tcpModel = new TcpConnectionPresentationModel(tcpElement);
			SerialConnectionElement serialElement = launchElement.findChild(SerialConnectionElement.class);
			final SerialConnectionPresentationModel serialModel = new SerialConnectionPresentationModel(serialElement);
			
			// FIXME: don't hardcode the list here? Merge with launch element.
			final SelectionPresentationModel type = new SelectionPresentationModel("Type", Arrays.asList("TCP", "Serial")) {
				
				@Override
				protected String doGetValue() {
					ConnectionType ct = launchElement.getConnectionType();
					return launchElement.getConnectionTypeLabel(ct);
				}
				
				@Override
				protected void doSetValue(String value) {
					boolean tcp = value.equals("TCP");
					if (tcp) {
						launchElement.setConnectionType(ConnectionType.TCP);
					} else {
						launchElement.setConnectionType(ConnectionType.SERIAL);
					}
				}			
			};
			
			type.addAndCallListener(new IPresentationModel.DefaultListener() {
				
				@Override
				public void changed(int what, Object object) {
					
					if ((what & IPresentationModel.VALUE_CHANGED) != 0) {
						boolean tcp = type.getValue().equals("TCP");
						tcpModel.setVisible(tcp);
						serialModel.setVisible(!tcp);
					}
				}			
			});
			
			result.add(type);
			result.add(tcpModel);
			result.add(serialModel);
			
			return result;
		}
		else if (element instanceof ExecutableElement) {
			
			ExecutableElement executableElement = (ExecutableElement)element;
			
			CompositePresentationModel result = new CompositePresentationModel("Executable");
			// FIXME: this is more like a CSS class, to be honest, not id.
			result.setId("executable"); //$NON-NLS-1$
			result.setClasses(new String[]{"top"});
			
			CompositePresentationModel executable = new CompositePresentationModel("Executable");
			result.add(executable);
			
			BinaryPresentationModel binaryModel = new BinaryPresentationModel("Binary", element, "getProgramName", "setProgramName");
			ProjectPresentationModel projectModel = new ProjectPresentationModel("Project", element, "getProjectName", "setProjectName");
			binaryModel.setProjectModel(projectModel);
			
			executable.add(binaryModel);
			executable.add(projectModel);
			
			RemoteBinaryElement remote = element.findChild(RemoteBinaryElement.class);
			if (remote != null)
				executable.add(new StringReflectionPresentationModel("On Target", remote, "getRemotePath", "setRemotePath"));
			
			BuildSettingsElement buildElement = element.findChild(BuildSettingsElement.class);
			
			final CompositePresentationModel buildSettings = new CompositePresentationModel("Build on Launch");
			buildSettings.setClasses(new String[]{"top"}); //$NON-NLS-1$
			
			
			//BooleanReflectionPresentationModel buildAutomatically = new BooleanReflectionPresentationModel()
			
			BuildConfigurationModel configModel = new BuildConfigurationModel("Configuration", buildElement);
			
			buildSettings.add(configModel);
			
			executable.add(new StaticStringPresentationModel() {

				@Override
				public String getString() {
					// FIXME: make this string really correspond to reality.
					return "Automatically build on launch";
				}
				
				@Override
				public void activate() {
					notifyListeners(IPresentationModel.ACTIVATED, buildSettings);
				}
			});
			
			CompositePresentationModel runtime = new CompositePresentationModel("Runtime"); //$NON-NLS-1$
			
			ArgumentsElement arguments = element.findChild(ArgumentsElement.class);
			if (arguments != null)
				runtime.add(new StringReflectionPresentationModel("Arguments", arguments, "getArguments", "setArguments"));
		
			WorkingDirectoryElement wd = element.findChild(WorkingDirectoryElement.class);
			if (wd != null)
				runtime.add(new PathStringReflectionPresentationModel("Directory", wd, "getPath", "setPath"));
			
			
			//runtime.add
			
			result.add(runtime);
			
			
			if (summary)
			{
				String programName = null;
				if (element instanceof ExecutableElement) {
					programName = ((ExecutableElement)element).getProgramName();
					if (programName.isEmpty()) {
						programName = "Not specified";
					}
				}
				if (element instanceof AttachToProcessElement) {
					programName += " (attach)";
				}
				return new DrilldownPresentationModel(programName, result);
			} else {
				return result;
			}
		}
		
		
		// TODO Auto-generated method stub
		return null;
	}
}
