/**********************************************************************
 * Copyright (c) 2002,2003 Rational Software Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors: 
 * IBM Rational Software - Initial API and implementation
***********************************************************************/
package org.eclipse.cdt.internal.core.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.parser.ITokenDuple;
import org.eclipse.cdt.core.parser.ast.ASTPointerOperator;
import org.eclipse.cdt.core.parser.ast.IASTArrayModifier;
import org.eclipse.cdt.core.parser.ast.IASTConstructorMemberInitializer;
import org.eclipse.cdt.core.parser.ast.IASTExceptionSpecification;
import org.eclipse.cdt.core.parser.ast.IASTExpression;
import org.eclipse.cdt.core.parser.ast.IASTInitializerClause;
import org.eclipse.cdt.core.parser.ast.IASTScope;

/**
 * @author jcamelon
 *
 */
public class Declarator implements IParameterCollection, IDeclaratorOwner, IDeclarator
{
	private static final int DEFAULT_ARRAYLIST_SIZE = 4; 
	private boolean hasFunctionTryBlock;
    private ITokenDuple pointerOperatorNameDuple;
    private ITokenDuple namedDuple;
    private boolean isFunction;
    private boolean hasFunctionBody;
    private IASTExpression constructorExpression;
    private boolean pureVirtual = false;
    private final IDeclaratorOwner owner;
	private Declarator ownedDeclarator = null; 
	private String name = "";  //$NON-NLS-1$
	private IASTInitializerClause initializerClause = null;
	private IASTExceptionSpecification exceptionSpecification = null;
	private IASTExpression bitFieldExpression = null;
	private boolean isConst = false; 
	private boolean isVolatile = false;
	private boolean isKandR = false;  

	private List ptrOps = Collections.EMPTY_LIST;
	private List parameters = Collections.EMPTY_LIST;
	private List arrayModifiers = Collections.EMPTY_LIST;
	private List constructorMemberInitializers = Collections.EMPTY_LIST;
	private int nameStartOffset, nameEndOffset;
	private boolean varArgs;
	private int nameLine; 

    public Declarator( IDeclaratorOwner owner )
	{
		this.owner = owner; 
	}
	
    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return
     */
    public int getNameEndOffset()
    {
        return nameEndOffset;
    }

    public int getNameLine()
	{
    	return nameLine;
    }
    
    /**
     * @return
     */
    public int getNameStartOffset()
    {
        return nameStartOffset;
    }

    /**
     * @return
     */
    public IDeclaratorOwner getOwner()
    {
        return owner;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name = string;
    }

    /**
     * @param i
     */
    public void setNameEndOffsetAndLineNumber(int offset, int lineNumber)
    {
        nameEndOffset = offset;
    }

    /**
     * @param i
     */
    public void setNameStartOffset(int i)
    {
        nameStartOffset = i;
    }
    
    /**
     * @return
     */
    public List getPointerOperators()
    {
        return Collections.unmodifiableList( ptrOps );
    }

	public void addPointerOperator( ASTPointerOperator ptrOp )
	{
    	if( ptrOps == Collections.EMPTY_LIST )
    		ptrOps = new ArrayList( DEFAULT_ARRAYLIST_SIZE );

		ptrOps.add( ptrOp ); 
	}
    /**
     * @return
     */
    public List getParameters()
    {
        return parameters;
    }

	public void addParameter( DeclarationWrapper param )
	{
    	if( parameters == Collections.EMPTY_LIST )
    		parameters = new ArrayList( DEFAULT_ARRAYLIST_SIZE );

		parameters.add( param );
	}
    /**
     * @return
     */
    public IASTInitializerClause getInitializerClause()
    {
        return initializerClause;
    }

    /**
     * @param expression
     */
    public void setInitializerClause(IASTInitializerClause expression)
    {
        initializerClause = expression;
    }

    /**
     * @return
     */
    public Declarator getOwnedDeclarator()
    {
        return ownedDeclarator;
    }

    /**
     * @param declarator
     */
    public void setOwnedDeclarator(Declarator declarator)
    {
        ownedDeclarator = declarator;
    }
    
    public void setName( ITokenDuple duple )
    {
		setName( duple.toString() );
		setNameStartOffset( duple.getFirstToken().getOffset());
		setNameEndOffsetAndLineNumber( duple.getLastToken().getEndOffset(), duple.getLastToken().getLineNumber());
		namedDuple = duple;
    }

    /**
     * @return
     */
    public IASTExceptionSpecification getExceptionSpecification()
    {
        return exceptionSpecification;
    }

    /**
     * @return
     */
    public boolean isConst()
    {
        return isConst;
    }

    /**
     * @return
     */
    public boolean isVolatile()
    {
        return isVolatile;
    }

