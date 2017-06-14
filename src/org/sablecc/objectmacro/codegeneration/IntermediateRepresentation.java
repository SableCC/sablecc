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

package org.sablecc.objectmacro.codegeneration;

import org.sablecc.objectmacro.intermediate.syntax3.node.PIntermediateRepresentation;

import java.io.*;

public class IntermediateRepresentation {

    private final PIntermediateRepresentation ast;

    private final String name;

    private final File destinationDirectory;

    private final String destinationPackage;

    public IntermediateRepresentation(
            PIntermediateRepresentation ast,
            File macroFile,
            File destinationDirectory,
            String destinationPackage) {

        this.ast = ast;
        this.destinationDirectory = destinationDirectory;
        this.destinationPackage = destinationPackage;

        String macroFileName = macroFile.getName();
        int length = macroFileName.length();
        this.name = macroFileName
                .substring(0, length - ".objectmacro".length());
    }

    public PIntermediateRepresentation getAST() {

        return this.ast;
    }

    public String getName() {

        return this.name;
    }

    public File getDestinationDirectory() {

        return this.destinationDirectory;
    }

    public String getDestinationPackage() {

        return this.destinationPackage;
    }
}
