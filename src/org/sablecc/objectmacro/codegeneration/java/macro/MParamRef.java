/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MParamRef extends Macro{
    
    private String field_Name;
    
    
    private final List<Macro> list_GetParams;
    
    
    private DSeparator GetParamsSeparator;
    
    private DBeforeFirst GetParamsBeforeFirst;
    
    private DAfterLast GetParamsAfterLast;
    
    private DNone GetParamsNone;
    
    
    private final InternalValue GetParamsValue;
    
    private final Context GetParamsContext = new Context();
    
    public MParamRef(String pName){
        
                this.setPName(pName);
        
            this.list_GetParams = new ArrayList<>();
        
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
    
            this.list_GetParams.add(macro);
        }
        public void addGetParams(MContextName macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("GetParams");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("ContextName");
            }
    
            this.list_GetParams.add(macro);
        }
    
    private String buildName(){
    
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
    
    private String getName(){
    
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
            
                
                
            }@Override
            void setContextName(MContextName mContextName){
            
                
                
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
    private String applyIndent(
                            String macro,
                            String indent){

            StringBuilder sb = new StringBuilder();
            String[] lines = macro.split( "\n");

            if(lines.length > 1){
                for(int i = 0; i < lines.length; i++){
                    String line = lines[i];
                    sb.append(indent).append(line);

                    if(i < lines.length - 1){
                        sb.append(LINE_SEPARATOR);
                    }
                }
            }
            else{
                sb.append(indent).append(macro);
            }

            return sb.toString();
    }
}