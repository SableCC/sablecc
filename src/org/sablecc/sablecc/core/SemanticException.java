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
            NameDeclaration duplicateNameDeclaration,
            NameDeclaration olderNameDeclaration) {

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

}
