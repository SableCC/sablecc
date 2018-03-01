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

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.intermediate.syntax3.node.TString;
import org.sablecc.objectmacro.util.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

class GenerationUtils {

    static final String CONTEXT_STRING = "Context";

    static final String INSERT_VAR_NAME = "insert_";

    static final String INDENTATION = "Indentation";

    static final String SEPARATOR_DIRECTIVE = "Separator";

    static final String AFTER_LAST_DIRECTIVE = "AfterLast";

    static final String NONE_DIRECTIVE = "None";

    static final String BEFORE_FIRST_DIRECTIVE = "BeforeFirst";

    static String string(
            TString tString) {

        String string = tString.getText();
        int length = string.length();
        return string.substring(1, length - 1);
    }

    static String escapedString(
            TString tString) {

        StringBuilder sb = new StringBuilder();
        String s = string(tString);
        boolean escaped = false;
        for (char c : s.toCharArray()) {
            if (escaped) {
                escaped = false;

                if (c == '\\') {
                    sb.append('\\');
                    sb.append('\\');
                }
                else if (c == '\'') {
                    sb.append('\'');
                }
                else {
                    throw new InternalException("unhandled case");
                }
            }
            else if (c == '\\') {
                escaped = true;
            }
            else if (c == '\"') {
                sb.append('\\');
                sb.append('\"');
            }
            else {
                sb.append(c);
            }
        }

        if (escaped) {
            throw new InternalException("incomplete escape");
        }

        return sb.toString();
    }

    static String buildNameCamelCase(
            LinkedList<TString> name_parts){

        StringBuilder macroName = new StringBuilder();
        for(TString partName : name_parts){
            macroName.append(Utils.toCamelCase(string(partName)));
        }

        return macroName.toString();
    }

    static String buildName(
            LinkedList<TString> name_parts){

        StringBuilder macroName = new StringBuilder();
        for(TString partName : name_parts){
            macroName.append(string(partName));
        }

        return macroName.toString();
    }

    static void writeFile(
            File packageDirectory,
            String fileName,
            String content){

        File destination = new File(packageDirectory, fileName);

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(content);
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }
    }

    static String getLetterFromInteger(
            Integer i){

        return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
    }
}