    /**
     * @param specification
     */
    public void setExceptionSpecification(IASTExceptionSpecification specification)
    {
        exceptionSpecification = specification;
    }

    /**
     * @param b
     */
    public void setConst(boolean b)
    {
        isConst = b;
    }

    /**
     * @param b
     */
    public void setVolatile(boolean b)
    {
        isVolatile = b;
    }

    /**
     * @return
     */
    public boolean isKandR()
    {
        return isKandR;
    }

    /**
     * @param b
     */
    public void setKandR(boolean b)
    {
        isKandR = b;
    }

    /**
     * @param b
     */
    public void setPureVirtual(boolean b)
    {
     	pureVirtual = b;
    }

    /**
     * @return
     */
    public boolean isPureVirtual()
    {
        return pureVirtual;
    }

    /**
     * @param arrayMod
     */
    public void addArrayModifier(IASTArrayModifier arrayMod)
    {
    	if( arrayModifiers == Collections.EMPTY_LIST )
    		arrayModifiers = new ArrayList( DEFAULT_ARRAYLIST_SIZE );
		arrayModifiers.add( arrayMod );        
    }

    /**
     * @return
     */
    public List getArrayModifiers()
    {
        return arrayModifiers;
    }

    /**
     * @return
     */
    public IASTExpression getBitFieldExpression()
    {
        return bitFieldExpression;
    }

    /**
     * @param expression
     */
    public void setBitFieldExpression(IASTExpression expression)
    {
        bitFieldExpression = expression;
    }

    /**
     * @param astExpression
     */
    public void setConstructorExpression(IASTExpression astExpression)
    {
        constructorExpression = astExpression;
    }

    /**
     * @return
     */
    public IASTExpression getConstructorExpression()
    {
        return constructorExpression;
    }

    /**
     * @param initializer
     */
    public void addConstructorMemberInitializer(IASTConstructorMemberInitializer initializer)
    {
    	if( constructorMemberInitializers == Collections.EMPTY_LIST )
    		constructorMemberInitializers = new ArrayList( DEFAULT_ARRAYLIST_SIZE );

        constructorMemberInitializers.add( initializer );
    }

    /**
     * @return
     */
    public List getConstructorMemberInitializers()
    {
        return constructorMemberInitializers;
    }

    /**
     * @param b
     */
    public void hasFunctionBody(boolean b)
    {
    	hasFunctionBody = b;
    }

    /**
     * @return
     */
    public boolean isFunction()
    {
        return isFunction;
    }

    /**
     * @param b
     */
    public void setIsFunction(boolean b)
    {
        isFunction = b;
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.parser.IDeclaratorOwner#getDeclarators()
     */
    public Iterator getDeclarators()
    {
		List l = new ArrayList(); 
		if( ownedDeclarator != null )
			l.add( ownedDeclarator );
        return l.iterator();
    }

    /* (non-Javadoc)
     * @see org.eclipse.cdt.internal.core.parser.IDeclaratorOwner#getDeclarationWrapper()
     */
    public DeclarationWrapper getDeclarationWrapper()
    {
    	Declarator d = this;
    	while( d.getOwner() instanceof IDeclarator )
    		d = (Declarator)d.getOwner();
    	return (DeclarationWrapper)d.getOwner(); 
    }

	
    /**
     * @return
     */
    public ITokenDuple getNameDuple()
    {
        return namedDuple;
    }

    /**
     * @param nameDuple
     */
    public void setPointerOperatorName(ITokenDuple nameDuple)
    {
        pointerOperatorNameDuple = nameDuple; 
    }

    /**
     * @return
     */
    public ITokenDuple getPointerOperatorNameDuple()
    {
        return pointerOperatorNameDuple;
    }

    /**
     * @return
     */
    public boolean hasFunctionBody()
    {
        return hasFunctionBody;
    }

    /**
     * @param b
     */
    public void setHasFunctionBody(boolean b)
    {
        hasFunctionBody = b;
    }

    /**
     * @param b
     */
    public void setFunctionTryBlock(boolean b)
    {
        hasFunctionTryBlock = true;
    }

    /**
     * @return
     */
    public boolean hasFunctionTryBlock()
    {
        return hasFunctionTryBlock;
    }

    /**
     * @param b
     */
    public void setHasFunctionTryBlock(boolean b)
    {
        hasFunctionTryBlock = b;
    }

	/**
	 * @param b
	 */
	public void setIsVarArgs(boolean b) {
		varArgs = b;
	}

	/**
	 * @return Returns the varArgs.
	 */
	public boolean isVarArgs() {
		return varArgs;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.internal.core.parser.IDeclarator#getScope()
	 */
	public IASTScope getScope() {
		return getDeclarationWrapper().getScope();
	}

}
