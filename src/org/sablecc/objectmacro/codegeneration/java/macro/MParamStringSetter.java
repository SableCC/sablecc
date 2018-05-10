/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MParamStringSetter extends Macro{
    
    private String field_Name;
    
    
    private final List<Macro> list_StringParam;
    
    
    private DSeparator StringParamSeparator;
    
    private DBeforeFirst StringParamBeforeFirst;
    
    private DAfterLast StringParamAfterLast;
    
    private DNone StringParamNone;
    
    
    private final InternalValue StringParamValue;
    
    
    private final List<Macro> list_ParamArg;
    
    
    private DSeparator ParamArgSeparator;
    
    private DBeforeFirst ParamArgBeforeFirst;
    
    private DAfterLast ParamArgAfterLast;
    
    private DNone ParamArgNone;
    
    
    private final InternalValue ParamArgValue;
    
    private final Context StringParamContext = new Context();private final Context ParamArgContext = new Context();
    
    public MParamStringSetter(String pName){
        
                this.setPName(pName);
        
            this.list_StringParam = new ArrayList<>();    this.list_ParamArg = new ArrayList<>();
        
            this.StringParamValue = new InternalValue(this.list_StringParam, this.StringParamContext);    this.ParamArgValue = new InternalValue(this.list_ParamArg, this.ParamArgContext);
    }
    
    private void setPName( String pName ){
        if(pName == null){
            throw ObjectMacroException.parameterNull("Name");
        }
    
        this.field_Name = pName;
    }
        public void addStringParam(MStringParam macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("StringParam");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("StringParam");
            }
    
            this.list_StringParam.add(macro);
        }
        public void addParamArg(MParamArg macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("ParamArg");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("ParamArg");
            }
    
            this.list_ParamArg.add(macro);
        }
    
    private String buildName(){
    
        return this.field_Name;
    }
    private String buildStringParam(){
        StringBuilder sb = new StringBuilder();
        Context local_context = StringParamContext;
        List<Macro> macros = this.list_StringParam;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.StringParamNone != null){
            sb.append(this.StringParamNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.StringParamBeforeFirst != null){
                expansion = this.StringParamBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.StringParamAfterLast != null){
                expansion = this.StringParamAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.StringParamSeparator != null){
                expansion = this.StringParamSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    private String buildParamArg(){
        StringBuilder sb = new StringBuilder();
        Context local_context = ParamArgContext;
        List<Macro> macros = this.list_ParamArg;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.ParamArgNone != null){
            sb.append(this.ParamArgNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.ParamArgBeforeFirst != null){
                expansion = this.ParamArgBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.ParamArgAfterLast != null){
                expansion = this.ParamArgAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.ParamArgSeparator != null){
                expansion = this.ParamArgSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String getName(){
    
        return this.field_Name;
    }
    private InternalValue getStringParam(){
        return this.StringParamValue;
    }
    private InternalValue getParamArg(){
        return this.ParamArgValue;
    }
    private void initStringParamInternals(Context context){
        for(Macro macro : this.list_StringParam){
            macro.apply(new InternalsInitializer("StringParam"){
            @Override
            void setStringParam(MStringParam mStringParam){
            
                
                
            }
            });
        }
    }
    private void initParamArgInternals(Context context){
        for(Macro macro : this.list_ParamArg){
            macro.apply(new InternalsInitializer("ParamArg"){
            @Override
            void setParamArg(MParamArg mParamArg){
            
                
                
            }
            });
        }
    }
    
    private void initStringParamDirectives(){
        
    }
    private void initParamArgDirectives(){
        
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setParamStringSetter(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("ParamStringSetter");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
    
    initStringParamDirectives();
    initParamArgDirectives();
    
    initStringParamInternals(null);
    initParamArgInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("private void setP");
        sb0.append(buildName());
        sb0.append("( ");
        sb0.append(buildStringParam());
        sb0.append(" )");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    if(");
        sb0.append(buildParamArg());
        sb0.append(" == null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        throw ObjectMacroException.parameterNull(\"");
        sb0.append(buildName());
        sb0.append("\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    this.field_");
        sb0.append(buildName());
        sb0.append(" = ");
        sb0.append(buildParamArg());
        sb0.append(";");
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");
    
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