/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public  class MPackageDeclaration extends Macro{
    
    String field_PackageName;
    
    MPackageDeclaration(String pPackageName, Macros macros){
        
        
        this.setMacros(macros);
        this.setPPackageName(pPackageName);
    }
    
    private void setPPackageName( String pPackageName ){
        if(pPackageName == null){
            throw ObjectMacroException.parameterNull("PackageName");
        }
    
        this.field_PackageName = pPackageName;
    }
    
    String buildPackageName(){
    
        return this.field_PackageName;
    }
    
    String getPackageName(){
    
        return this.field_PackageName;
    }
    
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer){
    
        internalsInitializer.setPackageDeclaration(this);
    }
    
    
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
    
        sb0.append("package ");
        sb0.append(buildPackageName());
        sb0.append(";");
    
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