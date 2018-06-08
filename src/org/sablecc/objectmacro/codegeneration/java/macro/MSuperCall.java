/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;
public class MSuperCall extends Macro {
    
    private DSeparator ParametersSeparator;
    
    private DBeforeFirst ParametersBeforeFirst;
    
    private DAfterLast ParametersAfterLast;
    
    private DNone ParametersNone;
    
    final List<String> list_Parameters;
    
    final Context ParametersContext = new Context();
    
    final StringValue ParametersValue;
    
    MSuperCall(Macros macros){
        
        this.setMacros(macros);
        this.list_Parameters = new LinkedList<>();
        
        this.ParametersValue = new StringValue(this.list_Parameters, this.ParametersContext);
    }
    
    MSuperCall(String pParameters, Macros macros){
        
        this.setMacros(macros);
        this.list_Parameters = new LinkedList<>();
        
        this.ParametersValue = new StringValue(this.list_Parameters, this.ParametersContext);
        
        if (pParameters != null) {
            this.addParameters(pParameters);
        }
    }
    
    public void addAllParameters(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("Parameters");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("Parameters");
            }
    
            this.list_Parameters.add(string);
        }
    }
    
    public void addParameters(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("Parameters");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_Parameters.add(string);
    }
    
    private String buildParameters() {
    
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_Parameters;
    
        int i = 0;
        int nb_strings = strings.size();
    
        
    if(this.ParametersSeparator == null) {
        initParametersDirectives();
    }
        
    
        for(String string : strings) {
            
    string = this.ParametersSeparator.apply(i, string, nb_strings);
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getParameters() {
        return this.ParametersValue;
    }
    
    private void initParametersDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(", ");
        this.ParametersSeparator = new DSeparator(sb1.toString());
        this.ParametersValue.setSeparator(this.ParametersSeparator);
    }
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setSuperCall(this);
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
    
        
        initParametersDirectives();
    
        StringBuilder sb0 = new StringBuilder();
        
        sb0.append("super(");
        sb0.append(buildParameters());
        sb0.append(");");
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