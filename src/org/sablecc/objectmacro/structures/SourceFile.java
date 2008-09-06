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

package org.sablecc.objectmacro.structures;

import org.sablecc.objectmacro.exception.InternalException;
import org.sablecc.objectmacro.exception.SemanticException;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroSourceFilePart;
import org.sablecc.objectmacro.syntax3.node.AParam;
import org.sablecc.objectmacro.syntax3.node.ASourceFile;
import org.sablecc.objectmacro.syntax3.node.ATextBlock;
import org.sablecc.objectmacro.syntax3.node.ATextBlockSourceFilePart;
import org.sablecc.objectmacro.syntax3.node.PSourceFilePart;

public class SourceFile
        extends Scope {

    private final ASourceFile definition;

    SourceFile(
            ASourceFile definition,
            GlobalData globalData)
            throws SemanticException {

        super(null, globalData);

        if (definition == null) {
            throw new InternalException("definition may not be null");
        }

        if (globalData == null) {
            throw new InternalException("globalData may not be null");
        }

        this.definition = definition;

        for (PSourceFilePart part : definition.getParts()) {
            if (part instanceof AMacroSourceFilePart) {
                AMacroSourceFilePart macroPart = (AMacroSourceFilePart) part;
                addMacro((AMacro) macroPart.getMacro());
            }
            else if (part instanceof ATextBlockSourceFilePart) {
                ATextBlockSourceFilePart textBlockPart = (ATextBlockSourceFilePart) part;
                addTextBlock((ATextBlock) textBlockPart.getTextBlock());
            }
            else {
                throw new InternalException("unexpected source file part type");
            }
        }
    }

    public ASourceFile getDefinition() {

        return this.definition;
    }

    @Override
    Param addParam(
            AParam definition) {

        throw new InternalException("a source file does not have parameters");
    }

    @Override
    public void addReferencedParam(
            Param param) {

        throw new InternalException(
                "a source file does not refer to parameters");
    }

    @Override
    public void addReferencedTextBlock(
            TextBlock textBlock) {

        throw new InternalException(
                "a source file does not refer to text blocks");
    }
}
