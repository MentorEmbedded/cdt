package org.eclipset.cdt.flatui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class FlatUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipset.cdt.flatui"; //$NON-NLS-1$

	// The shared instance
	private static FlatUIPlugin plugin;

	/**
	 * The constructor
	 */
	public FlatUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// Load the font. It's normally not good idea to do anything complicated
		// in the start method, but most code in flatui plugin depends on FontAwesome
		// being around, so we should do this before anything else can get to execute.
		// Loading of TTF font does not take long in practice.
		FA.load();

		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static FlatUIPlugin getDefault() {
		return plugin;
	}

}
