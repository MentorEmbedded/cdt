package org.eclipse.cdt.debug.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.debug.internal.ui.CDebugImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Widget;

/**
 * @since 7.4
 */
public abstract class UIElement {

	private String fId;
	private String fLabel;
	private String fDescription;
	private String fErrorMessage;
	private UIElement fParentElement;
	private boolean fShowDetails = false;
	private List<UIElement> fChildren = new ArrayList<UIElement>();
	
	private ILinkListener fLinkListener;
	private IChangeListener fChangeListener;
	private IStatusListener fStatusListener;
	
	private Composite fDetailsContent;
	private Set<Widget> fSummaryWidgets = new HashSet<Widget>();
	
	private boolean fInitializing = false;

	public UIElement(String id, UIElement parentElement, String label, String description) {
		fId = id;
		fLabel = label;
		fDescription = description;
		fParentElement = parentElement;
	}

	public String getId() {
		return fId;
	}

	public String getLabel() {
		return fLabel;
	}

	public String getDescription() {
		return fDescription;
	}

	public UIElement getParent() {
		return fParentElement;
	}

	public UIElement find(String id) {
		if (id.equals(getId())) {
			return this;
		}
		for (UIElement el : getChildren()) {
			if (el.getId().equals(id)) {
				return el;
			}
			UIElement el1 = el.find(id);
			if (el1 != null) {
				return el1;
			}
		}
		return null;
	}

	protected UIElement[] getChildren() {
		return fChildren.toArray(new UIElement[fChildren.size()]);
	}

	protected void createChildren(IAttributeStore store) {
		removeAllChildren();
		doCreateChildren(store);
		for (UIElement child : getChildren()) {
			child.createChildren(store);
		}
	}

	protected void doCreateChildren(IAttributeStore store) {
	}

	public void addChildren(UIElement[] children) {
		fChildren.addAll(Arrays.asList(children));
		for (UIElement child : children) {
			child.setLinkListener(fLinkListener);
			child.setChangeListener(fChangeListener);
			child.setStatusListener(fStatusListener);
		}
	}

	public void insertChild(UIElement child, int index) {
		if (index < 0) {
			return;
		}
		if (index >= fChildren.size()) {
			fChildren.add(child);
		}
		else {
			fChildren.add(index, child);
		}
	}

	public void removeChild(UIElement child) {
		fChildren.remove(child);
	}

	public void removeAllChildren() {
		for (UIElement el : getChildren()) {
			el.dispose();
		}
		fChildren.clear();
	}

	public void createContent(Composite parent, IAttributeStore store) {
		disposeContent();
		if (fShowDetails) {
			createDetailsContent(parent, store);
			for (UIElement el : getChildren()) {
				el.initializeFrom(store, parent);
			}
		}
		else {
			createSummaryContent(parent, store);
		}
	}

	public void dispose() {		
		disposeContent();

		fLinkListener = null;
		fChangeListener = null;
		fStatusListener = null;

		for (UIElement child : fChildren) {
			child.dispose();
		}
		fChildren.clear();
	}

	public void disposeContent() {
		if (fDetailsContent != null) {
			fDetailsContent.dispose();
			fDetailsContent = null;
		}
		disposeSummaryContent();
	}

