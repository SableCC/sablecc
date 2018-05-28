/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public  class MMissingMacroFile extends Macro{
    
    String field_FileName;
    
    MMissingMacroFile(String pFileName, Macros macros){
        
        
        this.setMacros(macros);
        this.setPFileName(pFileName);
    }
    
    private void setPFileName( String pFileName ){
        if(pFileName == null){
            throw ObjectMacroException.parameterNull("FileName");
        }
    
        this.field_FileName = pFileName;
    }
    
    String buildFileName(){
    
        return this.field_FileName;
    }
    
    String getFileName(){
    
        return this.field_FileName;
    }
    
    
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setMissingMacroFile(this);
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
    
        MCommandLineErrorHead m1 = this.getMacros().newCommandLineErrorHead();
        
        
        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("The macro file, \"");
        sb0.append(buildFileName());
        sb0.append("\", does not exist.");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        MCommandLineErrorTail m2 = this.getMacros().newCommandLineErrorTail();
        
        
        sb0.append(m2.build(null));
    
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