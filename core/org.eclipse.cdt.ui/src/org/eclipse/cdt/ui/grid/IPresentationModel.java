package org.eclipse.cdt.ui.grid;

/* Base class for elements of a hierarchical presentation model.
 * 
 * One goal of this class is to support notifications. We could have used
 * Observable, but this class adds a few improvements:
 * - A list of integer flags for different kinds of changes
 * - Listener interface that uses int argument, as opposed to Object, for convenience
 *   access to those flags
 * - Calling listener automatically when it's installed, so as to simplify usage.
 * 
 * This class also provides a name, which happens to be common trait of everything.
 */
public interface IPresentationModel {
	
	public static int CHANGED = 1;
	public static int CHILD_ADDED = 2;
	public static int CHILD_REMOVED = 4;
	public static int CHILD_CHANGED = 8;	
	
	public interface Listener {
		// Informs the client that something has changed in the model.
		// 'what' is an ORed mask of flags, which are to be defined in
		// specific classes. Value of 0 means that anything could have
		// changed.
		// FIXME: Use all-1 value for 'all changed'?
		public void changed(int what, Object object);
	}
	
	public abstract String getName();
	
	public abstract void addAndCallListener(Listener listener);

	public abstract void removeListener(Listener listener);
}