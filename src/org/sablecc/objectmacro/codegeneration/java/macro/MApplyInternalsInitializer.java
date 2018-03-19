/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MApplyInternalsInitializer extends Macro{

    private String field_ParamName;

    private final List<Macro> list_ListRedefinedInternalsSetter;

    private DSeparator ListRedefinedInternalsSetterSeparator;

    private DBeforeFirst ListRedefinedInternalsSetterBeforeFirst;

    private DAfterLast ListRedefinedInternalsSetterAfterLast;

    private DNone ListRedefinedInternalsSetterNone;

    private final InternalValue ListRedefinedInternalsSetterValue;

    private final Context ListRedefinedInternalsSetterContext = new Context();

    public MApplyInternalsInitializer(String pParamName){

        this.setPParamName(pParamName);

    this.list_ListRedefinedInternalsSetter = new ArrayList<>();

    this.ListRedefinedInternalsSetterValue = new InternalValue(this.list_ListRedefinedInternalsSetter, this.ListRedefinedInternalsSetterContext);
    }

    private void setPParamName(String pParamName){
        if(pParamName == null){
            throw ObjectMacroException.parameterNull("ParamName");
        }

        this.field_ParamName = pParamName;
    }

    public void addListRedefinedInternalsSetter(MRedefinedInternalsSetter macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("ListRedefinedInternalsSetter");
        }
                if(this.build_state != null){
            throw ObjectMacroException.cannotModify("ApplyInternalsInitializer");
        }

        this.list_ListRedefinedInternalsSetter.add(macro);
    }

    private String buildParamName(){

        return this.field_ParamName;
    }

    private String buildListRedefinedInternalsSetter(){
        StringBuilder sb = new StringBuilder();
        Context local_context = ListRedefinedInternalsSetterContext;
        List<Macro> macros = this.list_ListRedefinedInternalsSetter;

        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;

        if(this.ListRedefinedInternalsSetterNone != null){
            sb.append(this.ListRedefinedInternalsSetterNone.apply(i, "", nb_macros));
        }

        for(Macro macro : macros){
            expansion = macro.build(local_context);

            if(this.ListRedefinedInternalsSetterBeforeFirst != null){
                expansion = this.ListRedefinedInternalsSetterBeforeFirst.apply(i, expansion, nb_macros);
            }

            if(this.ListRedefinedInternalsSetterAfterLast != null){
                expansion = this.ListRedefinedInternalsSetterAfterLast.apply(i, expansion, nb_macros);
            }

            if(this.ListRedefinedInternalsSetterSeparator != null){
                expansion = this.ListRedefinedInternalsSetterSeparator.apply(i, expansion, nb_macros);
            }

            sb.append(expansion);
            i++;
        }

        return sb.toString();
    }

    private String getParamName(){

        return this.field_ParamName;
    }

    private InternalValue getListRedefinedInternalsSetter(){
        return this.ListRedefinedInternalsSetterValue;
    }
    private void initListRedefinedInternalsSetterInternals(Context context){
        for(Macro macro : this.list_ListRedefinedInternalsSetter){
            macro.apply(new InternalsInitializer("ListRedefinedInternalsSetter"){
@Override
void setRedefinedInternalsSetter(MRedefinedInternalsSetter mRedefinedInternalsSetter){

        }
});
        }
    }

    private void initListRedefinedInternalsSetterDirectives(){
            }
    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setApplyInternalsInitializer(this);
    }

    @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("ApplyInternalsInitializer");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;

                initListRedefinedInternalsSetterDirectives();
        
                initListRedefinedInternalsSetterInternals(null);
        
        StringBuilder sb0 = new StringBuilder();

        sb0.append("macro.apply(new InternalsInitializer(\"");
        sb0.append(buildParamName());
        sb0.append("\")");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildListRedefinedInternalsSetter());
        sb0.append(LINE_SEPARATOR);
        sb0.append("});");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(Context context) {
        return build();
    }
}
