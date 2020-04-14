////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CheckUtil;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * Simple utility class for all sevntu checks.
 *
 * @author Damian Szczepanik
 */
public final class SevntuUtil {

    /** Prevent instances. */
    private SevntuUtil() {
    }

    /**
     * Reports passed token as unsupported by throwing {@link IllegalArgumentException} exception.
     * This utility method if used to mark that token passed to
     * {@link AbstractCheck#visitToken(com.puppycrawl.tools.checkstyle.api.DetailAST)} is not
     * supported by this method.
     * @param token
     *        token/type that is not supported
     * @throws IllegalArgumentException
     *         always
     */
    public static void reportInvalidToken(int token) {
        throw new IllegalArgumentException("Found unsupported token: "
                + TokenUtil.getTokenName(token));
    }

    /**
     * Gets the next node of a syntactical tree (child of a current node or sibling of a current
     * node, or sibling of a parent of a current node).
     *
     * @param node
     *            Current node in considering
     * @param subTreeRoot
     *            The root node of the subtree. Can be a top level root node
     * @return Current node after bypassing, if current node reached the root of a subtree
     *     method returns null
     */
    public static DetailAST getNextSubTreeNode(DetailAST node, DetailAST subTreeRoot) {
        DetailAST toVisitAst = node.getFirstChild();
        DetailAST currentNode = node;

        while (toVisitAst == null) {
            toVisitAst = currentNode.getNextSibling();
            if (toVisitAst == null) {
                if (currentNode.getParent().equals(subTreeRoot)) {
                    break;
                }
                currentNode = currentNode.getParent();
            }
        }
        return toVisitAst;
    }

    /**
     * Obtains full type name of first token in node.
     * @param ast an AST node
     * @return fully qualified name (FQN) of type, or null if none was found
     */
    public static String getTypeNameOfFirstToken(DetailAST ast) {
        final DetailAST findFirstToken = ast.findFirstToken(TokenTypes.TYPE);
        final FullIdent ident = CheckUtil.createFullType(findFirstToken);

        return ident.getText();
    }

    /**
     * Checks node for matching class, taken both FQN and short name into account.
     *
     * @param ast
     *            an AST node
     * @param fqClassName
     *            fully qualified class name
     * @return true if type name of first token in node is fqnClassName, or its short name; false
     *         otherwise
     */
    public static boolean matchesFullyQualifiedName(DetailAST ast, String fqClassName) {
        final String typeName = getTypeNameOfFirstToken(ast);
        final int lastDotPosition = fqClassName.lastIndexOf('.');
        boolean isMatched = false;
        if (lastDotPosition == -1) {
            isMatched = typeName.equals(fqClassName);
        }
        else {
            final String shortClassName = fqClassName.substring(lastDotPosition + 1);
            isMatched = typeName.equals(fqClassName) || typeName.equals(shortClassName);
        }
        return isMatched;
    }

}
