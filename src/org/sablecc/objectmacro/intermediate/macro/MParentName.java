/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

public  class MParentName extends Macro{
    
    final List<Macro> list_Parent;
    
    final Context ParentContext = new Context();
    
    final InternalValue ParentValue;
    
    private DSeparator ParentSeparator;
    
    private DBeforeFirst ParentBeforeFirst;
    
    private DAfterLast ParentAfterLast;
    
    private DNone ParentNone;
    
    public MParentName(Macros macros){
        
        
        this.setMacros(macros);
        this.list_Parent = new LinkedList<>();
        
        this.ParentValue = new InternalValue(this.list_Parent, this.ParentContext);
    }
    
    public void addParent(MName macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Parent");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("Name");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Parent.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    private String buildParent(){
        StringBuilder sb = new StringBuilder();
        Context local_context = ParentContext;
        List<Macro> macros = this.list_Parent;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.ParentNone != null){
            sb.append(this.ParentNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.ParentBeforeFirst != null){
                expansion = this.ParentBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.ParentAfterLast != null){
                expansion = this.ParentAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.ParentSeparator != null){
                expansion = this.ParentSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private InternalValue getParent(){
        return this.ParentValue;
    }
    private void initParentInternals(Context context){
        for(Macro macro : this.list_Parent){
            macro.apply(new InternalsInitializer("Parent"){
                @Override
                void setName(MName mName){
                
                    
                    
                }
            });
        }
    }
    
    private void initParentDirectives(){
        
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setParentName(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("ParentName");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        initParentDirectives();
        
        initParentInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("Parent ");
        sb0.append("{");
        sb0.append(" ");
        sb0.append(buildParent());
        sb0.append(" }");
    
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