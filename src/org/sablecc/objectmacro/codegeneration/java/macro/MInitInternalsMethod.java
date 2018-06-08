/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;
public class MInitInternalsMethod extends Macro {
    
    private DSeparator ParamNameSeparator;
    
    private DBeforeFirst ParamNameBeforeFirst;
    
    private DAfterLast ParamNameAfterLast;
    
    private DNone ParamNameNone;
    
    final List<String> list_ParamName;
    
    final Context ParamNameContext = new Context();
    
    final StringValue ParamNameValue;
    
    private DSeparator ApplyInternalsInitializerSeparator;
    
    private DBeforeFirst ApplyInternalsInitializerBeforeFirst;
    
    private DAfterLast ApplyInternalsInitializerAfterLast;
    
    private DNone ApplyInternalsInitializerNone;
    
    final List<Macro> list_ApplyInternalsInitializer;
    
    final Context ApplyInternalsInitializerContext = new Context();
    
    final MacroValue ApplyInternalsInitializerValue;
    
    MInitInternalsMethod(Macros macros){
        
        this.setMacros(macros);
        this.list_ParamName = new LinkedList<>();
        this.list_ApplyInternalsInitializer = new LinkedList<>();
        
        this.ParamNameValue = new StringValue(this.list_ParamName, this.ParamNameContext);
        this.ApplyInternalsInitializerValue = new MacroValue(this.list_ApplyInternalsInitializer, this.ApplyInternalsInitializerContext);
    }
    
    MInitInternalsMethod(String pParamName, List<Macro> pApplyInternalsInitializer, Macros macros){
        
        this.setMacros(macros);
        this.list_ParamName = new LinkedList<>();
        this.list_ApplyInternalsInitializer = new LinkedList<>();
        
        this.ParamNameValue = new StringValue(this.list_ParamName, this.ParamNameContext);
        this.ApplyInternalsInitializerValue = new MacroValue(this.list_ApplyInternalsInitializer, this.ApplyInternalsInitializerContext);
        if (pApplyInternalsInitializer != null) {
            this.addAllApplyInternalsInitializer(pApplyInternalsInitializer);
        }
        
        if (pParamName != null) {
            this.addParamName(pParamName);
        }
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
    
    public void addAllApplyInternalsInitializer(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("ApplyInternalsInitializer");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "ApplyInternalsInitializer");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeApplyInternalsInitializer(macro);
            this.list_ApplyInternalsInitializer.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeApplyInternalsInitializer (Macro macro) {
        macro.apply(new InternalsInitializer("ApplyInternalsInitializer"){
            @Override
            void setApplyInternalsInitializer(MApplyInternalsInitializer mApplyInternalsInitializer){
                
            
            
            }
        });
    }
    
    public void addApplyInternalsInitializer(MApplyInternalsInitializer macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("ApplyInternalsInitializer");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_ApplyInternalsInitializer.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    private String buildParamName() {
    
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_ParamName;
    
        int i = 0;
        int nb_strings = strings.size();
    
        
        
    
        for(String string : strings) {
            
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildApplyInternalsInitializer() {
    
        StringBuilder sb = new StringBuilder();
        Context local_context = this.ApplyInternalsInitializerContext;
        List<Macro> macros = this.list_ApplyInternalsInitializer;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
        
        if(this.ApplyInternalsInitializerSeparator == null) {
            initApplyInternalsInitializerDirectives();
        }
        
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
            
            expansion = this.ApplyInternalsInitializerSeparator.apply(i, expansion, nb_macros);
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getParamName() {
        return this.ParamNameValue;
    }
    
    MacroValue getApplyInternalsInitializer() {
        return this.ApplyInternalsInitializerValue;
    }
    
    private void initApplyInternalsInitializerInternals(Context context) {
        for(Macro macro : this.list_ApplyInternalsInitializer) {
            macro.apply(new InternalsInitializer("ApplyInternalsInitializer"){
                @Override
                void setApplyInternalsInitializer(MApplyInternalsInitializer mApplyInternalsInitializer){
                    
                    
                    mApplyInternalsInitializer.setParamName(ApplyInternalsInitializerContext, getParamName());
                }
            });
        }
    }
    
    private void initParamNameDirectives() {
        
    }
    
    private void initApplyInternalsInitializerDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.ApplyInternalsInitializerSeparator = new DSeparator(sb1.toString());
        this.ApplyInternalsInitializerValue.setSeparator(this.ApplyInternalsInitializerSeparator);
    }
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setInitInternalsMethod(this);
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
    
        
        initApplyInternalsInitializerInternals(null);
        
        initParamNameDirectives();
        initApplyInternalsInitializerDirectives();
    
        StringBuilder sb0 = new StringBuilder();
        
        sb0.append("private void init");
        sb0.append(buildParamName());
        sb0.append("Internals(Context context) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    for(Macro macro : this.list_");
        sb0.append(buildParamName());
        sb0.append(") ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        sb2.append("    ");
        indentations.add(sb2.toString());
        sb1.append(buildApplyInternalsInitializer());
        sb0.append(applyIndent(sb1.toString(), indentations.remove(indentations.size() - 1)));
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");
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