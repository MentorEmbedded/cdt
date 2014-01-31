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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.debug.internal.ui.dialogfields.DialogField;
import org.eclipse.cdt.debug.internal.ui.dialogfields.IDialogFieldListener;
import org.eclipse.cdt.debug.internal.ui.dialogfields.IListAdapter;
import org.eclipse.cdt.debug.internal.ui.dialogfields.LayoutUtil;
import org.eclipse.cdt.debug.internal.ui.dialogfields.ListDialogField;
import org.eclipse.cdt.debug.ui.dialogs.GridUtils;
import org.eclipse.cdt.debug.ui.launch.AbstractUIElement;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.LaunchUIMessages;
import org.eclipse.cdt.dsf.gdb.newlaunch.SharedLibrariesElement;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

public class SharedLibrariesUIElement extends AbstractUIElement {

	private class AddDirectoryDialog extends Dialog {

		protected Text fText;
		
		private Button fBrowseButton;

		private IPath fValue;

		/** 
		 * Constructor for AddDirectoryDialog. 
		 */
		public AddDirectoryDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite)super.createDialogArea(parent);

			Composite subComp = ControlFactory.createCompositeEx(composite, 2, GridData.FILL_HORIZONTAL);
			((GridLayout)subComp.getLayout()).makeColumnsEqualWidth = false;
			GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			subComp.setLayoutData(data);
			subComp.setFont(parent.getFont());

