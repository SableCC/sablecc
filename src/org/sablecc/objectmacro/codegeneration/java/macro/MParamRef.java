/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public  class MParamRef extends Macro{
    
    String field_Name;
    
    final List<Macro> list_GetParams;
    
    final Context GetParamsContext = new Context();
    
    final InternalValue GetParamsValue;
    
    private DSeparator GetParamsSeparator;
    
    private DBeforeFirst GetParamsBeforeFirst;
    
    private DAfterLast GetParamsAfterLast;
    
    private DNone GetParamsNone;
    
    public MParamRef(String pName, Macros macros){
        
        
        this.setMacros(macros);
        this.setPName(pName);
        this.list_GetParams = new LinkedList<>();
        
        this.GetParamsValue = new InternalValue(this.list_GetParams, this.GetParamsContext);
    }
    
    private void setPName( String pName ){
        if(pName == null){
            throw ObjectMacroException.parameterNull("Name");
        }
    
        this.field_Name = pName;
    }
    
    public void addGetParams(MContextArg macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("GetParams");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("ContextArg");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_GetParams.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addGetParams(MPlainText macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("GetParams");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("PlainText");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_GetParams.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    String buildName(){
    
        return this.field_Name;
    }
    
    private String buildGetParams(){
        StringBuilder sb = new StringBuilder();
        Context local_context = GetParamsContext;
        List<Macro> macros = this.list_GetParams;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.GetParamsNone != null){
            sb.append(this.GetParamsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.GetParamsBeforeFirst != null){
                expansion = this.GetParamsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.GetParamsAfterLast != null){
                expansion = this.GetParamsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.GetParamsSeparator != null){
                expansion = this.GetParamsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    String getName(){
    
        return this.field_Name;
    }
    
    private InternalValue getGetParams(){
        return this.GetParamsValue;
    }
    private void initGetParamsInternals(Context context){
        for(Macro macro : this.list_GetParams){
            macro.apply(new InternalsInitializer("GetParams"){
                @Override
                void setContextArg(MContextArg mContextArg){
                
                    
                    
                }
                
                @Override
                void setPlainText(MPlainText mPlainText){
                
                    
                    
                }
            });
        }
    }
    
    private void initGetParamsDirectives(){
        
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setParamRef(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("ParamRef");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        initGetParamsDirectives();
        
        initGetParamsInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("get");
        sb0.append(buildName());
        sb0.append("(");
        sb0.append(buildGetParams());
        sb0.append(")");
    
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