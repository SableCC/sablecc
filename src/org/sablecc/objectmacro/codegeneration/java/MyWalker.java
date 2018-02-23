package org.sablecc.objectmacro.codegeneration.java;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.codegeneration.IntermediateRepresentation;
import org.sablecc.objectmacro.exception.CompilerException;
import org.sablecc.objectmacro.intermediate.syntax3.analysis.*;
import org.sablecc.objectmacro.intermediate.syntax3.node.*;
import org.sablecc.objectmacro.util.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MyWalker extends DepthFirstAdapter{

    private IntermediateRepresentation ir;
    private int irMacroCount;
    private Map<String, List<String>> macroList = new LinkedHashMap<>();
    private String currentMacro;
    private String packageDirectory;

    public MyWalker(IntermediateRepresentation ir)
    {
        this.ir = ir;
    }

    private String string(
            TString tString) {

        String string = tString.getText();
        int length = string.length();
        return string.substring(1, length - 1);
    }

    private String escapedString(
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

    private String buildNameCamelCase(
            LinkedList<TString> name_parts){

        StringBuilder macroName = new StringBuilder();
        for(TString partName : name_parts){
            macroName.append(Utils.toCamelCase(string(partName)));
        }

        return macroName.toString();
    }

    private void writeFile(
            String fileName,
            String content){

        File destination = new File(this.packageDirectory, fileName);

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(content);
            fw.close();
        }
        catch (IOException e) {
            throw CompilerException.outputError(destination.toString(), e);
        }
    }

    @Override
    public void inAIntermediateRepresentation(
            AIntermediateRepresentation node) {
        this.irMacroCount = node.getMacros().size();

        if(!this.ir.getDestinationPackage().equals(""))
        {
            this.packageDirectory = this.ir.getDestinationPackage();
        }
    }

    @Override
    public void outAIntermediateRepresentation(
            AIntermediateRepresentation node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Contenu du fichier de macro :");
        sb.append(System.getProperty("line.separator" ));
        for (Map.Entry<String, List<String>> entry: macroList.entrySet()) {
            sb.append("Macro "+entry.getKey().toUpperCase());
            sb.append(entry.getValue().toString());
            sb.append(System.getProperty("line.separator"));
        }
        sb.append("Nombre de macro = "+this.irMacroCount);
        writeFile("Test.txt", sb.toString());
    }

    @Override
    public void inAMacro(
            AMacro node)
    {
        String macroName = buildNameCamelCase(node.getNames());
        if(!macroList.containsKey(macroName))
            macroList.put(macroName, new LinkedList<>());

        this.currentMacro = macroName;
    }

    @Override
    public void outAMacro(
            AMacro node)
    {
        this.currentMacro = null;
    }

    @Override
    public void inAParam(AParam node)
    {
        String pName = buildNameCamelCase(node.getNames());
        String pType = node.getType().toString();

        this.macroList.get(currentMacro).add("- "+pType+" "+pName);
    }

    @Override
    public void inAInternal(AInternal node)
    {
        String iName = buildNameCamelCase(node.getNames());
        String iType = node.getType().toString();

        this.macroList.get(currentMacro).add("- "+iType+" "+iName);
    }

}
