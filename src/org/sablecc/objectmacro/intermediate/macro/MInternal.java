/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

public  class MInternal extends Macro{
    
    final List<Macro> list_InternalName;
    
    final Context InternalNameContext = new Context();
    
    final InternalValue InternalNameValue;
    
    private DSeparator InternalNameSeparator;
    
    private DBeforeFirst InternalNameBeforeFirst;
    
    private DAfterLast InternalNameAfterLast;
    
    private DNone InternalNameNone;
    
    final List<Macro> list_Type;
    
    final Context TypeContext = new Context();
    
    final InternalValue TypeValue;
    
    private DSeparator TypeSeparator;
    
    private DBeforeFirst TypeBeforeFirst;
    
    private DAfterLast TypeAfterLast;
    
    private DNone TypeNone;
    
    final List<Macro> list_Directives;
    
    final Context DirectivesContext = new Context();
    
    final InternalValue DirectivesValue;
    
    private DSeparator DirectivesSeparator;
    
    private DBeforeFirst DirectivesBeforeFirst;
    
    private DAfterLast DirectivesAfterLast;
    
    private DNone DirectivesNone;
    
    MInternal(Macros macros){
        
        
        this.setMacros(macros);
        this.list_InternalName = new LinkedList<>();
        this.list_Type = new LinkedList<>();
        this.list_Directives = new LinkedList<>();
        
        this.InternalNameValue = new InternalValue(this.list_InternalName, this.InternalNameContext);
        this.TypeValue = new InternalValue(this.list_Type, this.TypeContext);
        this.DirectivesValue = new InternalValue(this.list_Directives, this.DirectivesContext);
    }
    
    public void addAllInternalName(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("InternalName");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("Internal");
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "InternalName");
            }
        
            if(this.getMacros() != macro.getMacros()){
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeInternalName(macro);
            this.list_InternalName.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeInternalName (Macro macro) {
        macro.apply(new InternalsInitializer("InternalName"){
            @Override
            void setName(MName mName){
            
                
                
            }
        });
    }
    
    public void addInternalName(MName macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("InternalName");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("Internal");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_InternalName.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addAllType(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("Type");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("Internal");
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "Type");
            }
        
            if(this.getMacros() != macro.getMacros()){
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeType(macro);
            this.list_Type.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeType (Macro macro) {
        macro.apply(new InternalsInitializer("Type"){
            @Override
            void setStringType(MStringType mStringType){
            
                
                
            }
            
            @Override
            void setMacroType(MMacroType mMacroType){
            
                
                
            }
        });
    }
    
    public void addType(MStringType macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Type");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("Internal");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Type.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addType(MMacroType macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Type");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("Internal");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Type.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addAllDirectives(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("Directives");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("Internal");
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "Directives");
            }
        
            if(this.getMacros() != macro.getMacros()){
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeDirectives(macro);
            this.list_Directives.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeDirectives (Macro macro) {
        macro.apply(new InternalsInitializer("Directives"){
            @Override
            void setDirective(MDirective mDirective){
            
                
                
            }
        });
    }
    
    public void addDirectives(MDirective macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Directives");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("Internal");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Directives.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    private String buildInternalName(){
        StringBuilder sb = new StringBuilder();
        Context local_context = InternalNameContext;
        List<Macro> macros = this.list_InternalName;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.InternalNameNone != null){
            sb.append(this.InternalNameNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro: macros){
            expansion = macro.build(local_context);
    
            if(this.InternalNameBeforeFirst != null){
                expansion = this.InternalNameBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.InternalNameAfterLast != null){
                expansion = this.InternalNameAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.InternalNameSeparator != null){
                expansion = this.InternalNameSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildType(){
        StringBuilder sb = new StringBuilder();
        Context local_context = TypeContext;
        List<Macro> macros = this.list_Type;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.TypeNone != null){
            sb.append(this.TypeNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro: macros){
            expansion = macro.build(local_context);
    
            if(this.TypeBeforeFirst != null){
                expansion = this.TypeBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.TypeAfterLast != null){
                expansion = this.TypeAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.TypeSeparator != null){
                expansion = this.TypeSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildDirectives(){
        StringBuilder sb = new StringBuilder();
        Context local_context = DirectivesContext;
        List<Macro> macros = this.list_Directives;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.DirectivesNone != null){
            sb.append(this.DirectivesNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro: macros){
            expansion = macro.build(local_context);
    
            if(this.DirectivesBeforeFirst != null){
                expansion = this.DirectivesBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.DirectivesAfterLast != null){
                expansion = this.DirectivesAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.DirectivesSeparator != null){
                expansion = this.DirectivesSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private InternalValue getInternalName(){
        return this.InternalNameValue;
    }
    
    private InternalValue getType(){
        return this.TypeValue;
    }
    
    private InternalValue getDirectives(){
        return this.DirectivesValue;
    }
    private void initInternalNameInternals(Context context){
        for(Macro macro : this.list_InternalName){
            macro.apply(new InternalsInitializer("InternalName"){
                @Override
                void setName(MName mName){
                
                    
                    
                }
            });
        }
    }
    
    private void initTypeInternals(Context context){
        for(Macro macro : this.list_Type){
            macro.apply(new InternalsInitializer("Type"){
                @Override
                void setStringType(MStringType mStringType){
                
                    
                    
                }
                
                @Override
                void setMacroType(MMacroType mMacroType){
                
                    
                    
                }
            });
        }
    }
    
    private void initDirectivesInternals(Context context){
        for(Macro macro : this.list_Directives){
            macro.apply(new InternalsInitializer("Directives"){
                @Override
                void setDirective(MDirective mDirective){
                
                    
                    
                }
            });
        }
    }
    
    private void initInternalNameDirectives(){
        
    }
    
    private void initTypeDirectives(){
        
    }
    
    private void initDirectivesDirectives(){
        StringBuilder sb1 = new StringBuilder();
        sb1.append(", ");
        this.DirectivesSeparator = new DSeparator(sb1.toString());
        this.DirectivesValue.setSeparator(this.DirectivesSeparator);
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setInternal(this);
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
    
        initInternalNameDirectives();
        initTypeDirectives();
        initDirectivesDirectives();
        
        initInternalNameInternals(null);
        initTypeInternals(null);
        initDirectivesInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("Internal ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        indentations.add(sb2.toString());
        sb1.append(buildInternalName());
        sb1.append(LINE_SEPARATOR);
        sb1.append(buildType());
        sb1.append(LINE_SEPARATOR);
        sb1.append(buildDirectives());
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