/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MAddIndent extends Macro{
    
    private final List<Macro> list_MacroBodyParts;
    
    
    private DSeparator MacroBodyPartsSeparator;
    
    private DBeforeFirst MacroBodyPartsBeforeFirst;
    
    private DAfterLast MacroBodyPartsAfterLast;
    
    private DNone MacroBodyPartsNone;
    
    
    private final InternalValue MacroBodyPartsValue;
    
    private final Context MacroBodyPartsContext = new Context();
    
    public MAddIndent(){
        
        
            this.list_MacroBodyParts = new ArrayList<>();
        
            this.MacroBodyPartsValue = new InternalValue(this.list_MacroBodyParts, this.MacroBodyPartsContext);
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
    
    private InternalValue getMacroBodyParts(){
        return this.MacroBodyPartsValue;
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
    
    private void initMacroBodyPartsDirectives(){
        
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setAddIndent(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("AddIndent");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
    
    initMacroBodyPartsDirectives();
    
    initMacroBodyPartsInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("sbIndentation = new StringBuilder();");
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildMacroBodyParts());
        sb0.append(LINE_SEPARATOR);
        sb0.append("indentations.add(sbIndentation.toString());");
    
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