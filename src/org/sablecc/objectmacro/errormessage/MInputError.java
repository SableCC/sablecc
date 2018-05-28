/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MInputError extends Macro{
    
    String field_FileName;
    
    String field_Message;
    
    MInputError(String pFileName, String pMessage, Macros macros){
        
        
        this.setMacros(macros);
        this.setPFileName(pFileName);
        this.setPMessage(pMessage);
    }
    
    private void setPFileName( String pFileName ){
        if(pFileName == null){
            throw ObjectMacroException.parameterNull("FileName");
        }
    
        this.field_FileName = pFileName;
    }
    
    private void setPMessage( String pMessage ){
        if(pMessage == null){
            throw ObjectMacroException.parameterNull("Message");
        }
    
        this.field_Message = pMessage;
    }
    
    String buildFileName(){
    
        return this.field_FileName;
    }
    
    String buildMessage(){
    
        return this.field_Message;
    }
    
    String getFileName(){
    
        return this.field_FileName;
    }
    
    String getMessage(){
    
        return this.field_Message;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setInputError(this);
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
    
        sb0.append("*** I/O ERROR ***");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("The following system error happened while reading \"");
        sb0.append(buildFileName());
        sb0.append("\":");
        sb0.append(LINE_SEPARATOR);
        sb0.append(" ");
        sb0.append(buildMessage());
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