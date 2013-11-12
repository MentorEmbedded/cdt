/*******************************************************************************
 * Copyright (c) 2013 Mentor Graphics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mentor Graphics - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.debug.ui.dialogs;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

/**
 * An attribute store is responsible for storing and retrieving attribute values 
 * in the underlying storage.
 * Clients who uses {@link UIElement} hierarchy must create an attribute store wrapper
 * around their data.
 *  
 * @since 7.4
 */
public interface IAttributeStore {

	/**
	 * Returns a unique identifier for this store.
	 */
	public String getId();

	/**
	 * Returns the boolean-valued attribute with the given name.  
	 * Returns the given default value if the attribute is undefined.
	 *
	 * @param attributeName the name of the attribute
	 * @param defaultValue the value to use if no value is found
	 * @return the value or the default value if no value was found.
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li>An exception occurs while retrieving the attribute from
	 *  underlying storage.</li>
	 * <li>An attribute with the given name exists, but does not
	 *  have a boolean value</li>
	 * </ul>
	 */
	public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException;
	
	/**
	 * Returns the integer-valued attribute with the given name.  
	 * Returns the given default value if the attribute is undefined.
	 *
	 * @param attributeName the name of the attribute
	 * @param defaultValue the value to use if no value is found
	 * @return the value or the default value if no value was found.
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li>An exception occurs while retrieving the attribute from
	 *  underlying storage.</li>
	 * <li>An attribute with the given name exists, but does not
	 *  have an integer value</li>
	 * </ul>
	 */
	public int getAttribute(String attributeName, int defaultValue) throws CoreException;
	
	/**
	 * Returns the <code>java.util.List</code>-valued attribute with the given name.  
	 * Returns the given default value if the attribute is undefined.
	 *
	 * @param attributeName the name of the attribute
	 * @param defaultValue the value to use if no value is found
	 * @return the value or the default value if no value was found.
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li>An exception occurs while retrieving the attribute from
	 *  underlying storage.</li>
	 * <li>An attribute with the given name exists, but does not
	 *  have a List value</li>
	 * </ul>
	 */
	public List<?> getAttribute(String attributeName, List<?> defaultValue) throws CoreException;
	
	/**
	 * Returns the <code>java.util.Set</code>-valued attribute with the given name.  
	 * Returns the given default value if the attribute is undefined.
	 *
	 * @param attributeName the name of the attribute
	 * @param defaultValue the value to use if no value is found
	 * @return the value or the default value if no value was found.
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li>An exception occurs while retrieving the attribute from
	 *  underlying storage.</li>
	 * <li>An attribute with the given name exists, but does not
	 *  have a List value</li>
	 * </ul>
	 */
	public Set<?> getAttribute(String attributeName, Set<?> defaultValue) throws CoreException;
	
	/**
	 * Returns the <code>java.util.Map</code>-valued attribute with the given name.  
	 * Returns the given default value if the attribute is undefined.
	 *
	 * @param attributeName the name of the attribute
	 * @param defaultValue the value to use if no value is found
	 * @return the value or the default value if no value was found.
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li>An exception occurs while retrieving the attribute from
	 *  underlying storage.</li>
	 * <li>An attribute with the given name exists, but does not
	 *  have a Map value</li>
	 * </ul>
	 */
	public Map<?, ?> getAttribute(String attributeName, Map<?, ?> defaultValue) throws CoreException;
	
	/**
	 * Returns the string-valued attribute with the given name.  
	 * Returns the given default value if the attribute is undefined.
	 *
	 * @param attributeName the name of the attribute
	 * @param defaultValue the value to use if no value is found
	 * @return the value or the default value if no value was found.
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li>An exception occurs while retrieving the attribute from
	 *  underlying storage.</li>
	 * <li>An attribute with the given name exists, but does not
	 *  have a String value</li>
	 * </ul>
	 */
	public String getAttribute(String attributeName, String defaultValue) throws CoreException;
	
	/**
	 * Returns a map containing the attributes in this store.
	 * Returns an empty map if this store has no attributes.
	 * 
	 * @return a map of attribute keys and values
	 * @exception CoreException unable to generate/retrieve an attribute map
	 */
	public Map<String, ?> getAttributes() throws CoreException;

	/**
	 * Returns whether the content of this store has been modified
	 * since it was last saved or created.
	 * 
	 * @return whether the content of this store has been modified
	 *  since it was last saved or created
	 */
	public boolean isDirty();
	
	/**
	 * Sets the integer-valued attribute with the given name.  
	 *
	 * @param attributeName the name of the attribute, cannot be <code>null</code>
	 * @param value the value
	 */
	public void setAttribute(String attributeName, int value);
	
	/**
	 * Sets the String-valued attribute with the given name.
	 * If the value is <code>null</code>, the attribute is removed from
	 * this store.
	 *
	 * @param attributeName the name of the attribute, cannot be <code>null</code>
	 * @param value the value, or <code>null</code> if the attribute is to be undefined
	 */
	public void setAttribute(String attributeName, String value);
	
	/**
	 * Sets the <code>java.util.List</code>-valued attribute with the given name.
	 * The specified List <em>must</em> contain only String-valued entries.
	 * If the value is <code>null</code>, the attribute is removed from
	 * this store.
	 *
	 * @param attributeName the name of the attribute, cannot be <code>null</code>
	 * @param value the value, or <code>null</code> if the attribute is to be undefined
	 */
	public void setAttribute(String attributeName, List<?> value);
	
	/**
	 * Sets the <code>java.util.Map</code>-valued attribute with the given name.
	 * The specified Map <em>must</em> contain only String keys and String values.
	 * If the value is <code>null</code>, the attribute is removed from
	 * this store.
	 *
	 * @param attributeName the name of the attribute, cannot be <code>null</code>
	 * @param value the value, or <code>null</code> if the attribute is to be undefined
	 */
	public void setAttribute(String attributeName, Map<?, ?> value);
	
	/**
	 * Sets the <code>java.util.Set</code>-valued attribute with the given name.
	 * The specified Set <em>must</em> contain only String values.
	 * If the value is <code>null</code>, the attribute is removed from
	 * this store.
	 *
	 * @param attributeName the name of the attribute, cannot be <code>null</code>
	 * @param value the value, or <code>null</code> if the attribute is to be undefined
	 */
	public void setAttribute(String attributeName, Set<?> value);
	
	/**
	 * Sets the boolean-valued attribute with the given name.  
	 *
	 * @param attributeName the name of the attribute, cannot be <code>null</code>
	 * @param value the value
	 */
	public void setAttribute(String attributeName, boolean value);	
		
	/**
	 * Sets the attributes of this store to be the ones contained
	 * in the given map. The values must be an instance of one of the following
	 * classes: <code>String</code>, <code>Integer</code>, or
	 * <code>Boolean</code>, <code>List</code>, <code>Map</code>. Attributes
	 * previously set on this store but not included in the given
	 * map are considered to be removals. Setting the given map to be
	 * <code>null</code> is equivalent to removing all attributes.
	 *
	 * @param attributes a map of attribute names to attribute values.
	 *  Attribute names are not allowed to be <code>null</code>
	 */
	public void setAttributes(Map<String, ?> attributes);
}
