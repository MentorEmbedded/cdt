package org.eclipse.cdt.ui.grid;

import java.util.Collections;
import java.util.List;

/**
 * @since 6.0
 */
public class ListPresentationModel extends CompositePresentationModel {
	private boolean canReorder = false;
	private boolean canDelete = false;

	public ListPresentationModel() {
		super();
	}

	public ListPresentationModel(String name) {
		super(name);
	}
	
	
	
	public boolean canReorder() {
		return canReorder;
	}

	public void setCanReorder(boolean canReorder) {
		this.canReorder = canReorder;
	}

	public boolean canDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	
	public void moveUp(IPresentationModel child) {
		List<IPresentationModel> children2 = getChildren();
		int childIndex = children2.indexOf(child);
		if (childIndex > 0) {
			Collections.swap(children2, childIndex, childIndex-1);
			// TODO notify listeners
		}
		
	}
	
	public void moveDown(IPresentationModel child) {
		List<IPresentationModel> children2 = getChildren();
		int childIndex = children2.indexOf(child);
		if (childIndex >= 0 && childIndex < children2.size()-1) {
			Collections.swap(children2, childIndex, childIndex+1);
			// TODO notify listeners
		}
		
	}
	
	public void moveTo(IPresentationModel child, int index) {
		List<IPresentationModel> children2 = getChildren();
		int childIndex = children2.indexOf(child);
		if (childIndex >= 0 && index >= 0 && index < children2.size() && childIndex != index) {
			// TODO
		}
	}
}
