/* This file is part of SableCC ( http://sablecc.org ).
 *
 * See the NOTICE file distributed with this work for copyright information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sablecc.sablecc.core;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.errormessage.*;
import org.sablecc.sablecc.core.interfaces.*;
import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.syntax3.node.*;

public class SemanticException
        extends CompilerException {

    private Token location;

    private SemanticException(
            String message,
            Token location) {

        super(message);

        if (location == null) {
            throw new InternalException("location may not be null");
        }

        this.location = location;
    }

    public static SemanticException duplicateDeclaration(
            INameDeclaration duplicateNameDeclaration,
            INameDeclaration olderNameDeclaration) {

        String name = duplicateNameDeclaration.getName();
        if (!name.equals(olderNameDeclaration.getName())) {
            throw new InternalException("names must be identical");
        }

        TIdentifier duplicateIdentifier = duplicateNameDeclaration
                .getNameIdentifier();
        TIdentifier olderIdentifier = olderNameDeclaration.getNameIdentifier();

        return new SemanticException(new MDuplicateDeclaration(name,
                duplicateNameDeclaration.getNameType(),
                duplicateIdentifier.getLine() + "",
                duplicateIdentifier.getPos() + "",
                olderNameDeclaration.getNameType(), olderIdentifier.getLine()
                        + "", olderIdentifier.getPos() + "").toString(),
                duplicateIdentifier);
    }

    public static SemanticException duplicateAlternativeName(
            TreeAlternative duplicateAlternative,
            TreeAlternative olderAlternative) {

        String name = duplicateAlternative.getName();
        if (!name.equals(olderAlternative.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateAlternative.getNameToken();
        Token olderToken = olderAlternative.getNameToken();

        return new SemanticException(
                new MDuplicateAlternativeName(name, duplicateAlternative
                        .getProduction().getName(), duplicateToken.getLine()
                        + "", duplicateToken.getPos() + "", olderToken
                        .getLine() + "", olderToken.getPos() + "").toString(),
                duplicateToken);
    }

    public static SemanticException duplicateAlternativeName(
            ParserAlternative duplicateAlternative,
            ParserAlternative olderAlternative) {

        String name = duplicateAlternative.getName();
        if (!name.equals(olderAlternative.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateAlternative.getNameToken();
        Token olderToken = olderAlternative.getNameToken();

        return new SemanticException(
                new MDuplicateAlternativeName(name, duplicateAlternative
                        .getProduction().getName(), duplicateToken.getLine()
                        + "", duplicateToken.getPos() + "", olderToken
                        .getLine() + "", olderToken.getPos() + "").toString(),
                duplicateToken);
    }

    public static SemanticException duplicateElementName(
            TreeElement duplicateElement,
            TreeElement olderElement) {

        String name = duplicateElement.getName();

        if (!name.equals(olderElement.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateElement.getNameToken();
        Token olderToken = olderElement.getNameToken();

        return new SemanticException(
                new MDuplicateElementName(name, duplicateElement
                        .getAlternative().getProduction().getName(),
                        duplicateElement.getAlternative().getIndex() + "",
                        duplicateToken.getLine() + "", duplicateToken.getPos()
                                + "", olderToken.getLine() + "",
                        olderToken.getPos() + "").toString(), duplicateToken);
    }

    public static SemanticException duplicateElementName(
            ParserElement duplicateElement,
            ParserElement olderElement) {

        String name = duplicateElement.getName();

        if (!name.equals(olderElement.getName())) {
            throw new InternalException("names must be identical");
        }

        Token duplicateToken = duplicateElement.getNameToken();
        Token olderToken = olderElement.getNameToken();

        return new SemanticException(
                new MDuplicateElementName(name, duplicateElement
                        .getAlternative().getProduction().getName(),
                        duplicateElement.getAlternative().getIndex() + "",
                        duplicateToken.getLine() + "", duplicateToken.getPos()
                                + "", olderToken.getLine() + "",
                        olderToken.getPos() + "").toString(), duplicateToken);
    }

    public static SemanticException spuriousParserNamedContextDeclaration(
            AParserContext declaration,
            Context.NamedContext namedContext) {

        String name = declaration.getName().getText();
        if (!name.equals(namedContext.getName())) {
            throw new InternalException("names must be identical");
        }

        TIdentifier duplicateIdentifier = declaration.getName();
        TIdentifier olderIdentifier = namedContext.getParserDeclaration()
                .getName();

        return new SemanticException(
                new MDuplicateDeclaration(name, "context",
                        duplicateIdentifier.getLine() + "",
                        duplicateIdentifier.getPos() + "", "context",
                        olderIdentifier.getLine() + "",
                        olderIdentifier.getPos() + "").toString(),
                duplicateIdentifier);
    }

}
