/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

public  class MTextArgument extends Macro{
    
    String field_ParamName;
    
    final List<Macro> list_TextParts;
    
    final Context TextPartsContext = new Context();
    
    final InternalValue TextPartsValue;
    
    private DSeparator TextPartsSeparator;
    
    private DBeforeFirst TextPartsBeforeFirst;
    
    private DAfterLast TextPartsAfterLast;
    
    private DNone TextPartsNone;
    
    public MTextArgument(String pParamName, Macros macros){
        
        
        this.setMacros(macros);
        this.setPParamName(pParamName);
        this.list_TextParts = new LinkedList<>();
        
        this.TextPartsValue = new InternalValue(this.list_TextParts, this.TextPartsContext);
    }
    
    private void setPParamName( String pParamName ){
        if(pParamName == null){
            throw ObjectMacroException.parameterNull("ParamName");
        }
    
        this.field_ParamName = pParamName;
    }
    
    public void addAllTextParts(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("TextParts");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("TextArgument");
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "TextParts");
            }
        
            if(this.getMacros() != macro.getMacros()){
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeTextParts(macro);
            this.list_TextParts.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeTextParts (Macro macro) {
        macro.apply(new InternalsInitializer("TextParts"){
            @Override
            void setStringPart(MStringPart mStringPart){
            
                
                
            }
            
            @Override
            void setEolPart(MEolPart mEolPart){
            
                
                
            }
            
            @Override
            void setParamInsert(MParamInsert mParamInsert){
            
                
                
            }
            
            @Override
            void setMacroInsert(MMacroInsert mMacroInsert){
            
                
                
            }
        });
    }
    
    public void addTextParts(MStringPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("TextParts");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("TextArgument");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_TextParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addTextParts(MEolPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("TextParts");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("TextArgument");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_TextParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addTextParts(MParamInsert macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("TextParts");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("TextArgument");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_TextParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addTextParts(MMacroInsert macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("TextParts");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("TextArgument");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_TextParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    String buildParamName(){
    
        return this.field_ParamName;
    }
    
    private String buildTextParts(){
        StringBuilder sb = new StringBuilder();
        Context local_context = TextPartsContext;
        List<Macro> macros = this.list_TextParts;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.TextPartsNone != null){
            sb.append(this.TextPartsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.TextPartsBeforeFirst != null){
                expansion = this.TextPartsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.TextPartsAfterLast != null){
                expansion = this.TextPartsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.TextPartsSeparator != null){
                expansion = this.TextPartsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    String getParamName(){
    
        return this.field_ParamName;
    }
    
    private InternalValue getTextParts(){
        return this.TextPartsValue;
    }
    private void initTextPartsInternals(Context context){
        for(Macro macro : this.list_TextParts){
            macro.apply(new InternalsInitializer("TextParts"){
                @Override
                void setStringPart(MStringPart mStringPart){
                
                    
                    
                }
                
                @Override
                void setEolPart(MEolPart mEolPart){
                
                    
                    
                }
                
                @Override
                void setParamInsert(MParamInsert mParamInsert){
                
                    
                    
                }
                
                @Override
                void setMacroInsert(MMacroInsert mMacroInsert){
                
                    
                    
                }
            });
        }
    }
    
    private void initTextPartsDirectives(){
        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.TextPartsSeparator = new DSeparator(sb1.toString());
        this.TextPartsValue.setSeparator(this.TextPartsSeparator);
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setTextArgument(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("TextArgument");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        initTextPartsDirectives();
        
        initTextPartsInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("Value");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        indentations.add(sb2.toString());
        MParamName m1 = this.getMacros().newParamName();
        
        m1.setName(null, getParamName());
        sb1.append(m1.build(null));
        sb1.append(LINE_SEPARATOR);
        sb1.append(buildTextParts());
        sb0.append(applyIndent(sb1.toString(), indentations.remove(indentations.size() - 1)));
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