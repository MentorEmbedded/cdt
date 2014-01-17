/*******************************************************************************
 * Copyright (c) 2011 Mentor Graphics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mentor Graphics - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.dsf.gdb.internal.ui;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @since 4.1
 */
public interface IGdbUIConstants {

	/**
	 * Plug-in identifier (value <code>"org.eclipse.cdt.dsf.gdb.ui"</code>).
	 */
	public static final String PLUGIN_ID = GdbUIPlugin.PLUGIN_ID; 
	
	/**  image identifier. */
	public static final String IMG_WIZBAN_ADVANCED_TIMEOUT_SETTINGS = PLUGIN_ID + ".imageAdvancedTimeoutSettings"; //$NON-NLS-1$
	public static final String IMG_OBJ_BROWSE = PLUGIN_ID + ".browse";	//$NON-NLS-1$
	public static final String IMG_OBJ_WORKSPACE = PLUGIN_ID + ".workspace";	//$NON-NLS-1$
	public static final String IMG_OBJ_SEARCH_PROJECT = PLUGIN_ID + ".searchProject";	//$NON-NLS-1$
	public static final String IMG_OBJ_PATH_VARIABLES = PLUGIN_ID + ".pathVariables";	//$NON-NLS-1$
	public static final String IMG_OBJ_NEW_EXECUTABLE = PLUGIN_ID + ".addExecutable";	//$NON-NLS-1$
	public static final String IMG_OBJ_NEW_ATTACH = PLUGIN_ID + ".newAttach";	//$NON-NLS-1$
}
