/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MInsertMacroPart extends Macro{

    private String field_Name;
    private String field_IndexBuilder;
    private String field_IndexInsert;
    private final List<Macro> list_ListPart;
    private DSeparator ListPartSeparator;

    private DBeforeFirst ListPartBeforeFirst;

    private DAfterLast ListPartAfterLast;

    private DNone ListPartNone;
    private final InternalValue ListPartValue;
    private final List<Macro> list_ListSetInternal;
    private DSeparator ListSetInternalSeparator;

    private DBeforeFirst ListSetInternalBeforeFirst;

    private DAfterLast ListSetInternalAfterLast;

    private DNone ListSetInternalNone;
    private final InternalValue ListSetInternalValue;

    private final Context ListPartContext = new Context();    private final Context ListSetInternalContext = new Context();

    public MInsertMacroPart(String pName, String pIndexBuilder, String pIndexInsert){

        this.setPName(pName);        this.setPIndexBuilder(pIndexBuilder);        this.setPIndexInsert(pIndexInsert);

    this.list_ListPart = new ArrayList<>();    this.list_ListSetInternal = new ArrayList<>();

    this.ListPartValue = new InternalValue(this.list_ListPart, this.ListPartContext);    this.ListSetInternalValue = new InternalValue(this.list_ListSetInternal, this.ListSetInternalContext);
    }

    private void setPName( String pName ){
        if(pName == null){
            throw ObjectMacroException.parameterNull("Name");
        }

        this.field_Name = pName;
    }
    private void setPIndexBuilder( String pIndexBuilder ){
        if(pIndexBuilder == null){
            throw ObjectMacroException.parameterNull("IndexBuilder");
        }

        this.field_IndexBuilder = pIndexBuilder;
    }
    private void setPIndexInsert( String pIndexInsert ){
        if(pIndexInsert == null){
            throw ObjectMacroException.parameterNull("IndexInsert");
        }

        this.field_IndexInsert = pIndexInsert;
    }
    public void addListPart(MInitStringBuilder macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("ListPart");
        }
                if(this.build_state != null){
            throw ObjectMacroException.cannotModify("InitStringBuilder");
        }

        this.list_ListPart.add(macro);
    }
    public void addListPart(MStringPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("ListPart");
        }
                if(this.build_state != null){
            throw ObjectMacroException.cannotModify("StringPart");
        }

        this.list_ListPart.add(macro);
    }
    public void addListPart(MParamInsertPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("ListPart");
        }
                if(this.build_state != null){
            throw ObjectMacroException.cannotModify("ParamInsertPart");
        }

        this.list_ListPart.add(macro);
    }
    public void addListPart(MEolPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("ListPart");
        }
                if(this.build_state != null){
            throw ObjectMacroException.cannotModify("EolPart");
        }

        this.list_ListPart.add(macro);
    }
    public void addListPart(MInsertMacroPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("ListPart");
        }
                if(this.build_state != null){
            throw ObjectMacroException.cannotModify("InsertMacroPart");
        }

        this.list_ListPart.add(macro);
    }
    public void addListSetInternal(MSetInternal macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("ListSetInternal");
        }
                if(this.build_state != null){
            throw ObjectMacroException.cannotModify("SetInternal");
        }

        this.list_ListSetInternal.add(macro);
    }

    private String buildName(){

        return this.field_Name;
    }
    private String buildIndexBuilder(){

        return this.field_IndexBuilder;
    }
    private String buildIndexInsert(){

        return this.field_IndexInsert;
    }
    private String buildListPart(){
        StringBuilder sb = new StringBuilder();
        Context local_context = ListPartContext;
        List<Macro> macros = this.list_ListPart;

        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;

        if(this.ListPartNone != null){
            sb.append(this.ListPartNone.apply(i, "", nb_macros));
        }

        for(Macro macro : macros){
            expansion = macro.build(local_context);

            if(this.ListPartBeforeFirst != null){
                expansion = this.ListPartBeforeFirst.apply(i, expansion, nb_macros);
            }

            if(this.ListPartAfterLast != null){
                expansion = this.ListPartAfterLast.apply(i, expansion, nb_macros);
            }

            if(this.ListPartSeparator != null){
                expansion = this.ListPartSeparator.apply(i, expansion, nb_macros);
            }

            sb.append(expansion);
            i++;
        }

        return sb.toString();
    }
    private String buildListSetInternal(){
        StringBuilder sb = new StringBuilder();
        Context local_context = ListSetInternalContext;
        List<Macro> macros = this.list_ListSetInternal;

        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;

        if(this.ListSetInternalNone != null){
            sb.append(this.ListSetInternalNone.apply(i, "", nb_macros));
        }

        for(Macro macro : macros){
            expansion = macro.build(local_context);

            if(this.ListSetInternalBeforeFirst != null){
                expansion = this.ListSetInternalBeforeFirst.apply(i, expansion, nb_macros);
            }

            if(this.ListSetInternalAfterLast != null){
                expansion = this.ListSetInternalAfterLast.apply(i, expansion, nb_macros);
            }

            if(this.ListSetInternalSeparator != null){
                expansion = this.ListSetInternalSeparator.apply(i, expansion, nb_macros);
            }

            sb.append(expansion);
            i++;
        }

        return sb.toString();
    }

    private String getName(){

        return this.field_Name;
    }
    private String getIndexBuilder(){

        return this.field_IndexBuilder;
    }
    private String getIndexInsert(){

        return this.field_IndexInsert;
    }
    private InternalValue getListPart(){
        return this.ListPartValue;
    }
    private InternalValue getListSetInternal(){
        return this.ListSetInternalValue;
    }
    private void initListPartInternals(Context context){
        for(Macro macro : this.list_ListPart){
            macro.apply(new InternalsInitializer("ListPart"){
@Override
void setInitStringBuilder(MInitStringBuilder mInitStringBuilder){

    
    
}@Override
void setStringPart(MStringPart mStringPart){

    
    
}@Override
void setParamInsertPart(MParamInsertPart mParamInsertPart){

    
    
}@Override
void setEolPart(MEolPart mEolPart){

    
    
}@Override
void setInsertMacroPart(MInsertMacroPart mInsertMacroPart){

    
    
}
});
        }
    }
    private void initListSetInternalInternals(Context context){
        for(Macro macro : this.list_ListSetInternal){
            macro.apply(new InternalsInitializer("ListSetInternal"){
@Override
void setSetInternal(MSetInternal mSetInternal){

    
    
}
});
        }
    }

    private void initListPartDirectives(){
        
    }
    private void initListSetInternalDirectives(){
        
    }
    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setInsertMacroPart(this);
    }

   @Override
    public String build(){

        BuildState buildState = this.build_state;

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("InsertMacroPart");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        initListPartDirectives();
initListSetInternalDirectives();

        initListPartInternals(null);
initListSetInternalInternals(null);

        StringBuilder sb0 = new StringBuilder();

        sb0.append("        M");
        sb0.append(buildName());
        sb0.append(" minsert_");
        sb0.append(buildIndexInsert());
        sb0.append(" = new M");
        sb0.append(buildName());
        sb0.append("();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        ");
        sb0.append(buildListPart());
        sb0.append(LINE_SEPARATOR);
        sb0.append("        ");
        sb0.append(buildListSetInternal());
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb");
        sb0.append(buildIndexBuilder());
        sb0.append(".append(minsert_");
        sb0.append(buildIndexInsert());
        sb0.append(".build(null));");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(Context context) {
        return build();
    }
}