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

package org.sablecc.sablecc.launcher;

import java.io.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.codegeneration.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.syntax3.lexer.*;
import org.sablecc.sablecc.syntax3.parser.*;
import org.sablecc.util.*;

public class CompilationTask {

    final private File grammarFile;

    final private String targetLanguage;

    final private File destinationDirectory;

    final private String destinationPackage;

    final private boolean generateCode;

    final private Strictness strictness;

    final private Trace trace;

    private boolean hasRun;

    public CompilationTask(
            File grammarFile,
            String targetLanguage,
            File destinationDirectory,
            String destinationPackage,
            boolean generateCode,
            Strictness strictness,
            Trace trace) {

        if (grammarFile == null) {
            throw new InternalException("grammarFile may not be null");
        }

        if (targetLanguage == null) {
            throw new InternalException("targetLanguage may not be null");
        }

        if (destinationDirectory == null) {
            throw new InternalException("destinationDirectory may not be null");
        }

        if (destinationPackage == null) {
            throw new InternalException("destinationPackage may not be null");
        }

        if (strictness == null) {
            throw new InternalException("strictness may not be null");
        }

        if (trace == null) {
            throw new InternalException("trace may not be null");
        }

        this.grammarFile = grammarFile;
        this.targetLanguage = targetLanguage;
        this.destinationDirectory = destinationDirectory;
        this.destinationPackage = destinationPackage;
        this.generateCode = generateCode;
        this.strictness = strictness;
        this.trace = trace;
    }

    public void run()
            throws ParserException, LexerException {

        if (this.hasRun) {
            throw new InternalException("compilation task may only run once");
        }
        else {
            this.hasRun = true;
        }

        this.trace.informativeln("Compiling \"" + this.grammarFile.toString()
                + "\"");

        try {
            FileReader fr = new FileReader(this.grammarFile);
            BufferedReader br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = br.read()) != -1) {
                sb.append((char) c);
            }

            br.close();
            fr.close();

            GrammarCompiler grammarCompiler = new GrammarCompiler(
                    sb.toString(), this.strictness, this.trace);
            Grammar grammar = grammarCompiler.compileGrammar();

            if (this.generateCode) {
                CodeGenerator codeGenerator = new CodeGenerator(grammar,
                        this.destinationDirectory, this.destinationPackage,
                        this.trace);
                codeGenerator.run();
            }

            this.trace.informativeln("Done!");
        }
        catch (IOException e) {
            throw LauncherException.inputError(this.grammarFile.toString(), e);
        }
    }
}
