/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public  class MClassBeforeFirst extends Macro{
    
    final List<Macro> list_PackageDeclaration;
    
    final Context PackageDeclarationContext = new Context();
    
    final InternalValue PackageDeclarationValue;
    
    private DSeparator PackageDeclarationSeparator;
    
    private DBeforeFirst PackageDeclarationBeforeFirst;
    
    private DAfterLast PackageDeclarationAfterLast;
    
    private DNone PackageDeclarationNone;
    
    MClassBeforeFirst(Macros macros){
        
        
        this.setMacros(macros);
        this.list_PackageDeclaration = new LinkedList<>();
        
        this.PackageDeclarationValue = new InternalValue(this.list_PackageDeclaration, this.PackageDeclarationContext);
    }
    
    public void addAllPackageDeclaration(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("PackageDeclaration");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("ClassBeforeFirst");
        }
        
        int i = 0;
        
        for(Macro macro: macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "PackageDeclaration");
            }
        
            if(this.getMacros() != macro.getMacros()){
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypePackageDeclaration(macro);
            this.list_PackageDeclaration.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypePackageDeclaration (Macro macro) {
        macro.apply(new InternalsInitializer("PackageDeclaration"){
            @Override
            void setPackageDeclaration(MPackageDeclaration mPackageDeclaration){
            
                
                
            }
        });
    }
    
    public void addPackageDeclaration(MPackageDeclaration macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("PackageDeclaration");
        }
        if(this.cacheBuilder != null){
            throw ObjectMacroException.cannotModify("ClassBeforeFirst");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_PackageDeclaration.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    private String buildPackageDeclaration(){
        StringBuilder sb = new StringBuilder();
        Context local_context = PackageDeclarationContext;
        List<Macro> macros = this.list_PackageDeclaration;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.PackageDeclarationNone != null){
            sb.append(this.PackageDeclarationNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro: macros){
            expansion = macro.build(local_context);
    
            if(this.PackageDeclarationBeforeFirst != null){
                expansion = this.PackageDeclarationBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.PackageDeclarationAfterLast != null){
                expansion = this.PackageDeclarationAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.PackageDeclarationSeparator != null){
                expansion = this.PackageDeclarationSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private InternalValue getPackageDeclaration(){
        return this.PackageDeclarationValue;
    }
    private void initPackageDeclarationInternals(Context context){
        for(Macro macro : this.list_PackageDeclaration){
            macro.apply(new InternalsInitializer("PackageDeclaration"){
                @Override
                void setPackageDeclaration(MPackageDeclaration mPackageDeclaration){
                
                    
                    
                }
            });
        }
    }
    
    private void initPackageDeclarationDirectives(){
        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.PackageDeclarationBeforeFirst = new DBeforeFirst(sb1.toString());
        this.PackageDeclarationValue.setBeforeFirst(this.PackageDeclarationBeforeFirst);
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setClassBeforeFirst(this);
     }
    
    @Override
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
    
        initPackageDeclarationDirectives();
        
        initPackageDeclarationInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        MHeader m1 = this.getMacros().newHeader();
        
        
        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildPackageDeclaration());
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("class DBeforeFirst");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        extends Directive ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    DBeforeFirst(String value) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        super(value);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    @Override");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    String apply(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            Integer index,");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String macro,");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            Integer list_size) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(index == 0)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            return this.value.concat(macro);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        return macro;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");
        sb0.append(LINE_SEPARATOR);
    
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