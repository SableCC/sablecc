/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MInitStringValue extends Macro {
    
    private DSeparator ParamNameSeparator;
    
    private DBeforeFirst ParamNameBeforeFirst;
    
    private DAfterLast ParamNameAfterLast;
    
    private DNone ParamNameNone;
    
    final List<String> list_ParamName;
    
    final Context ParamNameContext = new Context();
    
    final StringValue ParamNameValue;
    
    MInitStringValue(Macros macros){
        
        
        this.setMacros(macros);
        this.list_ParamName = new LinkedList<>();
        
        this.ParamNameValue = new StringValue(this.list_ParamName, this.ParamNameContext);
    }
    
    public void addAllParamName(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("ParamName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("ParamName");
            }
    
            this.list_ParamName.add(string);
        }
    }
    
    public void addParamName(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("ParamName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_ParamName.add(string);
    }
    
    private String buildParamName() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_ParamName;
    
        int i = 0;
        int nb_strings = strings.size();
    
        if(this.ParamNameNone != null) {
            sb.append(this.ParamNameNone.apply(i, "", nb_strings));
        }
    
        for(String string : strings) {
    
            if(this.ParamNameBeforeFirst != null) {
                string = this.ParamNameBeforeFirst.apply(i, string, nb_strings);
            }
    
            if(this.ParamNameAfterLast != null) {
                string = this.ParamNameAfterLast.apply(i, string, nb_strings);
            }
    
            if(this.ParamNameSeparator != null) {
                string = this.ParamNameSeparator.apply(i, string, nb_strings);
            }
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getParamName() {
        return this.ParamNameValue;
    }
    
    
    private void initParamNameDirectives() {
        
    }
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setInitStringValue(this);
    }
    
    
    public String build() {
    
        CacheBuilder cache_builder = this.cacheBuilder;
    
        if(cache_builder == null) {
            cache_builder = new CacheBuilder();
        }
        else if(cache_builder.getExpansion() == null) {
            throw new InternalException("Cycle detection detected lately");
        }
        else {
            return cache_builder.getExpansion();
        }
        this.cacheBuilder = cache_builder;
        List<String> indentations = new LinkedList<>();
    
        initParamNameDirectives();
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("this.");
        sb0.append(buildParamName());
        sb0.append("Value = new StringValue(this.list_");
        sb0.append(buildParamName());
        sb0.append(", this.");
        sb0.append(buildParamName());
        sb0.append("Context);");
    
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