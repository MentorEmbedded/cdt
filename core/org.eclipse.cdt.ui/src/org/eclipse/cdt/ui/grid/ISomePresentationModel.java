package org.eclipse.cdt.ui.grid;

/** ViewModel that can be shown in a grid view.
 *  
 *  Implementations of this interface describe a hierarchy of visual elements
 *  and important details like name or types, but are independent of actual
 *  visual rendering.
 *  
 *  FIXME: document how this thing can be obtained from a model. We possibly
 *  want to have a viewmodel factory passed to GridView.
 */
public interface ISomePresentationModel {
	
	public String getName();

}
