/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

public  class MIndentPart extends Macro{
    
    final List<Macro> list_IndentationText;
    
    final Context IndentationTextContext = new Context();
    
    final InternalValue IndentationTextValue;
    
    private DSeparator IndentationTextSeparator;
    
    private DBeforeFirst IndentationTextBeforeFirst;
    
    private DAfterLast IndentationTextAfterLast;
    
    private DNone IndentationTextNone;
    
    MIndentPart(Macros macros){
        
        
        this.setMacros(macros);
        this.list_IndentationText = new LinkedList<>();
        
        this.IndentationTextValue = new InternalValue(this.list_IndentationText, this.IndentationTextContext);
    }
    
    public void addAllIndentationText(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("IndentationText");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("IndentPart");
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "IndentationText");
            }
        
            if(this.getMacros() != macro.getMacros()){
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeIndentationText(macro);
            this.list_IndentationText.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeIndentationText (Macro macro) {
        macro.apply(new InternalsInitializer("IndentationText"){
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
    
    public void addIndentationText(MStringPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("IndentationText");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("IndentPart");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_IndentationText.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addIndentationText(MEolPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("IndentationText");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("IndentPart");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_IndentationText.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addIndentationText(MParamInsert macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("IndentationText");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("IndentPart");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_IndentationText.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addIndentationText(MMacroInsert macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("IndentationText");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("IndentPart");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_IndentationText.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    private String buildIndentationText(){
        StringBuilder sb = new StringBuilder();
        Context local_context = IndentationTextContext;
        List<Macro> macros = this.list_IndentationText;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.IndentationTextNone != null){
            sb.append(this.IndentationTextNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.IndentationTextBeforeFirst != null){
                expansion = this.IndentationTextBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.IndentationTextAfterLast != null){
                expansion = this.IndentationTextAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.IndentationTextSeparator != null){
                expansion = this.IndentationTextSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private InternalValue getIndentationText(){
        return this.IndentationTextValue;
    }
    private void initIndentationTextInternals(Context context){
        for(Macro macro : this.list_IndentationText){
            macro.apply(new InternalsInitializer("IndentationText"){
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
    
    private void initIndentationTextDirectives(){
        StringBuilder sb3 = new StringBuilder();
        sb3.append(LINE_SEPARATOR);
        this.IndentationTextSeparator = new DSeparator(sb3.toString());
        this.IndentationTextValue.setSeparator(this.IndentationTextSeparator);
    }
    @Override
    void apply(
            InternalsInitializer internalsInitializer){
    
        internalsInitializer.setIndentPart(this);
    }
    
    
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
    
        initIndentationTextDirectives();
        
        initIndentationTextInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("Indent ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        indentations.add(sb2.toString());
        sb1.append(buildIndentationText());
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