package org.eclipse.cdt.ui.grid;

/** Base class for elements of a hierarchical presentation model.
 * 
 * One goal of this class is to support notifications. We could have used
 * Observable, but this class adds a few improvements:
 * - A list of integer flags for different kinds of changes
 * - Listener interface that uses int argument, as opposed to Object, for convenience
 *   access to those flags
 * - Calling listener automatically when it's installed, so as to simplify usage.
 * 
 * This class also provides a name, which happens to be common trait of everything.
 * 
 * @since 5.7
 */
public interface IPresentationModel {
	
	public static int VALUE_CHANGED = 1;
	public static int VISIBILITY_CHANGED = 2;
	public static int ENABLENESS_CHANGED = 4;
	public static int ACTIVATED = 64;
	
	public interface Listener {
		// Informs the client that something has changed in the model.
		// 'what' is an ORed mask of flags, which are to be defined in
		// specific classes. Value of 0 means that anything could have
		// changed.
		// FIXME: Use all-1 value for 'all changed'?
		public void changed(int what, Object object);
		
		public void childAdded(IPresentationModel parent, IPresentationModel child);
		
		public void childRemoved(IPresentationModel parent, IPresentationModel child);
		
		public void visibilityChanged(IPresentationModel model, boolean visible);
	}
	
	public class DefaultListener implements Listener {
		@Override
		public void changed(int what, Object object) {
			//throw new UnsupportedOperationException();			
		}
		
		@Override
		public void childAdded(IPresentationModel parent, IPresentationModel child) {
			//throw new UnsupportedOperationException();	
		}
		
		@Override
		public void childRemoved(IPresentationModel parent, IPresentationModel child) {
			//throw new UnsupportedOperationException();
		}
		
		@Override
		public void visibilityChanged(IPresentationModel model, boolean visible) {	
		}
	}
	
	public abstract String getId();
	
	public abstract String getName();
	
	public abstract boolean isVisible();
	
	public abstract boolean isEnabled();
	
	public abstract void addAndCallListener(Listener listener);

	public abstract void removeListener(Listener listener);
	
	// FIXME: experimental interface.
	public abstract void activate();
	
	public IPresentationModel findChild(String id);
	
}