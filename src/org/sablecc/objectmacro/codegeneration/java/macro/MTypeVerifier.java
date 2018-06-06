/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;
public class MTypeVerifier extends Macro {
    
    private DSeparator ParamNameSeparator;
    
    private DBeforeFirst ParamNameBeforeFirst;
    
    private DAfterLast ParamNameAfterLast;
    
    private DNone ParamNameNone;
    
    final List<String> list_ParamName;
    
    final Context ParamNameContext = new Context();
    
    final StringValue ParamNameValue;
    
    private DSeparator TypeVerificationSeparator;
    
    private DBeforeFirst TypeVerificationBeforeFirst;
    
    private DAfterLast TypeVerificationAfterLast;
    
    private DNone TypeVerificationNone;
    
    final List<Macro> list_TypeVerification;
    
    final Context TypeVerificationContext = new Context();
    
    final MacroValue TypeVerificationValue;
    
    private DSeparator OverrideSeparator;
    
    private DBeforeFirst OverrideBeforeFirst;
    
    private DAfterLast OverrideAfterLast;
    
    private DNone OverrideNone;
    
    final List<Macro> list_Override;
    
    final Context OverrideContext = new Context();
    
    final MacroValue OverrideValue;
    
    MTypeVerifier(Macros macros){
        
        this.setMacros(macros);
        this.list_ParamName = new LinkedList<>();
        this.list_TypeVerification = new LinkedList<>();
        this.list_Override = new LinkedList<>();
        
        this.ParamNameValue = new StringValue(this.list_ParamName, this.ParamNameContext);
        this.TypeVerificationValue = new MacroValue(this.list_TypeVerification, this.TypeVerificationContext);
        this.OverrideValue = new MacroValue(this.list_Override, this.OverrideContext);
    }
    
    MTypeVerifier(String pParamName, List<Macro> pTypeVerification, List<Macro> pOverride, Macros macros){
        
        this.setMacros(macros);
        this.list_ParamName = new LinkedList<>();
        this.list_TypeVerification = new LinkedList<>();
        this.list_Override = new LinkedList<>();
        
        this.ParamNameValue = new StringValue(this.list_ParamName, this.ParamNameContext);
        this.TypeVerificationValue = new MacroValue(this.list_TypeVerification, this.TypeVerificationContext);
        this.OverrideValue = new MacroValue(this.list_Override, this.OverrideContext);
        if (pTypeVerification != null) {
            this.addAllTypeVerification(pTypeVerification);
        }
        if (pOverride != null) {
            this.addAllOverride(pOverride);
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
    
    public void addAllTypeVerification(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("TypeVerification");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "TypeVerification");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeTypeVerification(macro);
            this.list_TypeVerification.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeTypeVerification (Macro macro) {
        macro.apply(new InternalsInitializer("TypeVerification"){
            @Override
            void setApplyInternalsInitializer(MApplyInternalsInitializer mApplyInternalsInitializer){
                
            
            
            }
        });
    }
    
    public void addTypeVerification(MApplyInternalsInitializer macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("TypeVerification");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_TypeVerification.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addAllOverride(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("Override");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "Override");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeOverride(macro);
            this.list_Override.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeOverride (Macro macro) {
        macro.apply(new InternalsInitializer("Override"){
            @Override
            void setOverride(MOverride mOverride){
                
            
            
            }
        });
    }
    
    public void addOverride(MOverride macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Override");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Override.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
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
    
    private String buildTypeVerification() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.TypeVerificationContext;
        List<Macro> macros = this.list_TypeVerification;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.TypeVerificationNone != null) {
            sb.append(this.TypeVerificationNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.TypeVerificationBeforeFirst != null) {
                expansion = this.TypeVerificationBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.TypeVerificationAfterLast != null) {
                expansion = this.TypeVerificationAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.TypeVerificationSeparator != null) {
                expansion = this.TypeVerificationSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildOverride() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.OverrideContext;
        List<Macro> macros = this.list_Override;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.OverrideNone != null) {
            sb.append(this.OverrideNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.OverrideBeforeFirst != null) {
                expansion = this.OverrideBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.OverrideAfterLast != null) {
                expansion = this.OverrideAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.OverrideSeparator != null) {
                expansion = this.OverrideSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getParamName() {
        return this.ParamNameValue;
    }
    
    MacroValue getTypeVerification() {
        return this.TypeVerificationValue;
    }
    
    MacroValue getOverride() {
        return this.OverrideValue;
    }
    
    private void initTypeVerificationInternals(Context context) {
        for(Macro macro : this.list_TypeVerification) {
            macro.apply(new InternalsInitializer("TypeVerification"){
                @Override
                void setApplyInternalsInitializer(MApplyInternalsInitializer mApplyInternalsInitializer){
                    
                    
                    mApplyInternalsInitializer.setParamName(TypeVerificationContext, getParamName());
                }
            });
        }
    }
    
    private void initOverrideInternals(Context context) {
        for(Macro macro : this.list_Override) {
            macro.apply(new InternalsInitializer("Override"){
                @Override
                void setOverride(MOverride mOverride){
                    
                
                
                }
            });
        }
    }
    
    private void initParamNameDirectives() {
        
    }
    
    private void initTypeVerificationDirectives() {
        
    }
    
    private void initOverrideDirectives() {
        
    }
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setTypeVerifier(this);
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
        initTypeVerificationDirectives();
        initOverrideDirectives();
        
        initTypeVerificationInternals(null);
        initOverrideInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
        
        sb0.append(buildOverride());
        sb0.append(LINE_SEPARATOR);
        sb0.append("void verifyType");
        sb0.append(buildParamName());
        sb0.append(" (Macro macro) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        indentations.add(sb2.toString());
        sb1.append(buildTypeVerification());
        sb0.append(applyIndent(sb1.toString(), indentations.remove(indentations.size() - 1)));
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