	private Composite createSummaryContent(Composite parent, IAttributeStore store) {		
		Composite base = parent;

		Link link = new Link(base, SWT.NONE);
		link.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, hasContent() ? 1 : 4, 1));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyLinkListener();
			}
		});
		link.setText(String.format("<a>%s</a>", getLabel())); //$NON-NLS-1$
		link.setToolTipText(getDescription());
		fSummaryWidgets.add(link);

		if (hasContent()) {
			if (numberOfRowsInSummary() > 1) {
				fSummaryWidgets.add(GridUtils.createBar(base, 1));
			}
			Composite content = new Composite(base, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = layout.marginWidth = 0;
			content.setLayout(layout);
			int horSpan = 1;
			if (numberOfRowsInSummary() <= 1) {
				++horSpan;
			}
			if (!isRemovable()) {
				++horSpan;
			}
			content.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, horSpan, 1));
			fSummaryWidgets.add(content);
			if (isRemovable()) {
				Button removeButton = new Button(base, SWT.PUSH);
				removeButton.setImage(CDebugImages.get(CDebugImages.IMG_LCL_REMOVE_UIELEMENT));
				removeButton.setToolTipText("Remove");
				removeButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				removeButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						getChangeListener().elementRemoved(UIElement.this);;
					}
				});
				fSummaryWidgets.add(removeButton);
			}
			
			doCreateSummaryContent(content, store);
		}
		
		fSummaryWidgets.add(GridUtils.addHorizontalSeparatorToGrid(parent, 4));
		
		return base;
	}

	
	/**
	 * Creates the composite with four columns for details widgets. 
	 * The content will be populated with widgets when "initialzeFrom" 
	 * is called for each child. 
	 */
	protected Composite createDetailsContent(Composite parent, IAttributeStore store) {
		fDetailsContent = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		fDetailsContent.setLayout(layout);
		int horSpan = (parent.getLayout() instanceof GridLayout) ? ((GridLayout)parent.getLayout()).numColumns : 1;
		fDetailsContent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, horSpan, 1));
		return fDetailsContent;
	}
	
	protected boolean isRemovable() {
		return false;
	}

	protected boolean hasContent() {
		return true;
	}

	protected int numberOfRowsInSummary() {
		return 1;
	}

	protected void doCreateSummaryContent(Composite parent, IAttributeStore store) {
	}

	protected void setLinkListener(ILinkListener l) {
		fLinkListener = l;
	}

	protected void setChangeListener(IChangeListener l) {
		fChangeListener = l;
	}

	protected void setStatusListener(IStatusListener l) {
		fStatusListener = l;
	}
	
	private void notifyLinkListener() {
		if (fLinkListener != null) {
			fLinkListener.linkActivated(this);
		}
	}

	public void setShowDetails(boolean show) {
		fShowDetails = show;
	}

	public boolean showDetails() {
		return fShowDetails;
	}

	protected ILinkListener getLinkListener() {
		return fLinkListener;
	}

	protected IChangeListener getChangeListener() {
		return fChangeListener;
	}

	protected IStatusListener getStatusListener() {
		return fStatusListener;
	}

	public void initializeFrom(IAttributeStore store, Composite parent) {
		fInitializing = true;
		setErrorMessage(null);
		createChildren(store);
		createContent(parent, store);
		doInitializeFrom(store);
		fInitializing = false;
	}

	protected void doInitializeFrom(IAttributeStore store) {
		if (showDetails()) {
			initializeDetailsContent(store);
		}
		else {
			initializeSummaryContent(store);
		}
	}

	public void performApply(IAttributeStore store) {
		for (UIElement el : getChildren()) {
			el.performApply(store);
		}
		doPerformApply(store);
	}

	public void setDefaults(IAttributeStore store) {
		for (UIElement el : getChildren()) {
			el.setDefaults(store);
		}
		doSetDefaults(store);
	}

	public void refresh(IAttributeStore store, Composite parent) {
		setErrorMessage(null);
		performApply(store);
		disposeSummaryContent();
		removeAllChildren();
		createChildren(store);
		for (UIElement el : getChildren()) {
			el.initializeFrom(store, parent);
		}
	}

	protected void doPerformApply(IAttributeStore store) {
	}

	protected void doSetDefaults(IAttributeStore store) {
	}

	protected void initializeSummaryContent(IAttributeStore store) {		
	}

	protected void initializeDetailsContent(IAttributeStore store) {		
	}

	protected void remove(IAttributeStore store) {
		for (UIElement child : getChildren()) {
			child.remove(store);
		}
	}

	public boolean isValid(IAttributeStore store) {
		if (!isContentValid(store)) {
			return false;
		}
		for (UIElement el : getChildren()) {
			if (!el.isValid(store)) {
				return false;
			}
		}
		return true;
	}
	
	protected boolean isContentValid(IAttributeStore store) {
		return true;
	}
	
	protected boolean isInitializing() {
		return fInitializing;
	}
	
	protected void disposeSummaryContent() {
		for (Widget w : fSummaryWidgets) {
			w.dispose();
		}
		fSummaryWidgets.clear();
	}

	public String getErrorMessage() {
		String error = fErrorMessage;
		if (error == null) {
			for (UIElement el : getChildren()) {
				String err = el.getErrorMessage();
				if (err != null) {
					error = err;
					break;
				}
			}
		}
		return (error != null) ? String.format("%s/%s", getLabel(), error) : null; //$NON-NLS-1$
	}

	protected void setErrorMessage(String message) {
		fErrorMessage = message;
	}
}
