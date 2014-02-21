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

import java.io.File;

import org.eclipse.cdt.debug.core.launch.ILaunchElement;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.debug.ui.launch.IUIElementFactory;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerElement;
import org.eclipse.cdt.dsf.gdb.newlaunch.StopModeElement;
import org.eclipse.cdt.ui.grid.BasicGroupGridElement;
import org.eclipse.cdt.ui.grid.BooleanPresentationModel;
import org.eclipse.cdt.ui.grid.CheckboxViewElement;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.IStringPresentationModel;
import org.eclipse.cdt.ui.grid.PathViewElement;
import org.eclipse.cdt.ui.grid.StringPresentationModel;
import org.eclipse.cdt.ui.grid.ViewElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DebuggerUIElement extends AbstractUIElement {

	private Text fGDBCommandText;
	private Text fGDBInitText;
	private Button fNonStopButton;
	
	private GridElement details;
	private IUIElementFactory factory;

	public DebuggerUIElement(DebuggerElement launchElement, boolean showDetails ) {
		super(launchElement, showDetails);
	}

	@Override
	public DebuggerElement getLaunchElement() {
		return (DebuggerElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		if (details != null)
			details.dispose();
		fGDBCommandText = null;
		fGDBInitText = null;
		fNonStopButton = null;
	}
	
	@Override
	public void createUIChildren(IUIElementFactory factory) {
		this.factory = factory;
	}
	
	@Override
	protected void createDetailsContent(Composite parent) {
		
		details = new GridElement() {
			@Override
			protected void createImmediateContent(Composite parent) {
				createImmediateDetailContent(parent);	
			}
			
			@Override
			protected void populateChildren() {
				for (ILaunchElement l: getLaunchElement().getChildren()) {
					GridElement uiElement = factory.createUIElement2(l, false);
					
					// FIXME: convert this into assert for production.
					if (uiElement != null) {
						addChild(uiElement);
					
						// HACK, HACK.
						if (uiElement instanceof ViewElement) {
							IPresentationModel model = ((ViewElement)uiElement).getModel();
							model.addAndCallListener(new IPresentationModel.Listener() {
								
								@Override
								public void changed(int what, Object object) {
									if (what == IPresentationModel.ACTIVATED) {
										linkActivated((ILaunchElement)object);
									}
								}
							});
						}
					}
				}
			}
		};
		
		details.fillIntoGrid(parent);
	}

	protected void createImmediateDetailContent(final Composite parent) {
		
		IStringPresentationModel gdbModel = new StringPresentationModel("GDB") {
			@Override
			public String doGetValue() { 	return getLaunchElement().getGDBPath(); };
			
			@Override
			public void doSetValue(String value) { getLaunchElement().setGDBPath(value); }
		};
		
		final PathViewElement gdb = new PathViewElement(gdbModel);
		
		IStringPresentationModel gdbInitModel = new StringPresentationModel("Script") {
			
			@Override
			protected String doGetValue() { return getLaunchElement().getGDBInitFile(); }
			
			@Override
			protected void doSetValue(String value) { getLaunchElement().setGDBInitFile(value); }
		};
		final PathViewElement gdbInit = new PathViewElement(gdbInitModel);
	
		BasicGroupGridElement gdbPaths = new BasicGroupGridElement("Paths") {
			
			{
				indentFirst();
				addChild(gdb);
				addChild(gdbInit);
			}
		};	
		gdbPaths.fillIntoGrid(parent);
			
		/*
		Label label = ControlFactory.createLabel(parent, LaunchUIMessages.getString("GDBDebuggerPage.gdb_debugger")); //$NON-NLS-1$
		label.setLayoutData(new GridData());
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		
		new Label(parent, SWT.NONE);
		
		fGDBCommandText = ControlFactory.createTextField(parent, SWT.SINGLE | SWT.BORDER);
		fGDBCommandText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
            	getLaunchElement().setGDBPath(fGDBCommandText.getText().trim());
			}
		});
		Button button = new Button(parent, SWT.PUSH);
		button.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		button.setToolTipText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_browse")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleGDBButtonSelected(parent.getShell());
			}
		});
		
		*/
		
		
		/*
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridUtils.fillIntoGrid(comp, parent);
		
		Label label = ControlFactory.createLabel(comp, LaunchUIMessages.getString("GDBDebuggerPage.gdb_debugger")); //$NON-NLS-1$
		label.setLayoutData(new GridData());
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		GridUtils.fillIntoGrid(label, comp);

		fGDBCommandText = ControlFactory.createTextField(comp, SWT.SINGLE | SWT.BORDER);
		fGDBCommandText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
            	getLaunchElement().setGDBPath(fGDBCommandText.getText().trim());
			}
		});
		Button button = new Button(comp, SWT.PUSH);
		button.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		button.setToolTipText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_browse")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleGDBButtonSelected(parent.getShell());
			}
		});

		label = ControlFactory.createLabel(comp, LaunchUIMessages.getString("GDBDebuggerPage.gdb_command_file")); //$NON-NLS-1$
		label.setLayoutData(new GridData());
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		GridUtils.fillIntoGrid(label, comp);
		
		fGDBInitText = ControlFactory.createTextField(comp, SWT.SINGLE | SWT.BORDER);
		fGDBInitText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fGDBInitText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
            	getLaunchElement().setGDBInitFile(fGDBInitText.getText().trim());
			}
		});

		button = new Button(comp, SWT.PUSH);
		button.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		button.setToolTipText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_cmdfile_browse")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleGDBInitButtonSelected(parent.getShell());
			}
		});

		label = ControlFactory.createLabel(
			comp, 
			LaunchUIMessages.getString("GDBDebuggerPage.cmdfile_warning"), //$NON-NLS-1$
			200, 
			SWT.DEFAULT, 
			SWT.WRAP);

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd.widthHint = 200;
		label.setLayoutData(gd);
		
		GridUtils.createVerticalSpacer(comp, 1);*/
	}

	private void handleGDBButtonSelected(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.NONE);
		dialog.setText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_browse_dlg_title")); //$NON-NLS-1$
		String gdbCommand = fGDBCommandText.getText().trim();
		int lastSeparatorIndex = gdbCommand.lastIndexOf(File.separator);
		if (lastSeparatorIndex != -1) {
			dialog.setFilterPath(gdbCommand.substring(0, lastSeparatorIndex));
		}
		String res = dialog.open();
		if (res == null) {
			return;
		}
		fGDBCommandText.setText(res);
	}

	private void handleGDBInitButtonSelected(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.NONE);
		dialog.setText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_cmdfile_dlg_title")); //$NON-NLS-1$
		String gdbCommand = fGDBInitText.getText().trim();
		int lastSeparatorIndex = gdbCommand.lastIndexOf(File.separator);
		if (lastSeparatorIndex != -1) {
			dialog.setFilterPath(gdbCommand.substring(0, lastSeparatorIndex));
		}
		String res = dialog.open();
		if (res == null) {
			return;
		}
		fGDBInitText.setText(res);
	}

	@Override
	protected void initializeDetailsContent() {

	}

	@Override
	protected void createSummaryContent(final Composite parent) {
				
		IStringPresentationModel gdbModel = new StringPresentationModel(LaunchUIMessages.getString("GDBDebuggerPage.gdb_debugger")) {
			@Override
			public String getValue() {
				return getLaunchElement().getGDBPath();
			};
			
			@Override
			public void setValue(String value) {
				getLaunchElement().setGDBPath(value);
			}
		};
		
		PathViewElement gdb = new PathViewElement(gdbModel);
		gdb.fillIntoGrid(parent);		
		
		/*
		fGDBCommandText = ControlFactory.createTextField(parent, SWT.SINGLE | SWT.BORDER);
		fGDBCommandText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText(ModifyEvent evt) {
            	getLaunchElement().setGDBPath(fGDBCommandText.getText().trim());
			}
		});
		
		Button button = new Button(parent, SWT.PUSH);
		button.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		button.setToolTipText(LaunchUIMessages.getString("GDBDebuggerPage.gdb_browse")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleGDBButtonSelected(parent.getShell());
			}
		});
		
		*/
		
		final StopModeElement stopMode = getLaunchElement().findChild(StopModeElement.class);
		
		CheckboxViewElement nonStop = new CheckboxViewElement(
				new BooleanPresentationModel(LaunchUIMessages.getString("GDBDebuggerPage.nonstop_mode")) 
		{
			@Override
			protected boolean doGetValue() {
				return stopMode.isNonStop();
			}
			
			@Override
			protected void doSetValue(boolean value) {
				stopMode.setNonStop(value);
			}
		});
		
		nonStop.fillIntoGrid(parent);		
		/*
		Link link = new Link(parent, SWT.NONE);
		link.setText("<a>Details</a>");
		
		new Label(parent, SWT.NONE); */
	}

	@Override
	protected void initializeSummaryContent() {
		if (fGDBCommandText != null) {
			fGDBCommandText.setText(getLaunchElement().getGDBPath());
		}
		StopModeElement stopMode = getLaunchElement().findChild(StopModeElement.class);
		if (fNonStopButton != null && stopMode != null && stopMode.isEnabled()) {
			fNonStopButton.setSelection(stopMode.isNonStop());
		}
	}

	@Override
	protected boolean hasMultipleRows() {
		// HACK HACK.
		return false;
	}
}
