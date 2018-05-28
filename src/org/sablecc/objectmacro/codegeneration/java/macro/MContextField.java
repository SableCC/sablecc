/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public  class MContextField extends Macro{
    
    private Map<Context, String> field_ParamName = new LinkedHashMap<>();
    
    MContextField(Macros macros){
        
        
        this.setMacros(macros);
    }
    
    void setParamName(
            Context context,
            String value) {
    
        if(value == null){
            throw new RuntimeException("value cannot be null here");
        }
    
        this.field_ParamName.put(context, value);
    }
    
    String buildParamName(Context context){
    
        return this.field_ParamName.get(context);
    }
    
    String getParamName(Context context){
    
        return this.field_ParamName.get(context);
    }
    
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer){
    
        internalsInitializer.setContextField(this);
    }
    
    
    String build(Context context){
    
        CacheBuilder cache_builder = this.cacheBuilders.get(context);
    
        if(cache_builder == null){
            cache_builder = new CacheBuilder();
        }
        else if(cache_builder.getExpansion() == null){
            throw new InternalException("Cycle detection detected lately");
        }
        else{
            return cache_builder.getExpansion();
        }
        this.cacheBuilders.put(context, cache_builder);
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("final Context ");
        sb0.append(buildParamName(context));
        sb0.append("Context = new Context();");
    
        cache_builder.setExpansion(sb0.toString());
        return sb0.toString();
    }
    
    
    private void setMacros(Macros macros){
        if(macros == null){
            throw new InternalException("macros cannot be null");
        }
    
        this.macros = macros;
    }
}