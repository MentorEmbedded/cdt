/*******************************************************************************
 * Copyright (c) 2007, 2012 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Intel Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.core.settings.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

/**
 * Representation in the project model of include file settings entries.
 * As an example, those are supplied by a gcc compiler with option "-include".
 */
public final class CIncludeFileEntry extends ACPathEntry implements ICIncludeFileEntry {
	/**
	 * This constructor is discouraged to be referenced by clients.
	 *
	 * Instead, use pooled entries with CDataUtil.createCIncludeFileEntry(name, flags).
	 *
	 * @param name - include file path. The path can be an absolute location on the local file-system
	 *    or with flag {@link #VALUE_WORKSPACE_PATH} it is treated as workspace full path.
	 * @param flags - bitwise combination of {@link ICSettingEntry} flags.
	 */
	public CIncludeFileEntry(String name, int flags) {
		super(name, flags);
	}

	/**
	 * This constructor is discouraged to be used directly.
	 *
	 * Instead, use pooled entries with CDataUtil.createCIncludeFileEntry(location.toString(), flags)
	 * or wrap it with CDataUtil.getPooledEntry(new CIncludeFileEntry(location, flags)).
	 *
	 * @param location - include file path. The path can be an absolute location on the local
	 *    file-system or with flag {@link #VALUE_WORKSPACE_PATH} it is treated as workspace full path.
	 * @param flags - bitwise combination of {@link ICSettingEntry} flags.
	 */
	public CIncludeFileEntry(IPath location, int flags) {
		super(location, flags);
	}

	/**
	 * This constructor is discouraged to be used directly.
	 *
	 * Instead, use pooled entries wrapping with CDataUtil.getPooledEntry(new CIncludeFileEntry(rc, flags)).
	 *
	 * @param rc - include file as a resource in the workspace.
	 * @param flags - bitwise combination of {@link ICSettingEntry} flags.
	 *    If {@link #VALUE_WORKSPACE_PATH} is missing it will be supplied.
	 */
	public CIncludeFileEntry(IFile rc, int flags) {
		super(rc, flags);
	}

	@Override
	public final int getKind() {
		return INCLUDE_FILE;
	}

	@Override
	public final boolean isFile() {
		return true;
	}

}
