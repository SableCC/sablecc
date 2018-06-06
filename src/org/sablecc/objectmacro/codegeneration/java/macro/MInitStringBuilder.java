/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;
public class MInitStringBuilder extends Macro {
    
    private DSeparator IndexBuilderSeparator;
    
    private DBeforeFirst IndexBuilderBeforeFirst;
    
    private DAfterLast IndexBuilderAfterLast;
    
    private DNone IndexBuilderNone;
    
    final List<String> list_IndexBuilder;
    
    final Context IndexBuilderContext = new Context();
    
    final StringValue IndexBuilderValue;
    
    MInitStringBuilder(Macros macros){
        
        this.setMacros(macros);
        this.list_IndexBuilder = new LinkedList<>();
        
        this.IndexBuilderValue = new StringValue(this.list_IndexBuilder, this.IndexBuilderContext);
    }
    
    MInitStringBuilder(String pIndexBuilder, Macros macros){
        
        this.setMacros(macros);
        this.list_IndexBuilder = new LinkedList<>();
        
        this.IndexBuilderValue = new StringValue(this.list_IndexBuilder, this.IndexBuilderContext);
        
        if (pIndexBuilder != null) {
            this.addIndexBuilder(pIndexBuilder);
        }
    }
    
    public void addAllIndexBuilder(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("IndexBuilder");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("IndexBuilder");
            }
    
            this.list_IndexBuilder.add(string);
        }
    }
    
    public void addIndexBuilder(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("IndexBuilder");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_IndexBuilder.add(string);
    }
    
    private String buildIndexBuilder() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_IndexBuilder;
    
        int i = 0;
        int nb_strings = strings.size();
    
        if(this.IndexBuilderNone != null) {
            sb.append(this.IndexBuilderNone.apply(i, "", nb_strings));
        }
    
        for(String string : strings) {
    
            if(this.IndexBuilderBeforeFirst != null) {
                string = this.IndexBuilderBeforeFirst.apply(i, string, nb_strings);
            }
    
            if(this.IndexBuilderAfterLast != null) {
                string = this.IndexBuilderAfterLast.apply(i, string, nb_strings);
            }
    
            if(this.IndexBuilderSeparator != null) {
                string = this.IndexBuilderSeparator.apply(i, string, nb_strings);
            }
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getIndexBuilder() {
        return this.IndexBuilderValue;
    }
    
    private void initIndexBuilderDirectives() {
        
    }
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setInitStringBuilder(this);
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
    
        
        initIndexBuilderDirectives();
    
        StringBuilder sb0 = new StringBuilder();
        
        sb0.append("StringBuilder sb");
        sb0.append(buildIndexBuilder());
        sb0.append(" = new StringBuilder();");
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