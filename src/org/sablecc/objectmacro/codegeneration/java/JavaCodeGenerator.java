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

package org.sablecc.objectmacro.codegeneration.java;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sablecc.objectmacro.codegeneration.*;
import org.sablecc.objectmacro.codegeneration.java.structure.Macro;
import org.sablecc.objectmacro.exception.*;
import org.sablecc.util.*;

public class JavaCodeGenerator
        extends CodeGenerator {

    private File packageDirectory;

    public JavaCodeGenerator(
            IntermediateRepresentation ir) {

        super(ir);
    }

    private File getPakcageDirectory() {

        if (this.packageDirectory == null) {
            String packageName = getIr().getDestinationPackage();
            if (!packageName.equals("")) {
                String packageDirectoryName = packageName.replace('.', '/');
                this.packageDirectory = new File(getIr()
                        .getDestinationDirectory(), packageDirectoryName);
            }
            else {
                this.packageDirectory = getIr().getDestinationDirectory();
            }
        }

        return this.packageDirectory;
    }

    @Override
    public void verifyTargetSpecificSemantics(
            Strictness strictness) {

        // nothing to verify
    }

    @Override
    public void generateCode() {

        File packageDirectory = getPakcageDirectory();

        if (!packageDirectory.exists()) {
            if (!packageDirectory.mkdirs()) {
                CompilerException.cannotCreateDirectory(packageDirectory
                        .toString());
            }
        }

        Map<String, Macro> macros = new LinkedHashMap<>();

        /*MacroCollector macroCollector = new MacroCollector(macros);
        getIr().getAST().apply(macroCollector);*/

        /*CodeGenerationWalker walker = new CodeGenerationWalker(getIr(),
                packageDirectory, macros);*/
        CodeGenerationWalker walker = new CodeGenerationWalker(getIr(),
                packageDirectory);
        /*MyWalker walker = new MyWalker(getIr());*/
        getIr().getAST().apply(walker);
    }
}
