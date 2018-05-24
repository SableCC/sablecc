/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public  class MSingleAdd extends Macro{
    
    String field_MacroName;
    
    String field_ParamName;
    
    final List<Macro> list_IsBuilt;
    
    final Context IsBuiltContext = new Context();
    
    final InternalValue IsBuiltValue;
    
    private DSeparator IsBuiltSeparator;
    
    private DBeforeFirst IsBuiltBeforeFirst;
    
    private DAfterLast IsBuiltAfterLast;
    
    private DNone IsBuiltNone;
    
    public MSingleAdd(String pMacroName, String pParamName, Macros macros){
        
        
        this.setMacros(macros);
        this.setPMacroName(pMacroName);
        this.setPParamName(pParamName);
        this.list_IsBuilt = new LinkedList<>();
        
        this.IsBuiltValue = new InternalValue(this.list_IsBuilt, this.IsBuiltContext);
    }
    
    private void setPMacroName( String pMacroName ){
        if(pMacroName == null){
            throw ObjectMacroException.parameterNull("MacroName");
        }
    
        this.field_MacroName = pMacroName;
    }
    
    private void setPParamName( String pParamName ){
        if(pParamName == null){
            throw ObjectMacroException.parameterNull("ParamName");
        }
    
        this.field_ParamName = pParamName;
    }
    
    public void addIsBuilt(MIsBuilt macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("IsBuilt");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("IsBuilt");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_IsBuilt.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    String buildMacroName(){
    
        return this.field_MacroName;
    }
    
    String buildParamName(){
    
        return this.field_ParamName;
    }
    
    private String buildIsBuilt(){
        StringBuilder sb = new StringBuilder();
        Context local_context = IsBuiltContext;
        List<Macro> macros = this.list_IsBuilt;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.IsBuiltNone != null){
            sb.append(this.IsBuiltNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.IsBuiltBeforeFirst != null){
                expansion = this.IsBuiltBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.IsBuiltAfterLast != null){
                expansion = this.IsBuiltAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.IsBuiltSeparator != null){
                expansion = this.IsBuiltSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    String getMacroName(){
    
        return this.field_MacroName;
    }
    
    String getParamName(){
    
        return this.field_ParamName;
    }
    
    private InternalValue getIsBuilt(){
        return this.IsBuiltValue;
    }
    private void initIsBuiltInternals(Context context){
        for(Macro macro : this.list_IsBuilt){
            macro.apply(new InternalsInitializer("IsBuilt"){
                @Override
                void setIsBuilt(MIsBuilt mIsBuilt){
                
                    
                    mIsBuilt.setMacroName(IsBuiltContext, getMacroName());
                }
            });
        }
    }
    
    private void initIsBuiltDirectives(){
        
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setSingleAdd(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("SingleAdd");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        initIsBuiltDirectives();
        
        initIsBuiltInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("public void add");
        sb0.append(buildParamName());
        sb0.append("(M");
        sb0.append(buildMacroName());
        sb0.append(" macro)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    if(macro == null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        throw ObjectMacroException.parameterNull(\"");
        sb0.append(buildParamName());
        sb0.append("\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        indentations.add(sb2.toString());
        sb1.append(buildIsBuilt());
        sb1.append(LINE_SEPARATOR);
        sb1.append(LINE_SEPARATOR);
        MFactoryComparison m1 = this.getMacros().newFactoryComparison();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("macro");
        m1.setVarName(null, sb3.toString());
        sb1.append(m1.build(null));
        sb0.append(applyIndent(sb1.toString(), indentations.remove(indentations.size() - 1)));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    this.list_");
        sb0.append(buildParamName());
        sb0.append(".add(macro);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    this.children.add(macro);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    Macro.cycleDetector.detectCycle(this, macro);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");
    
        buildState.setExpansion(sb0.toString());
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