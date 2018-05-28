/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MDuplicateOption extends Macro{
    
    String field_Name;
    
    String field_Line;
    
    String field_Char;
    
    String field_RefLine;
    
    String field_RefChar;
    
    MDuplicateOption(String pName, String pLine, String pChar, String pRefLine, String pRefChar, Macros macros){
        
        
        this.setMacros(macros);
        this.setPName(pName);
        this.setPLine(pLine);
        this.setPChar(pChar);
        this.setPRefLine(pRefLine);
        this.setPRefChar(pRefChar);
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
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setDuplicateOption(this);
     }
    
    @Override
    public String build(){
    
        CacheBuilder cache_builder = this.cacheBuilder;
    
        if(cache_builder == null){
            cache_builder = new CacheBuilder();
        }
        else if(cache_builder.getExpansion() == null){
            throw new InternalException("Cycle detection detected lately");
        }
        else{
            return cache_builder.getExpansion();
        }
        this.cacheBuilder = cache_builder;
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
        sb0.append("Duplicate \"");
        sb0.append(buildName());
        sb0.append("\" option.");
        sb0.append(LINE_SEPARATOR);
        sb0.append("It was already provided at line ");
        sb0.append(buildRefLine());
        sb0.append(", char ");
        sb0.append(buildRefChar());
        sb0.append(".");
    
        cache_builder.setExpansion(sb0.toString());
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