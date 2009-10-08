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

package org.sablecc.objectmacro.structure;

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.objectmacro.exception.*;
import org.sablecc.objectmacro.syntax3.node.*;

public class Expand {

    private final AExpand declaration;

    private final Scope enclosingMacro;

    private final ExpandSignature signature;

    private AOption none;

    private AOption separator;

    private AOption beforeFirst;

    private AOption afterLast;

    private AOption beforeOne;

    private AOption afterOne;

    private AOption beforeMany;

    private AOption afterMany;

    Expand(
            GlobalIndex globalIndex,
            AExpand declaration,
            Macro enclosingMacro) {

        if (globalIndex == null) {
            throw new InternalException("globalIndex may not be null");
        }

        if (declaration == null) {
            throw new InternalException("declaration may not be null");
        }

        if (enclosingMacro == null) {
            throw new InternalException("enclosingMacro may not be null");
        }

        this.declaration = declaration;
        this.enclosingMacro = enclosingMacro;

        Set<Macro> macroSet = new LinkedHashSet<Macro>();

        for (PMacroReference pMacroReference : declaration.getMacroReferences()) {
            AMacroReference macroReference = (AMacroReference) pMacroReference;
            Macro macro = enclosingMacro.getMacro(macroReference.getName());
            macroSet.add(macro);
        }

        this.signature = globalIndex.getExpandSignature(macroSet);
    }

    public void setOption(
            POption pOption) {

        if (pOption == null) {
            throw new InternalException("pOption may not be null");
        }

        AOption option = (AOption) pOption;
        TIdentifier nameId = option.getName();
        String name = nameId.getText();

        if (name.equals("none")) {

            if (this.none != null) {
                throw CompilerException.duplicateOption(option, this.none);
            }

            this.none = option;
        }
        else if (name.equals("separator")) {

            if (this.separator != null) {
                throw CompilerException.duplicateOption(option, this.separator);
            }

            this.separator = option;
        }
        else if (name.equals("before_first")) {

            if (this.beforeFirst != null) {
                throw CompilerException.duplicateOption(option,
                        this.beforeFirst);
            }
            else if (this.beforeOne != null) {
                throw CompilerException.conflictingOption(option,
                        this.beforeOne);
            }
            else if (this.beforeMany != null) {
                throw CompilerException.conflictingOption(option,
                        this.beforeMany);
            }

            this.beforeFirst = option;
        }
        else if (name.equals("after_last")) {

            if (this.afterLast != null) {
                throw CompilerException.duplicateOption(option, this.afterLast);
            }
            else if (this.afterOne != null) {
                throw CompilerException
                        .conflictingOption(option, this.afterOne);
            }
            else if (this.afterMany != null) {
                throw CompilerException.conflictingOption(option,
                        this.afterMany);
            }

            this.afterLast = option;
        }
        else if (name.equals("before_one")) {

            if (this.beforeOne != null) {
                throw CompilerException.duplicateOption(option, this.beforeOne);
            }
            else if (this.beforeFirst != null) {
                throw CompilerException.conflictingOption(option,
                        this.beforeFirst);
            }

            this.beforeOne = option;
        }
        else if (name.equals("after_one")) {

            if (this.afterOne != null) {
                throw CompilerException.duplicateOption(option, this.afterOne);
            }
            else if (this.afterLast != null) {
                throw CompilerException.conflictingOption(option,
                        this.afterLast);
            }

            this.afterOne = option;
        }
        else if (name.equals("before_many")) {

            if (this.beforeMany != null) {
                throw CompilerException
                        .duplicateOption(option, this.beforeMany);
            }
            else if (this.beforeFirst != null) {
                throw CompilerException.conflictingOption(option,
                        this.beforeFirst);
            }

            this.beforeMany = option;
        }
        else if (name.equals("after_many")) {

            if (this.afterMany != null) {
                throw CompilerException.duplicateOption(option, this.afterMany);
            }
            else if (this.afterLast != null) {
                throw CompilerException.conflictingOption(option,
                        this.afterLast);
            }

            this.afterMany = option;
        }
        else {
            throw CompilerException.unknownOption(option);
        }
    }

    public AExpand getDeclaration() {

        return this.declaration;
    }

    public Scope getEnclosingMacro() {

        return this.enclosingMacro;
    }

    public ExpandSignature getSignature() {

        return this.signature;
    }

    public AOption getNone() {

        return this.none;
    }

    public AOption getSeparator() {

        return this.separator;
    }

    public AOption getBeforeFirst() {

        return this.beforeFirst;
    }

    public AOption getAfterLast() {

        return this.afterLast;
    }

    public AOption getBeforeOne() {

        return this.beforeOne;
    }

    public AOption getAfterOne() {

        return this.afterOne;
    }

    public AOption getBeforeMany() {

        return this.beforeMany;
    }

    public AOption getAfterMany() {

        return this.afterMany;
    }

}
