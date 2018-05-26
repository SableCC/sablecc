/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MDuplicateDeclaration extends Macro{
    
    String field_Name;
    
    String field_Line;
    
    String field_Char;
    
    String field_RefLine;
    
    String field_RefChar;
    
    final List<Macro> list_Version;
    
    final Context VersionContext = new Context();
    
    final InternalValue VersionValue;
    
    private DSeparator VersionSeparator;
    
    private DBeforeFirst VersionBeforeFirst;
    
    private DAfterLast VersionAfterLast;
    
    private DNone VersionNone;
    
    public MDuplicateDeclaration(String pName, String pLine, String pChar, String pRefLine, String pRefChar, Macros macros){
        
        
        this.setMacros(macros);
        this.setPName(pName);
        this.setPLine(pLine);
        this.setPChar(pChar);
        this.setPRefLine(pRefLine);
        this.setPRefChar(pRefChar);
        this.list_Version = new LinkedList<>();
        
        this.VersionValue = new InternalValue(this.list_Version, this.VersionContext);
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
    
    public void addAllVersion(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("Version");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("DuplicateDeclaration");
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "Version");
            }
        
            if(this.getMacros() != macro.getMacros()){
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeVersion(macro);
            this.list_Version.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeVersion (Macro macro) {
        macro.apply(new InternalsInitializer("Version"){
            @Override
            void setPlainText(MPlainText mPlainText){
            
                
                
            }
        });
    }
    
    public void addVersion(MPlainText macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Version");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("DuplicateDeclaration");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Version.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
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
    
    private String buildVersion(){
        StringBuilder sb = new StringBuilder();
        Context local_context = VersionContext;
        List<Macro> macros = this.list_Version;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.VersionNone != null){
            sb.append(this.VersionNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.VersionBeforeFirst != null){
                expansion = this.VersionBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.VersionAfterLast != null){
                expansion = this.VersionAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.VersionSeparator != null){
                expansion = this.VersionSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
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
    
    private InternalValue getVersion(){
        return this.VersionValue;
    }
    private void initVersionInternals(Context context){
        for(Macro macro : this.list_Version){
            macro.apply(new InternalsInitializer("Version"){
                @Override
                void setPlainText(MPlainText mPlainText){
                
                    
                    
                }
            });
        }
    }
    
    private void initVersionDirectives(){
        StringBuilder sb1 = new StringBuilder();
        sb1.append("in version: ");
        this.VersionBeforeFirst = new DBeforeFirst(sb1.toString());
        this.VersionValue.setBeforeFirst(this.VersionBeforeFirst);
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setDuplicateDeclaration(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("DuplicateDeclaration");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        initVersionDirectives();
        
        initVersionInternals(null);
    
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
        sb0.append("Duplicate declaration of \"");
        sb0.append(buildName());
        sb0.append("\" ");
        sb0.append(buildVersion());
        sb0.append(".");
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