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

package org.sablecc.sablecc.exception;

import org.sablecc.exception.*;

@SuppressWarnings("serial")
public abstract class CompilerException
        extends RuntimeException {

    protected CompilerException(
            String message) {

        super(message);

        if (message == null) {
            throw new InternalException("message may not be null");
        }
    }
/*
    public static CompilerException duplicateDeclaration(
            NameDeclaration duplicateNameDeclaration,
            NameDeclaration olderNameDeclaration) {

        String name = duplicateNameDeclaration.getName();
        if (!name.equals(olderNameDeclaration.getName())) {
            throw new InternalException("names must be identical");
        }

        TIdentifier duplicateIdentifier = duplicateNameDeclaration
                .getNameIdentifier();
        TIdentifier olderIdentifier = olderNameDeclaration.getNameIdentifier();

        return new CompilerException(new MDuplicateDeclaration(name,
                duplicateNameDeclaration.getNameType(),
                duplicateIdentifier.getLine() + "",
                duplicateIdentifier.getPos() + "",
                olderNameDeclaration.getNameType(), olderIdentifier.getLine()
                        + "", olderIdentifier.getPos() + "").toString());
    }

    public static CompilerException invalidInterval(
            TTwoDots twoDots,
            Token from,
            Token to) {

        return new CompilerException(
                new MInvalidInterval(twoDots.getLine() + "", twoDots.getPos()
                        + "", from.getText(), to.getText()).toString());
    }

    public static CompilerException undefinedReference(
            TIdentifier identifier) {

        return new CompilerException(new MUndefinedReference(
                identifier.getText(), identifier.getLine() + "",
                identifier.getPos() + "").toString());
    }

    public static CompilerException invalidReference(
            TIdentifier token) {

        return new CompilerException(new MInvalidReference(token.getText(),
                token.getLine() + "", token.getPos() + "").toString());
    }

    public static CompilerException cyclicReference(
            TIdentifier reference,
            TIdentifier context) {

        return new CompilerException(new MCyclicReference(reference.getText(),
                reference.getLine() + "", reference.getPos() + "",
                context.getText(), context.getLine() + "", context.getPos()
                        + "").toString());
    }

    public static CompilerException invalidPriority(
            TGt gt,
            MatchedToken matchedToken) {

        return new CompilerException(new MInvalidPriority(gt.getLine() + "", gt
                .getPos()
                + "", matchedToken.getName()).toString());
    }

    public static CompilerException conflictingPriorities(
            TGt gt,
            MatchedToken high,
            MatchedToken low,
            TGt prior_gt) {

        return new CompilerException(new MConflictingPriorities(gt.getLine()
                + "", gt.getPos() + "", high.getName(), low.getName(), prior_gt
                .getLine()
                + "", prior_gt.getPos() + "").toString());
    }

    public static CompilerException notAToken(
            Token token) {

        return new CompilerException(new MNotAToken(token.getLine() + "",
                token.getPos() + "", token.getText()).toString());
    }

    public static CompilerException lexerConflict(
            MatchedToken matchedToken1,
            MatchedToken matchedToken2) {

        return new CompilerException(new MLexerConflict(
                matchedToken1.getName(), matchedToken2.getName()).toString());
    }

    public static CompilerException parserUselessProduction(
            String name) {

        return new CompilerException(
                new MParserUselessProduction(name).toString());
    }

    public static CompilerException parserSpuriousPriority(
            TIdentifier identifier) {

        return new CompilerException(new MParserSpuriousPriority(
                identifier.getText(), "" + identifier.getLine(), ""
                        + identifier.getPos()).toString());
    }

    public static CompilerException alternativeNotRecursive(
            TIdentifier identifier) {

        return new CompilerException(new MAlternativeNotRecursive(
                identifier.getText(), "" + identifier.getLine(), ""
                        + identifier.getPos()).toString());
    }

    public static CompilerException recursionNotFollowedByToken(
            TIdentifier identifier) {

        return new CompilerException(new MRecursionNotFollowedByToken(
                identifier.getText(), "" + identifier.getLine(), ""
                        + identifier.getPos()).toString());
    }

    public static CompilerException notImplemented(
            Token token,
            String feature) {

        return new CompilerException(new MNotImplemented(feature,
                token.getLine() + "", token.getPos() + "").toString());
    }

    public static CompilerException outputError(
            String fileName,
            Throwable cause) {

        return new CompilerException(new MOutputError(fileName,
                cause.getMessage()).toString());
    }
*/
}
