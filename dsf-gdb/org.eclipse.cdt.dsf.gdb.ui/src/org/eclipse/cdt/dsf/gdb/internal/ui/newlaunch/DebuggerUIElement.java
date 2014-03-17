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
import org.eclipse.cdt.debug.ui.launch.IUIElementFactory;
import org.eclipse.cdt.dsf.gdb.newlaunch.DebuggerElement;
import org.eclipse.cdt.ui.grid.BasicGroupGridElement;
import org.eclipse.cdt.ui.grid.CompositePresentationModel;
import org.eclipse.cdt.ui.grid.GridElement;
import org.eclipse.cdt.ui.grid.IPresentationModel;
import org.eclipse.cdt.ui.grid.IStringPresentationModel;
import org.eclipse.cdt.ui.grid.PathViewElement;
import org.eclipse.cdt.ui.grid.StringPresentationModel;
import org.eclipse.cdt.ui.grid.ViewElement;
import org.eclipse.cdt.ui.grid.ViewElementFactory;
import org.eclipse.swt.widgets.Composite;

public class DebuggerUIElement extends ViewElement {
	
	private IUIElementFactory factory;
	private DebuggerElement launchElement;
	
	@Override
	public CompositePresentationModel getModel() {
		return (CompositePresentationModel)super.getModel();
	}

	public DebuggerUIElement(DebuggerElement launchElement, IUIElementFactory factory, boolean showDetails ) {
		super(new CompositePresentationModel("Debugger"));
		this.launchElement = launchElement;
		this.factory = factory;
		
		if (showDetails)
			createDetailsContent();
		
	}

	public DebuggerElement getLaunchElement() {
		return launchElement;
	}
			
	protected void createDetailsContent() {
		
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
		addChild(gdbPaths);
		
		for (ILaunchElement l: getLaunchElement().getChildren()) {
			
			GridElement uiElement;
			
			IPresentationModel model = factory.createPresentationModel(l);
			if (model != null) {
				uiElement = (new ViewElementFactory()).createViewElement(model);
			} else {
				uiElement = factory.createUIElement2(l, false);
			}
			
			assert uiElement != null;
			
			addChild(uiElement);
			
			// HACK, HACK.
			if (uiElement instanceof ViewElement) {
				IPresentationModel model2 = ((ViewElement)uiElement).getModel();
				getModel().add(model2);
			}
		}	
	}
	
	@Override
	protected void createImmediateContent(Composite parent) {
	}

	protected void createImmediateDetailsContent() {
				
			
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
}
