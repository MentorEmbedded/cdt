/*******************************************************************************
 * Copyright (c) 2008, 2011 Institute for Software, HSR Hochschule fuer Technik  
 * Rapperswil, University of applied sciences and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *  
 * Contributors: 
 * Institute for Software - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.refactoring.utils;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.Keywords;
import org.eclipse.cdt.core.parser.util.CharArrayIntMap;

import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;

import org.eclipse.cdt.internal.ui.refactoring.RefactoringASTCache;

/**
 * Helps with IASTNames.
 * 
 * @author Mirko Stocker
 * 
 */
public class NameHelper {
	private static final String localVariableRegexp = "[a-z_A-Z]\\w*"; //$NON-NLS-1$

	public static boolean isValidLocalVariableName(String name) {
		boolean valid = Pattern.compile(localVariableRegexp).matcher(name).matches();
		return valid;
	}
	
	public static boolean isKeyword(String name) {
		CharArrayIntMap keywords = new CharArrayIntMap(0, -1);
		Keywords.addKeywordsC(keywords);
		Keywords.addKeywordsCpp(keywords);
		Keywords.addKeywordsPreprocessor(keywords);
		return keywords.containsKey(name.toCharArray());
	}
	
	/**
	 * Constructs the fully qualified name from the given parameters. The file and offset parameters are used to determine
	 * the namespace at the declaration position and the target namespace at the target position.
	 * 
	 * @param declaratorName of the method or function
	 * @param declarationTu translation unit of the method or function declaration
	 * @param insertFileTu translation unit of the file where the implementation is being inserted
	 * @param selectionOffset the offset in the declarationFile, usually the position or selection of the declaration
	 * @param insertLocation 
	 * @return the correct name for the target
	 * @throws CoreException 
	 */
	public static ICPPASTQualifiedName createQualifiedNameFor(IASTName declaratorName, ITranslationUnit declarationTu, int selectionOffset, ITranslationUnit insertFileTu, int insertLocation, RefactoringASTCache astCache) 
			throws CoreException {
		ICPPASTQualifiedName qname = new CPPASTQualifiedName();
		
		IASTName[] declarationNames = NamespaceHelper.getSurroundingNamespace(declarationTu, selectionOffset, astCache).getNames();
		IASTName[] implementationNames = NamespaceHelper.getSurroundingNamespace(insertFileTu, insertLocation, astCache).getNames();
		
		for (int i = 0; i < declarationNames.length; i++) {
			if (i >= implementationNames.length) {
				qname.addName(declarationNames[i]);
			} else if (!String.valueOf(declarationNames[i].toCharArray()).equals(String.valueOf(implementationNames[i].toCharArray()))) {
				qname.addName(declarationNames[i]);
			}
		}

		qname.addName(declaratorName.copy(CopyStyle.withLocations));
		return qname;
	}
	
	/**
	 * Returns the trimmed field name. Leading and trailing non-letters-digits are trimmed.
	 * If the first letter-digit is in lower case and the next is in upper case, 
	 * the first letter is trimmed.
	 * 
	 * @param fieldName Complete, unmodified name of the field to trim
	 * @return Trimmed field
	 */
	public static String trimFieldName(String fieldName){
		char[] letters = fieldName.toCharArray();
		int start = 0;
		int end = letters.length - 1;
		try {
			// Trim, non-letters at the beginning
			while (!Character.isLetterOrDigit(letters[start]) && start < end) {
				++start;
			}
			
			// If the next character is not a letter or digit, 
			// look ahead because the first letter might not be needed
			if (start + 1 <= end
					&& !Character.isLetterOrDigit(letters[start + 1])) {
				int lookAhead = 1;
				while (start + lookAhead <= end) {
					// Only change the start if something is found after the non-letters
					if (Character.isLetterOrDigit(letters[start + lookAhead])) {
						start += lookAhead;
						break;
					}
					lookAhead++;
				}
				
			}
			// Sometimes, a one letter lower case prefix is used to add some info
			// Example: mMyMember, sMyStatic
			// Trim the first letter
			else if (!Character.isUpperCase(letters[start]) && start + 1 <= end && Character.isUpperCase(letters[start + 1])) {
				start++;
			}
			
			// Trim, non-letters at the end
			while ((!Character.isLetter(letters[end]) && !Character.isDigit(letters[end])) && start < end) {
				--end;
			}
		} catch (IndexOutOfBoundsException e) {
		}	
		
		return new String(letters, start, end - start + 1);
	}
	
	public static String makeFirstCharUpper(String name) {
		if (Character.isLowerCase(name.charAt(0))){
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		}
		return name;
	} 

	public static String getTypeName(IASTParameterDeclaration parameter) {
		IASTName name = parameter.getDeclarator().getName();
		IBinding binding = name.resolveBinding();
		if (binding instanceof IVariable) {
			IType type = ((IVariable) binding).getType();
			if (type != null) {
				return ASTTypeUtil.getType(type);
			}
		}
		return ""; //$NON-NLS-1$
	}
}
