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

package org.sablecc.sablecc.structures;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import org.sablecc.sablecc.exception.CompilerException;
import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.syntax3.node.ALanguageName;
import org.sablecc.sablecc.syntax3.node.ANormalNamedExpression;
import org.sablecc.sablecc.syntax3.node.ASelectionNamedExpression;
import org.sablecc.sablecc.syntax3.node.Start;
import org.sablecc.sablecc.syntax3.node.TIdentifier;
import org.sablecc.sablecc.walkers.SelectionIdentifierFinder;

public class GlobalData {

    private final SortedMap<String, Name> globalNameMap = new TreeMap<String, Name>();

    private final Start ast;

    private final File grammarFile;

    private LanguageName language;

    public GlobalData(
            Start ast,
            File grammarFile) {

        if (ast == null) {
            throw new InternalException("ast may not be null");
        }

        if (grammarFile == null) {
            throw new InternalException("grammarFile may not be null");
        }

        this.ast = ast;
        this.grammarFile = grammarFile;
    }

    public Start getAst() {

        return this.ast;
    }

    public File getGrammarFile() {

        return this.grammarFile;
    }

    public void setLanguageName(
            ALanguageName declaration) {

        if (this.language != null) {
            throw new InternalException("language is already set");
        }

        this.language = new LanguageName(declaration);
        addGlobalName(this.language);
    }

    public void addExpression(
            ANormalNamedExpression declaration) {

        ExpressionName expressionName = new ExpressionName(declaration,
                declaration.getIdentifier());
        addGlobalName(expressionName);
    }

    public void addExpression(
            ASelectionNamedExpression declaration) {

        for (TIdentifier identifier : SelectionIdentifierFinder
                .getIdentifiers(declaration.getSelection())) {
            ExpressionName expressionName = new ExpressionName(declaration,
                    identifier);
            addGlobalName(expressionName);
        }

        SelectorName selectorName = new SelectorName(declaration, declaration
                .getName());
        addGlobalName(selectorName);
    }

    private void addGlobalName(
            Name globalName) {

        if (globalName == null) {
            throw new InternalException("globalName may not be null");
        }

        String globalNameString = globalName.getNameString();
        Name oldGlobalName = this.globalNameMap.get(globalNameString);
        if (oldGlobalName != null) {
            throw CompilerException.duplicate_global_name_declaration(
                    globalName, oldGlobalName);
        }

        this.globalNameMap.put(globalNameString, globalName);
    }
}
