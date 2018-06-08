/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;
public class MStringValueArg extends Macro {
    
    private DSeparator IndexSeparator;
    
    private DBeforeFirst IndexBeforeFirst;
    
    private DAfterLast IndexAfterLast;
    
    private DNone IndexNone;
    
    final List<String> list_Index;
    
    final Context IndexContext = new Context();
    
    final StringValue IndexValue;
    
    MStringValueArg(Macros macros){
        
        this.setMacros(macros);
        this.list_Index = new LinkedList<>();
        
        this.IndexValue = new StringValue(this.list_Index, this.IndexContext);
    }
    
    MStringValueArg(String pIndex, Macros macros){
        
        this.setMacros(macros);
        this.list_Index = new LinkedList<>();
        
        this.IndexValue = new StringValue(this.list_Index, this.IndexContext);
        
        if (pIndex != null) {
            this.addIndex(pIndex);
        }
    }
    
    public void addAllIndex(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("Index");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("Index");
            }
    
            this.list_Index.add(string);
        }
    }
    
    public void addIndex(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("Index");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_Index.add(string);
    }
    
    private String buildIndex() {
    
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_Index;
    
        int i = 0;
        int nb_strings = strings.size();
    
        
        
    
        for(String string : strings) {
            
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getIndex() {
        return this.IndexValue;
    }
    
    private void initIndexDirectives() {
        
    }
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setStringValueArg(this);
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
    
        
        initIndexDirectives();
    
        StringBuilder sb0 = new StringBuilder();
        
        sb0.append("value");
        sb0.append(buildIndex());
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