/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;
public class MNoneDirective extends Macro {
    
    private Map<Context, StringValue> list_StringBuilderName = new LinkedHashMap<>();
    
    private Map<Context, StringValue> list_Index = new LinkedHashMap<>();
    
    private Map<Context, StringValue> list_SizeVarName = new LinkedHashMap<>();
    
    private Map<Context, StringValue> list_ParameterName = new LinkedHashMap<>();
    
    MNoneDirective(Macros macros){
        
        this.setMacros(macros);
        this.list_StringBuilderName = new LinkedHashMap<>();
        this.list_Index = new LinkedHashMap<>();
        this.list_SizeVarName = new LinkedHashMap<>();
        this.list_ParameterName = new LinkedHashMap<>();
    }
    
    void setStringBuilderName(
            Context context,
            StringValue value) {
    
        if(value == null){
            throw new RuntimeException("value cannot be null here");
        }
    
        this.list_StringBuilderName.put(context, value);
    }
    
    void setIndex(
            Context context,
            StringValue value) {
    
        if(value == null){
            throw new RuntimeException("value cannot be null here");
        }
    
        this.list_Index.put(context, value);
    }
    
    void setSizeVarName(
            Context context,
            StringValue value) {
    
        if(value == null){
            throw new RuntimeException("value cannot be null here");
        }
    
        this.list_SizeVarName.put(context, value);
    }
    
    void setParameterName(
            Context context,
            StringValue value) {
    
        if(value == null){
            throw new RuntimeException("value cannot be null here");
        }
    
        this.list_ParameterName.put(context, value);
    }
    
    private String buildStringBuilderName(Context context) {
    
        StringValue stringValue = this.list_StringBuilderName.get(context);
        return stringValue.build();
    }
    
    private String buildIndex(Context context) {
    
        StringValue stringValue = this.list_Index.get(context);
        return stringValue.build();
    }
    
    private String buildSizeVarName(Context context) {
    
        StringValue stringValue = this.list_SizeVarName.get(context);
        return stringValue.build();
    }
    
    private String buildParameterName(Context context) {
    
        StringValue stringValue = this.list_ParameterName.get(context);
        return stringValue.build();
    }
    
    private StringValue getStringBuilderName(Context context) {
    
        return this.list_StringBuilderName.get(context);
    }
    
    private StringValue getIndex(Context context) {
    
        return this.list_Index.get(context);
    }
    
    private StringValue getSizeVarName(Context context) {
    
        return this.list_SizeVarName.get(context);
    }
    
    private StringValue getParameterName(Context context) {
    
        return this.list_ParameterName.get(context);
    }
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setNoneDirective(this);
    }
    
    
    String build(Context context) {
    
        CacheBuilder cache_builder = this.cacheBuilders.get(context);
    
        if(cache_builder == null) {
            cache_builder = new CacheBuilder();
        }
        else if(cache_builder.getExpansion() == null) {
            throw new InternalException("Cycle detection detected lately");
        }
        else {
            return cache_builder.getExpansion();
        }
        
    this.cacheBuilders.put(context, cache_builder);
        List<String> indentations = new LinkedList<>();
    
        
    
        StringBuilder sb0 = new StringBuilder();
        
        sb0.append(buildStringBuilderName(context));
        sb0.append(".append(this.");
        sb0.append(buildParameterName(context));
        sb0.append("None.apply(");
        sb0.append(buildIndex(context));
        sb0.append(", \"\", ");
        sb0.append(buildSizeVarName(context));
        sb0.append("));");
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