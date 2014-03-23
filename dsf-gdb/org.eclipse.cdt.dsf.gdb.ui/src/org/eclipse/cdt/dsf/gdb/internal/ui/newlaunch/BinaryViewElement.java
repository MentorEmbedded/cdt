package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbUIPlugin;
import org.eclipse.cdt.dsf.gdb.internal.ui.IGdbUIConstants;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.ui.CElementLabelProvider;
import org.eclipse.cdt.ui.grid.StringViewElement;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

public class BinaryViewElement extends StringViewElement {
	
	public BinaryViewElement(BinaryPresentationModel model) {
		super(model);
	}
	
	@Override
	public BinaryPresentationModel getModel()
	{
		return (BinaryPresentationModel) super.getModel();
	}

	@Override
	protected void createButton(final Composite parent) {

		Composite buttonBar = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = layout.marginWidth = 0;
		buttonBar.setLayout(layout);
		buttonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		Button browseButton = new Button(buttonBar, SWT.PUSH);
		browseButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_BROWSE));
		browseButton.setToolTipText(LaunchMessages.getString("Launch.common.Browse_2")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				text.setText(handleBrowseButtonSelected(
					parent.getShell(), 
					LaunchMessages.getString("CMaintab.Application_Selection"))); //$NON-NLS-1$
			}
		});
		
		Button fSearchButton = new Button(buttonBar, SWT.PUSH);
		fSearchButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_SEARCH_PROJECT));
		fSearchButton.setToolTipText(LaunchMessages.getString("CMainTab.Search...")); //$NON-NLS-1$
		fSearchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleSearchButtonSelected(parent.getShell());
			}
		});
		
		Button varButton = new Button(buttonBar, SWT.PUSH);
		varButton.setImage(GdbUIPlugin.getImage(IGdbUIConstants.IMG_OBJ_PATH_VARIABLES));
		varButton.setToolTipText(LaunchMessages.getString("CMainTab.Variables")); //$NON-NLS-1$
		varButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleVariablesButtonSelected(parent.getShell(), text);
			}
		});				
		
	};
	
	protected String handleBrowseButtonSelected(Shell shell, String title) {
		FileDialog fileDialog = new FileDialog(shell, SWT.NONE);
		fileDialog.setText(title);
		fileDialog.setFileName(text.getText());
		return fileDialog.open();
	}

	protected void handleSearchButtonSelected(Shell shell) {
		if (getCProject() == null) {
			MessageDialog.openInformation(shell, LaunchMessages.getString("CMainTab.Project_required"), //$NON-NLS-1$
					LaunchMessages.getString("CMainTab.Enter_project_before_searching_for_program")); //$NON-NLS-1$
			return;
		}

		ILabelProvider programLabelProvider = new CElementLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IBinary) {
					IBinary bin = (IBinary)element;
					StringBuffer name = new StringBuffer();
					name.append(bin.getPath().lastSegment());
					return name.toString();
				}
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element) {
				if (! (element instanceof ICElement)) {
					return super.getImage(element);
				}
				ICElement celement = (ICElement)element;

				if (celement.getElementType() == ICElement.C_BINARY) {
					IBinary belement = (IBinary)celement;
					if (belement.isExecutable()) {
						return DebugUITools.getImage(IDebugUIConstants.IMG_ACT_RUN);
					}
				}

				return super.getImage(element);
			}
		};

		ILabelProvider qualifierLabelProvider = new CElementLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IBinary) {
					IBinary bin = (IBinary)element;
					StringBuffer name = new StringBuffer();
					name.append(bin.getCPU() + (bin.isLittleEndian() ? "le" : "be")); //$NON-NLS-1$ //$NON-NLS-2$
					name.append(" - "); //$NON-NLS-1$
					name.append(bin.getPath().toString());
					return name.toString();
				}
				return super.getText(element);
			}
		};

		TwoPaneElementSelector dialog = new TwoPaneElementSelector(shell, programLabelProvider, qualifierLabelProvider);
		dialog.setElements(getBinaryFiles(shell, getCProject()));
		dialog.setMessage(LaunchMessages.getString("CMainTab.Choose_program_to_run")); //$NON-NLS-1$
		dialog.setTitle(LaunchMessages.getString("CMainTab.Program_Selection")); //$NON-NLS-1$
		dialog.setUpperListLabel(LaunchMessages.getString("Launch.common.BinariesColon")); //$NON-NLS-1$
		dialog.setLowerListLabel(LaunchMessages.getString("Launch.common.QualifierColon")); //$NON-NLS-1$
		dialog.setMultipleSelection(false);
		// dialog.set
		if (dialog.open() == Window.OK) {
			IBinary binary = (IBinary)dialog.getFirstResult();
			text.setText(binary.getResource().getProjectRelativePath().toString());
		}
	}
	
	/**
	 * A variable entry button has been pressed for the given text
	 * field. Prompt the user for a variable and enter the result
	 * in the given field.
	 */
	private void handleVariablesButtonSelected(Shell shell, Text textField) {
		String variable = getVariable(shell);
		if (variable != null) {
			textField.insert(variable);
		}
	}
	
	/**
	 * Prompts the user to choose and configure a variable and returns
	 * the resulting string, suitable to be used as an attribute.
	 */
	private String getVariable(Shell shell) {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(shell);
		dialog.open();
		return dialog.getVariableExpression();
	}
	
	// FIXME: add new presentation model for proejct, that cna return proejct directly
	protected ICProject getCProject() {
		if (getModel().getProjectModel() == null)
			return null;
		String projectName = getModel().getProjectModel().getValue().trim();
		if (projectName.isEmpty())
			return null;
		return CoreModel.getDefault().getCModel().getCProject(projectName);
	}
	
	protected IBinary[] getBinaryFiles(Shell shell, final ICProject cproject) {
		if (cproject == null || !cproject.exists()) {
			return null;
		}
		final Display display = shell.getDisplay();
		final IBinary[][] ret = new IBinary[1][];
		BusyIndicator.showWhile(display, new Runnable() {
			@Override
			public void run() {
				try {
					ret[0] = cproject.getBinaryContainer().getBinaries();
				} catch (CModelException e) {
					LaunchUIPlugin.errorDialog("Launch UI internal error", e); //$NON-NLS-1$
				}
			}
		});

		return ret[0];
	}	
}