			fText = new Text(subComp, SWT.SINGLE | SWT.BORDER);
			fText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			fText.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					updateOKButton();
				}
			});

			fBrowseButton = ControlFactory.createPushButton(subComp, LaunchUIMessages.getString("GDBServerDebuggerPage.7")); //$NON-NLS-1$
			data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			fBrowseButton.setLayoutData(data);
			fBrowseButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent evt) {
					DirectoryDialog dialog = new DirectoryDialog(AddDirectoryDialog.this.getShell());
					dialog.setMessage(LaunchUIMessages.getString("SolibSearchPathBlock.5")); //$NON-NLS-1$
					String res = dialog.open();
					if (res != null) {
						fText.setText(res);
					}
				}
			});

			applyDialogFont(composite);
			return composite;
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(LaunchUIMessages.getString("SolibSearchPathBlock.Add_Directory")); //$NON-NLS-1$
		}

		public IPath getValue() {
			return fValue;
		}

		private void setValue(String value) {
			fValue = (value != null) ? new Path(value) : null;
		}

		@Override
		protected void buttonPressed(int buttonId) {
			if (buttonId == IDialogConstants.OK_ID) {
				setValue(fText.getText());
			}
			else {
				setValue(null);
			}
			super.buttonPressed(buttonId);
		}

		protected void updateOKButton() {
			Button okButton = getButton(IDialogConstants.OK_ID);
			String text = fText.getText();
			okButton.setEnabled(isValid(text));
		}

		protected boolean isValid(String text) {
			return (text.trim().length() > 0);
		}

		@Override
		protected Control createButtonBar(Composite parent) {
			Control control = super.createButtonBar(parent);
			updateOKButton();
			return control;
		}
	}

	public class SolibSearchPathListDialogField extends ListDialogField {

		public SolibSearchPathListDialogField(IListAdapter adapter, String[] buttonLabels, ILabelProvider lprovider) {
			super(adapter, buttonLabels, lprovider);
		}

		@Override
		protected boolean managedButtonPressed(int index) {
			boolean result = super.managedButtonPressed(index);
			if (result)
				buttonPressed(index);
			return result;
		}

		@Override
		protected boolean getManagedButtonState(ISelection sel, int index) {
			if (index > 3)
				return getButtonState(sel, index);
			return super.getManagedButtonState(sel, index);
		}
	}

	private static String[] fgStaticButtonLabels = new String[] {
		LaunchUIMessages.getString("SolibSearchPathBlock.0"), //$NON-NLS-1$
		LaunchUIMessages.getString("SolibSearchPathBlock.1"), //$NON-NLS-1$
		LaunchUIMessages.getString("SolibSearchPathBlock.2"), //$NON-NLS-1$
		LaunchUIMessages.getString("SolibSearchPathBlock.3"), //$NON-NLS-1$
		LaunchUIMessages.getString("SolibSearchPathBlock.6"), //$NON-NLS-1$
		null, // separator
	};

	// Common widgets
	private Button fAutoSolibButton;

	// Detail widgets
	private SolibSearchPathListDialogField fDirList;
	private File[] fAutoSolibs = new File[0];
	
	public SharedLibrariesUIElement(SharedLibrariesElement launchElement, boolean showDetails) {
		super(launchElement, showDetails);
		IListAdapter listAdapter = new IListAdapter() {
			@Override
			public void customButtonPressed(DialogField field, int index) {
				buttonPressed(index);
			}
			@Override
			public void selectionChanged(DialogField field) {
			}
		};
		ILabelProvider lp = new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IPath)
					return ((IPath)element).toOSString();
				return super.getText(element);
			}
		};
		fDirList = new SolibSearchPathListDialogField(listAdapter, fgStaticButtonLabels, lp);
		fDirList.setLabelText(LaunchUIMessages.getString("SolibSearchPathBlock.4")); //$NON-NLS-1$
		fDirList.setUpButtonIndex(1);
		fDirList.setDownButtonIndex(2);
		fDirList.setRemoveButtonIndex(3);

		IDialogFieldListener fieldListener = new IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(DialogField field) {
				sharedLibraryListChanged();
			}
		};
		fDirList.setDialogFieldListener(fieldListener);
	}

	@Override
	public SharedLibrariesElement getLaunchElement() {
		return (SharedLibrariesElement)super.getLaunchElement();
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fAutoSolibButton = null;
	}

	@Override
	protected void doCreateSummaryContent(Composite parent) {
		fAutoSolibButton = new Button(parent, SWT.CHECK);
		GridUtils.fillIntoGrid(fAutoSolibButton, parent);
		fAutoSolibButton.setText(LaunchUIMessages.getString("GDBSolibBlock.0")); //$NON-NLS-1$
		fAutoSolibButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoSolibButtonChecked();
			}
		});
	}

	@Override
	protected void doCreateDetailsContent(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));		
		GridUtils.fillIntoGrid(comp, parent);
		
		PixelConverter converter = new PixelConverter(comp);
		fDirList.doFillIntoGrid(comp, 3);
		LayoutUtil.setHorizontalSpan(fDirList.getLabelControl(null), 2);
		LayoutUtil.setWidthHint(fDirList.getLabelControl(null), converter.convertWidthInCharsToPixels(30));
		LayoutUtil.setHorizontalGrabbing(fDirList.getListControl(null));
		
		fAutoSolibButton = new Button(parent, SWT.CHECK);
		GridUtils.fillIntoGrid(fAutoSolibButton, parent);
		fAutoSolibButton.setText(LaunchUIMessages.getString("GDBSolibBlock.0")); //$NON-NLS-1$
		fAutoSolibButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoSolibButtonChecked();
			}
		});
	}

	@Override
	protected void initializeSummaryContent() {
		if (fAutoSolibButton != null) {
			fAutoSolibButton.setSelection(getLaunchElement().isAutoLoadSymbols());
		}
	}

	@Override
	protected void initializeDetailsContent() {
		if (fAutoSolibButton != null) {
			fAutoSolibButton.setSelection(getLaunchElement().isAutoLoadSymbols());
		}
		if (fDirList != null) {
			String[] values = getLaunchElement().getSharedLibraryPaths();
			ArrayList<Path> paths = new ArrayList<Path>(values.length);
			for (String v : values) {
				paths.add(new Path(v));
			}
			fDirList.addElements(paths);
			
			String[] autoSolibNames = getLaunchElement().getAutoSolibList();
			fAutoSolibs = new File[autoSolibNames.length];
			for ( int i = 0; i < fAutoSolibs.length; ++i) {
				fAutoSolibs[i] = new File(autoSolibNames[i]);
			}
		}
	}

	private void autoSolibButtonChecked() {
		getLaunchElement().setAutoLoadSymbols(fAutoSolibButton.getSelection());
	}

	@Override
	protected boolean hasMultipleRows() {
		return getLaunchElement().getSharedLibraryPaths().length > 0;
	}

	protected void buttonPressed(int index) {
		if (index == 0) { // Add button
			addDirectory();
		}
		else if (index == 4) { //Select from list
			selectFromList();
		}
	}

	private boolean getButtonState(ISelection sel, int index) {
		if (index == 4) { // select from list
			return (!sel.isEmpty());
		}
		return true;
	}
	
	private void sharedLibraryListChanged() {
		@SuppressWarnings("unchecked")
		List<IPath> elements = fDirList.getElements();		
		ArrayList<String> values = new ArrayList<String>(elements.size());
		Iterator<IPath> it = elements.iterator();
		while(it.hasNext()) {
			values.add((it.next()).toOSString());
		}
		getLaunchElement().setSharedLibraryPaths(values.toArray( new String[values.size()]));
	}

	private boolean addDirectory() {
		boolean changed = false;
		AddDirectoryDialog dialog = new AddDirectoryDialog(Display.getCurrent().getActiveShell());
		dialog.open();
		IPath result = dialog.getValue();
		if (result != null && !contains(result)) {
			fDirList.addElement(result);
			changed = true; 
		}
		return changed;
	}

	private boolean contains(IPath path) {
		@SuppressWarnings("unchecked")
		List<IPath> list = fDirList.getElements();
		
		Iterator<IPath> it = list.iterator();
		while(it.hasNext()) {
			IPath p = it.next();
			if (p.toFile().equals(path.toFile()))
				return true;
		}
		return false;
	}

	protected void selectFromList() {
		@SuppressWarnings("unchecked")
		List<IPath> dirList = fDirList.getSelectedElements();
		
		final HashSet<File> libs = new HashSet<File>(10);
		if (generateLibraryList(dirList.toArray(new IPath[dirList.size()]), libs)) {
			ITreeContentProvider cp = new ITreeContentProvider() {
                @Override
				public Object[] getChildren(Object parentElement) {
					return getElements(parentElement);
				}
                
                @Override
				public Object getParent(Object element) {
					if (libs.contains(element))
						return libs;
					return null;
				}
                
                @Override
				public boolean hasChildren(Object element) {
					return false;
				}
                
                @SuppressWarnings("unchecked")
				@Override
				public Object[] getElements(Object inputElement) {
					if (inputElement instanceof Set) {
						return ((Set<File>)inputElement).toArray();
					}
					return new Object[0];
				}
                
                @Override
				public void dispose() {
				}
                
                @Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}	
			};
	
			LabelProvider lp = new LabelProvider() {
	
				@Override
				public String getText(Object element) {
					if (element instanceof File)
						return ((File)element).getName();
					return super.getText(element);
				}
			};
			CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(Display.getCurrent().getActiveShell(), lp, cp);
			dialog.setTitle(LaunchUIMessages.getString("SolibSearchPathBlock.7")); //$NON-NLS-1$
			dialog.setMessage(LaunchUIMessages.getString("SolibSearchPathBlock.8")); //$NON-NLS-1$
			dialog.setEmptyListMessage(LaunchUIMessages.getString("SolibSearchPathBlock.9")); //$NON-NLS-1$
			dialog.setComparator(new ViewerSorter());
			dialog.setInput(libs);
			dialog.setInitialElementSelections(Arrays.asList(fAutoSolibs));
			if (dialog.open() == Window.OK) {
				Object[] result = dialog.getResult();
				fAutoSolibs = Arrays.asList(result).toArray(new File[result.length]);
				String[] autoSolibNames = new String[fAutoSolibs.length];
				for (int i = 0; i < fAutoSolibs.length; ++i) {
					autoSolibNames[i] = fAutoSolibs[i].getPath();
				}
				getLaunchElement().setAutoSolibList(autoSolibNames);
			}
		}
	}

	private boolean generateLibraryList(final IPath[] paths, final Set<File> libs) {
		boolean result = true;

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				for (int i = 0; i < paths.length; ++i) {
					File dir = paths[i].toFile();
					if (dir.exists() && dir.isDirectory()) {
						File[] all = dir.listFiles();
						for (int j = 0; j < all.length; ++j) {
							if (monitor.isCanceled()) {
								throw new InterruptedException();
							}
							monitor.subTask(all[j].getPath());
							String libName = getSharedLibraryName(all[j]);
							if (libName != null) {
								libs.add(new File(libName));
							}
						}
					}
				}
			}
		};
        try {
        	IRunnableContext context = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			context.run(true, true, runnable);
		}
		catch(InvocationTargetException e) {
		}
		catch(InterruptedException e) {
			result = false;
		}
		return result;
	}

	protected String getSharedLibraryName(File file) {
		if (!file.isFile())
			return null;
		// no project: for now
		IPath path = new Path(file.getPath());
		String name = path.lastSegment();
		String extension = path.getFileExtension();
		if (extension != null && (extension.compareTo("so") == 0 || extension.compareToIgnoreCase("dll") == 0)) //$NON-NLS-1$ //$NON-NLS-2$
			return name;
		return (name.indexOf(".so.") >= 0) ? name : null; //$NON-NLS-1$
	}

	protected boolean isSharedLibrary(File file) {
		if (!file.isFile())
			return false;
		// no project: for now
		IPath path = new Path(file.getPath());
		String extension = path.getFileExtension();
		if (extension != null && (extension.compareTo("so") == 0 || extension.compareToIgnoreCase("dll") == 0)) //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		String name = path.lastSegment();
		return (name.indexOf(".so.") >= 0); //$NON-NLS-1$
	}
}
