/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MUnknownMacro extends Macro{
    
    String field_Name;
    
    String field_Line;
    
    String field_Char;
    
    final List<Macro> list_Versions;
    
    final Context VersionsContext = new Context();
    
    final InternalValue VersionsValue;
    
    private DSeparator VersionsSeparator;
    
    private DBeforeFirst VersionsBeforeFirst;
    
    private DAfterLast VersionsAfterLast;
    
    private DNone VersionsNone;
    
    public MUnknownMacro(String pName, String pLine, String pChar, Macros macros){
        
        
        this.setMacros(macros);
        this.setPName(pName);
        this.setPLine(pLine);
        this.setPChar(pChar);
        this.list_Versions = new LinkedList<>();
        
        this.VersionsValue = new InternalValue(this.list_Versions, this.VersionsContext);
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
    
    public void addVersions(MPlainText macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Versions");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("PlainText");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Versions.add(macro);
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
    
    private String buildVersions(){
        StringBuilder sb = new StringBuilder();
        Context local_context = VersionsContext;
        List<Macro> macros = this.list_Versions;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.VersionsNone != null){
            sb.append(this.VersionsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.VersionsBeforeFirst != null){
                expansion = this.VersionsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.VersionsAfterLast != null){
                expansion = this.VersionsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.VersionsSeparator != null){
                expansion = this.VersionsSeparator.apply(i, expansion, nb_macros);
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
    
    private InternalValue getVersions(){
        return this.VersionsValue;
    }
    private void initVersionsInternals(Context context){
        for(Macro macro : this.list_Versions){
            macro.apply(new InternalsInitializer("Versions"){
                @Override
                void setPlainText(MPlainText mPlainText){
                
                    
                    
                }
            });
        }
    }
    
    private void initVersionsDirectives(){
        StringBuilder sb1 = new StringBuilder();
        sb1.append(".");
        this.VersionsNone = new DNone(sb1.toString());
        this.VersionsValue.setNone(this.VersionsNone);StringBuilder sb2 = new StringBuilder();
        sb2.append("in version: ");
        this.VersionsBeforeFirst = new DBeforeFirst(sb2.toString());
        this.VersionsValue.setBeforeFirst(this.VersionsBeforeFirst);StringBuilder sb3 = new StringBuilder();
        sb3.append(".");
        this.VersionsAfterLast = new DAfterLast(sb3.toString());
        this.VersionsValue.setAfterLast(this.VersionsAfterLast);StringBuilder sb4 = new StringBuilder();
        sb4.append(", ");
        this.VersionsSeparator = new DSeparator(sb4.toString());
        this.VersionsValue.setSeparator(this.VersionsSeparator);
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setUnknownMacro(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("UnknownMacro");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        initVersionsDirectives();
        
        initVersionsInternals(null);
    
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
        sb0.append("Macro \"");
        sb0.append(buildName());
        sb0.append("\" does not exist ");
        sb0.append(buildVersions());
    
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