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

package org.sablecc.objectmacro.bootstrap;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.sablecc.objectmacro.syntax3.analysis.DepthFirstAdapter;
import org.sablecc.objectmacro.syntax3.node.ADQuoteMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEolMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AEscapeMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AFile;
import org.sablecc.objectmacro.syntax3.node.AMacro;
import org.sablecc.objectmacro.syntax3.node.AMacroMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.ATextMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.AVarMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.PMacroBodyPart;
import org.sablecc.objectmacro.syntax3.node.TIdentifier;

public class CodeGeneration
        extends DepthFirstAdapter {

    private final LinkedList<MacroParent> parentStack = new LinkedList<MacroParent>();

    private Macro currentMacro;

    @Override
    public void inAFile(
            AFile node) {

        this.parentStack.addFirst(File.getFile());
    }

    @Override
    public void outAFile(
            AFile node) {

        this.parentStack.removeFirst();
    }

    @Override
    public void inAMacro(
            AMacro node) {

        Macro macro = Macro.getMacro(node);

        this.parentStack.addFirst(macro);
        this.currentMacro = macro;

        StringBuilder sb = new StringBuilder();

        sb.append("import java.util.*;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("public class Macro_");
        sb.append(node.getName().getText());
        sb.append(" {");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));

        {
            Macro current = macro;

            while (current != null) {
                for (TIdentifier parameter : current.getDeclaration()
                        .getParameters()) {
                    sb.append("  private final String param_");
                    sb.append(parameter.getText());
                    sb.append(";");
                    sb.append(System.getProperty("line.separator"));
                }

                current = current.getParentMacro();
            }
        }

        sb.append(System.getProperty("line.separator"));

        for (PMacroBodyPart part : node.getParts()) {
            if (part instanceof AMacroMacroBodyPart) {
                Macro nestedMacro = Macro
                        .getMacro((AMacro) ((AMacroMacroBodyPart) part)
                                .getMacro());
                sb.append("  private final List<Macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append("> macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append("_list = new LinkedList<Macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(">();");
                sb.append(System.getProperty("line.separator"));
            }
        }

        sb.append(System.getProperty("line.separator"));

        if (macro.getParentMacro() == null) {
            sb.append("  public Macro_");
        }
        else {
            sb.append("  Macro_");
        }

        sb.append(node.getName().getText());
        sb.append("(");

        {
            boolean first = true;
            Macro current = macro;

            while (current != null) {
                for (TIdentifier parameter : current.getDeclaration()
                        .getParameters()) {
                    if (first) {
                        first = false;
                    }
                    else {
                        sb.append(",");
                    }
                    sb.append(System.getProperty("line.separator"));
                    sb.append("           String param_");
                    sb.append(parameter.getText());
                }

                current = current.getParentMacro();
            }
        }

        sb.append(") {");
        sb.append(System.getProperty("line.separator"));

        {
            Macro current = macro;

            while (current != null) {
                for (TIdentifier parameter : current.getDeclaration()
                        .getParameters()) {
                    sb.append("    this.param_");
                    sb.append(parameter.getText());
                    sb.append(" = param_");
                    sb.append(parameter.getText());
                    sb.append(";");
                    sb.append(System.getProperty("line.separator"));
                }

                current = current.getParentMacro();
            }
        }

        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));

        for (PMacroBodyPart part : node.getParts()) {
            if (part instanceof AMacroMacroBodyPart) {
                Macro nestedMacro = Macro
                        .getMacro((AMacro) ((AMacroMacroBodyPart) part)
                                .getMacro());
                sb.append("  public Macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(" newMacro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append("(");

                {
                    boolean first = true;
                    for (TIdentifier parameter : nestedMacro.getDeclaration()
                            .getParameters()) {
                        if (first) {
                            first = false;
                        }
                        else {
                            sb.append(",");
                        }
                        sb.append(System.getProperty("line.separator"));
                        sb.append("           String param_");
                        sb.append(parameter.getText());
                    }
                }

                sb.append(") {");
                sb.append(System.getProperty("line.separator"));
                sb.append("    Macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(" macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(" = new Macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append("(");

                {
                    boolean first = true;
                    Macro current = nestedMacro;

                    while (current != null) {
                        for (TIdentifier parameter : current.getDeclaration()
                                .getParameters()) {
                            if (first) {
                                first = false;
                            }
                            else {
                                sb.append(",");
                            }
                            sb.append(System.getProperty("line.separator"));
                            sb.append("                 param_");
                            sb.append(parameter.getText());
                        }

                        current = current.getParentMacro();
                    }
                }

                sb.append(");");
                sb.append(System.getProperty("line.separator"));
                sb.append("    macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append("_list.add(macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(");");
                sb.append(System.getProperty("line.separator"));
                sb.append("    return macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(";");
                sb.append(System.getProperty("line.separator"));
                sb.append("  }");
                sb.append(System.getProperty("line.separator"));
                sb.append(System.getProperty("line.separator"));
            }
        }

        sb.append("  public String toString() {");
        sb.append(System.getProperty("line.separator"));
        sb.append("    StringBuilder sb = new StringBuilder();");
        sb.append(System.getProperty("line.separator"));

        for (PMacroBodyPart part : node.getParts()) {
            if (part instanceof AVarMacroBodyPart) {
                sb.append("    sb.append(param_");
                sb
                        .append(Utils.getVarName(((AVarMacroBodyPart) part)
                                .getVar()));
                sb.append(");");
                sb.append(System.getProperty("line.separator"));
            }
            else if (part instanceof ATextMacroBodyPart) {
                sb.append("    sb.append(\"");
                sb.append(((ATextMacroBodyPart) part).getText().getText());
                sb.append("\");");
                sb.append(System.getProperty("line.separator"));
            }
            else if (part instanceof ADQuoteMacroBodyPart) {
                sb.append("    sb.append('\"');");
                sb.append(System.getProperty("line.separator"));
            }
            else if (part instanceof AEolMacroBodyPart) {
                sb
                        .append("    sb.append(System.getProperty(\"line.separator\"));");
                sb.append(System.getProperty("line.separator"));
            }
            else if (part instanceof AEscapeMacroBodyPart) {
                char c = ((AEscapeMacroBodyPart) part).getEscape().getText()
                        .charAt(1);
                sb.append("    sb.append('");
                switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '$':
                    sb.append("$");
                    break;
                default:
                    throw new InternalException();
                }
                sb.append("');");
                sb.append(System.getProperty("line.separator"));
            }
            else if (part instanceof AMacroMacroBodyPart) {
                Macro nestedMacro = Macro
                        .getMacro((AMacro) ((AMacroMacroBodyPart) part)
                                .getMacro());
                sb.append("    for(Macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(" macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(" : macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append("_list) {");
                sb.append(System.getProperty("line.separator"));
                sb.append("      sb.append(macro_");
                sb.append(nestedMacro.getDeclaration().getName().getText());
                sb.append(".toString());");
                sb.append(System.getProperty("line.separator"));
                sb.append("    }");
                sb.append(System.getProperty("line.separator"));
            }
            else {
                throw new InternalException();
            }
        }

        sb.append("    return sb.toString();");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));

        sb.append("}");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));

        try {
        FileWriter fw = new FileWriter("Macro_" + node.getName().getText() + ".java");
        fw.write(sb.toString());
        fw.close();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void outAMacro(
            AMacro node) {

        this.parentStack.removeFirst();
        this.currentMacro = this.currentMacro.getParentMacro();
    }

}
