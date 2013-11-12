package org.eclipse.cdt.debug.ui.dialogs;

/**
 * @since 7.4
 */
abstract public class SingleLinkUIElement extends UIElement {

	public SingleLinkUIElement(String id, UIElement parentElement, String label, String description) {
		super(id, parentElement, label, description);
	}

	@Override
	protected boolean hasContent() {
		return false;
	}
}
