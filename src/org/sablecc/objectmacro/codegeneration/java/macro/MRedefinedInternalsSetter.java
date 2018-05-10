/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MRedefinedInternalsSetter extends Macro{
    
    private String field_Name;
    
    
    private final List<Macro> list_MacroBodyParts;
    
    
    private DSeparator MacroBodyPartsSeparator;
    
    private DBeforeFirst MacroBodyPartsBeforeFirst;
    
    private DAfterLast MacroBodyPartsAfterLast;
    
    private DNone MacroBodyPartsNone;
    
    
    private final InternalValue MacroBodyPartsValue;
    
    
    private final List<Macro> list_SetInternals;
    
    
    private DSeparator SetInternalsSeparator;
    
    private DBeforeFirst SetInternalsBeforeFirst;
    
    private DAfterLast SetInternalsAfterLast;
    
    private DNone SetInternalsNone;
    
    
    private final InternalValue SetInternalsValue;
    
    private final Context MacroBodyPartsContext = new Context();private final Context SetInternalsContext = new Context();
    
    public MRedefinedInternalsSetter(String pName){
        
                this.setPName(pName);
        
            this.list_MacroBodyParts = new ArrayList<>();    this.list_SetInternals = new ArrayList<>();
        
            this.MacroBodyPartsValue = new InternalValue(this.list_MacroBodyParts, this.MacroBodyPartsContext);    this.SetInternalsValue = new InternalValue(this.list_SetInternals, this.SetInternalsContext);
    }
    
    private void setPName( String pName ){
        if(pName == null){
            throw ObjectMacroException.parameterNull("Name");
        }
    
        this.field_Name = pName;
    }
        public void addMacroBodyParts(MInitStringBuilder macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("MacroBodyParts");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("InitStringBuilder");
            }
    
            this.list_MacroBodyParts.add(macro);
        }
        public void addMacroBodyParts(MStringPart macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("MacroBodyParts");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("StringPart");
            }
    
            this.list_MacroBodyParts.add(macro);
        }
        public void addMacroBodyParts(MParamInsertPart macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("MacroBodyParts");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("ParamInsertPart");
            }
    
            this.list_MacroBodyParts.add(macro);
        }
        public void addMacroBodyParts(MEolPart macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("MacroBodyParts");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("EolPart");
            }
    
            this.list_MacroBodyParts.add(macro);
        }
        public void addMacroBodyParts(MInsertMacroPart macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("MacroBodyParts");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("InsertMacroPart");
            }
    
            this.list_MacroBodyParts.add(macro);
        }
        public void addSetInternals(MSetInternal macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("SetInternals");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("SetInternal");
            }
    
            this.list_SetInternals.add(macro);
        }
    
    private String buildName(){
    
        return this.field_Name;
    }
    private String buildMacroBodyParts(){
        StringBuilder sb = new StringBuilder();
        Context local_context = MacroBodyPartsContext;
        List<Macro> macros = this.list_MacroBodyParts;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.MacroBodyPartsNone != null){
            sb.append(this.MacroBodyPartsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.MacroBodyPartsBeforeFirst != null){
                expansion = this.MacroBodyPartsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.MacroBodyPartsAfterLast != null){
                expansion = this.MacroBodyPartsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.MacroBodyPartsSeparator != null){
                expansion = this.MacroBodyPartsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    private String buildSetInternals(){
        StringBuilder sb = new StringBuilder();
        Context local_context = SetInternalsContext;
        List<Macro> macros = this.list_SetInternals;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.SetInternalsNone != null){
            sb.append(this.SetInternalsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.SetInternalsBeforeFirst != null){
                expansion = this.SetInternalsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.SetInternalsAfterLast != null){
                expansion = this.SetInternalsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.SetInternalsSeparator != null){
                expansion = this.SetInternalsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String getName(){
    
        return this.field_Name;
    }
    private InternalValue getMacroBodyParts(){
        return this.MacroBodyPartsValue;
    }
    private InternalValue getSetInternals(){
        return this.SetInternalsValue;
    }
    private void initMacroBodyPartsInternals(Context context){
        for(Macro macro : this.list_MacroBodyParts){
            macro.apply(new InternalsInitializer("MacroBodyParts"){
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
    private void initSetInternalsInternals(Context context){
        for(Macro macro : this.list_SetInternals){
            macro.apply(new InternalsInitializer("SetInternals"){
            @Override
            void setSetInternal(MSetInternal mSetInternal){
            
                
                
            }
            });
        }
    }
    
    private void initMacroBodyPartsDirectives(){
        StringBuilder sb0 = new StringBuilder();
        sb0.append(LINE_SEPARATOR);
        this.MacroBodyPartsSeparator = new DSeparator(sb0.toString());
        this.MacroBodyPartsValue.setSeparator(this.MacroBodyPartsSeparator);
    }
    private void initSetInternalsDirectives(){
        
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setRedefinedInternalsSetter(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("RedefinedInternalsSetter");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
    
    initMacroBodyPartsDirectives();
    initSetInternalsDirectives();
    
    initMacroBodyPartsInternals(null);
    initSetInternalsInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("@Override");
        sb0.append(LINE_SEPARATOR);
        sb0.append("void set");
        sb0.append(buildName());
        sb0.append("(M");
        sb0.append(buildName());
        sb0.append(" m");
        sb0.append(buildName());
        sb0.append(")");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    ");
        sb0.append(buildMacroBodyParts());
        sb0.append(LINE_SEPARATOR);
        sb0.append("    ");
        sb0.append(buildSetInternals());
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