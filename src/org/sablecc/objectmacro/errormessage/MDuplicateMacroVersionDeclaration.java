/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MDuplicateMacroVersionDeclaration extends Macro{
    
    String field_Name;
    
    String field_Line;
    
    String field_Char;
    
    String field_RefLine;
    
    String field_RefChar;
    
    String field_Version;
    
    public MDuplicateMacroVersionDeclaration(String pName, String pLine, String pChar, String pRefLine, String pRefChar, String pVersion, Macros macros){
        
        
        this.setMacros(macros);
        this.setPName(pName);
        this.setPLine(pLine);
        this.setPChar(pChar);
        this.setPRefLine(pRefLine);
        this.setPRefChar(pRefChar);
        this.setPVersion(pVersion);
    }
    
    private void setPName( String pName ){
        if(pName == null){
            throw ObjectMacroException.parameterNull("Name");
        }
    
        this.field_Name = pName;
    }
    
    private void setPLine( String pLine ){
        if(pLine == null){
            throw ObjectMacroException.parameterNull("Line");
        }
    
        this.field_Line = pLine;
    }
    
    private void setPChar( String pChar ){
        if(pChar == null){
            throw ObjectMacroException.parameterNull("Char");
        }
    
        this.field_Char = pChar;
    }
    
    private void setPRefLine( String pRefLine ){
        if(pRefLine == null){
            throw ObjectMacroException.parameterNull("RefLine");
        }
    
        this.field_RefLine = pRefLine;
    }
    
    private void setPRefChar( String pRefChar ){
        if(pRefChar == null){
            throw ObjectMacroException.parameterNull("RefChar");
        }
    
        this.field_RefChar = pRefChar;
    }
    
    private void setPVersion( String pVersion ){
        if(pVersion == null){
            throw ObjectMacroException.parameterNull("Version");
        }
    
        this.field_Version = pVersion;
    }
    
    String buildName(){
    
        return this.field_Name;
    }
    
    String buildLine(){
    
        return this.field_Line;
    }
    
    String buildChar(){
    
        return this.field_Char;
    }
    
    String buildRefLine(){
    
        return this.field_RefLine;
    }
    
    String buildRefChar(){
    
        return this.field_RefChar;
    }
    
    String buildVersion(){
    
        return this.field_Version;
    }
    
    String getName(){
    
        return this.field_Name;
    }
    
    String getLine(){
    
        return this.field_Line;
    }
    
    String getChar(){
    
        return this.field_Char;
    }
    
    String getRefLine(){
    
        return this.field_RefLine;
    }
    
    String getRefChar(){
    
        return this.field_RefChar;
    }
    
    String getVersion(){
    
        return this.field_Version;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setDuplicateMacroVersionDeclaration(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("DuplicateMacroVersionDeclaration");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        MSemanticErrorHead m1 = this.getMacros().newSemanticErrorHead();
        
        
        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("Line: ");
        sb0.append(buildLine());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Char: ");
        sb0.append(buildChar());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Duplicate macro declaration of \"");
        sb0.append(buildName());
        sb0.append("\" in version \"");
        sb0.append(buildVersion());
        sb0.append("\".");
        sb0.append(LINE_SEPARATOR);
        sb0.append("It was already declared at line ");
        sb0.append(buildRefLine());
        sb0.append(", char ");
        sb0.append(buildRefChar());
        sb0.append(".");
    
        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }
    
    @Override
    String build(Context context) {
     return build();
    }
    
    
    private void setMacros(Macros macros){
        if(macros == null){
            throw new InternalException("macros cannot be null");
        }
    
        this.macros = macros;
    }
}