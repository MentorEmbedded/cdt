package org.eclipse.cdt.dsf.gdb.internal.ui.newlaunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.ui.dialogs.IAttributeStore;
import org.eclipse.cdt.debug.ui.dialogs.UIElement;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @since 7.4
 */
public class DebuggerUIElement extends UIElement {

	final private static String ELEMENT_ID = ".debugger"; //$NON-NLS-1$

	// Summary view widgets
	private StyledText fSummaryText;

	// Details view widgets
	private Combo fTypeCombo;
	private Button fAttachButton;

	private String[] fTypes = new String[SessionType.values().length]; 

	public DebuggerUIElement(UIElement parentElement) {
		super(parentElement.getId() + ELEMENT_ID, parentElement, "Debugger", "Debugger settings");
		for (int i = 0; i < fTypes.length; ++i) {
			if (SessionType.values()[i].equals(SessionType.LOCAL)) {
				fTypes[i] = "locally";
			}
			else if (SessionType.values()[i].equals(SessionType.REMOTE)) {
				fTypes[i] = "using gdbserver";
			}
			if (SessionType.values()[i].equals(SessionType.CORE)) {
				fTypes[i] = "core file";
			}
		}
	}

	@Override
	protected void doCreateChildren(IAttributeStore store) {
		try {
			SessionType sessionType = getSessionType(store);
			List<UIElement> children = new ArrayList<UIElement>();
			children.add(new DebuggerOptionsElement(this));
			if (sessionType != SessionType.CORE) {
				children.add(new SharedLibrariesElement(this));
			}
			if (sessionType == SessionType.REMOTE) {
				children.add(new ConnectionElement(this));
			}
			addChildren(children.toArray(new UIElement[children.size()]));
		}
		catch(CoreException e) {
			getStatusListener().errorReported(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doCreateSummaryContent(Composite parent, IAttributeStore store) {
		Composite base = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		base.setLayout(layout);
		base.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		fSummaryText = new StyledText(base, SWT.NONE);
		fSummaryText.setBackground(fSummaryText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	}

	@Override
	protected Composite createDetailsContent(Composite parent, IAttributeStore store) {
		Composite comp = super.createDetailsContent(parent, store);
		Composite base = new Composite(comp, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 0;
		base.setLayout(layout);
		base.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Composite comboComp = new Composite(base, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 0;
		comboComp.setLayout(layout);
		comboComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label label = new Label(comboComp, SWT.NONE);
		label.setText("Debug ");
		
		fTypeCombo = new Combo(comboComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		fTypeCombo.setItems(fTypes);
		fTypeCombo.select(0);
		
		fTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sessionTypeChanged();
			}
		});

		fAttachButton = new Button(base, SWT.CHECK);
		fAttachButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		fAttachButton.setText("Attach to process");

		return comp;
	}

	@Override
	protected void initializeSummaryContent(IAttributeStore store) {
		if (store == null || fSummaryText == null) {
			return;
		}
		try {
			SessionType sessionType = getSessionType(store);
			String text = ""; //$NON-NLS-1$
			if (sessionType == SessionType.LOCAL) {
				text = "local application";
			}
			else if (sessionType == SessionType.REMOTE) {
				text = "using gdbserver";
			}
			if (sessionType == SessionType.CORE) {
				text = "core file";
			}
			fSummaryText.setText(String.format("Debug %s", text));
		}
		catch(CoreException e) {
			getStatusListener().errorReported(e.getLocalizedMessage());
		}			
	}

	@Override
	protected void initializeDetailsContent(IAttributeStore store) {
		if (fTypeCombo != null) {
			try {
				SessionType sessionType = getSessionType(store);
				fTypeCombo.select(sessionType.ordinal());
			}
			catch(CoreException e) {
				getStatusListener().errorReported(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void disposeContent() {
		super.disposeContent();
		fSummaryText = null;
		fTypeCombo = null;
		fAttachButton = null;
	}

	@Override
	protected void doPerformApply(IAttributeStore store) {
		super.doPerformApply(store);
		if (fTypeCombo != null) {
			SessionType type = SessionType.values()[fTypeCombo.getSelectionIndex()];
			String attrValue = ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN;
			if (type == SessionType.LOCAL) {
				attrValue = ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN;
			}
			else if (type == SessionType.CORE) {
				attrValue = ICDTLaunchConfigurationConstants.DEBUGGER_MODE_CORE;
			}
			else if (type == SessionType.REMOTE) {
				attrValue = IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE;
			}
			store.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE, attrValue);				
		}
	}

	private void sessionTypeChanged() {
		getChangeListener().elementChanged(this);
	}

	protected SessionType getSessionType(IAttributeStore store) throws CoreException {
		String debugMode = store.getAttribute(
			ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE, 
			ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);
		if (debugMode.equals(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN)) {
			return SessionType.LOCAL;
		} 
		else if (debugMode.equals(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_ATTACH)) {
			return SessionType.LOCAL;
		} 
		else if (debugMode.equals(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_CORE)) {
			return SessionType.CORE;
		} 
		else if (debugMode.equals(IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE)) {
			return SessionType.REMOTE;
		} 
		else if (debugMode.equals(IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE_ATTACH)) {
		    return SessionType.REMOTE;
	    } 
		else {
	    	assert false : "Unexpected session-type attribute in launch config: " + debugMode;  //$NON-NLS-1$
	    }
    	return SessionType.LOCAL;
	}
}
