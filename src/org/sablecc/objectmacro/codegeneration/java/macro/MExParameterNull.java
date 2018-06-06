/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;
public class MExParameterNull extends Macro {
    
    private DSeparator PackageDeclarationSeparator;
    
    private DBeforeFirst PackageDeclarationBeforeFirst;
    
    private DAfterLast PackageDeclarationAfterLast;
    
    private DNone PackageDeclarationNone;
    
    final List<Macro> list_PackageDeclaration;
    
    final Context PackageDeclarationContext = new Context();
    
    final MacroValue PackageDeclarationValue;
    
    MExParameterNull(Macros macros){
        
        this.setMacros(macros);
        this.list_PackageDeclaration = new LinkedList<>();
        
        this.PackageDeclarationValue = new MacroValue(this.list_PackageDeclaration, this.PackageDeclarationContext);
    }
    
    MExParameterNull(List<Macro> pPackageDeclaration, Macros macros){
        
        this.setMacros(macros);
        this.list_PackageDeclaration = new LinkedList<>();
        
        this.PackageDeclarationValue = new MacroValue(this.list_PackageDeclaration, this.PackageDeclarationContext);
        if (pPackageDeclaration != null) {
            this.addAllPackageDeclaration(pPackageDeclaration);
        }
    }
    
    public void addAllPackageDeclaration(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("PackageDeclaration");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "PackageDeclaration");
            }
        
            if(this.getMacros() != macro.getMacros()) {
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
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_PackageDeclaration.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    private String buildPackageDeclaration() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.PackageDeclarationContext;
        List<Macro> macros = this.list_PackageDeclaration;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.PackageDeclarationNone != null) {
            sb.append(this.PackageDeclarationNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.PackageDeclarationBeforeFirst != null) {
                expansion = this.PackageDeclarationBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.PackageDeclarationAfterLast != null) {
                expansion = this.PackageDeclarationAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.PackageDeclarationSeparator != null) {
                expansion = this.PackageDeclarationSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    MacroValue getPackageDeclaration() {
        return this.PackageDeclarationValue;
    }
    
    private void initPackageDeclarationInternals(Context context) {
        for(Macro macro : this.list_PackageDeclaration) {
            macro.apply(new InternalsInitializer("PackageDeclaration"){
                @Override
                void setPackageDeclaration(MPackageDeclaration mPackageDeclaration){
                    
                
                
                }
            });
        }
    }
    
    private void initPackageDeclarationDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.PackageDeclarationBeforeFirst = new DBeforeFirst(sb1.toString());
        this.PackageDeclarationValue.setBeforeFirst(this.PackageDeclarationBeforeFirst);
    }
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setExParameterNull(this);
    }
    
    
    public String build() {
    
        CacheBuilder cache_builder = this.cacheBuilder;
    
        if(cache_builder == null) {
            cache_builder = new CacheBuilder();
        }
        else if(cache_builder.getExpansion() == null) {
            throw new InternalException("Cycle detection detected lately");
        }
        else {
            return cache_builder.getExpansion();
        }
        this.cacheBuilder = cache_builder;
        List<String> indentations = new LinkedList<>();
    
        
        initPackageDeclarationDirectives();
        
        initPackageDeclarationInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
        
        MHeader m1 = this.getMacros().newHeader();
        
        
        
        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildPackageDeclaration());
        sb0.append(LINE_SEPARATOR);
        MImportJavaUtil m2 = this.getMacros().newImportJavaUtil();
        
        
        
        sb0.append(m2.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("class MUserErrorParameterNull extends Macro");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private String field_ParamName;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    public MUserErrorParameterNull(String pParamName)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        this.setPParamName(pParamName);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private void setPParamName( String pParamName )");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(pParamName == null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            throw ObjectMacroException.parameterNull(\"ParamName\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        this.field_ParamName = pParamName;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private String buildParamName()");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        return this.field_ParamName;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private String getParamName()");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        return this.field_ParamName;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    @Override");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    void apply(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            InternalsInitializer internalsInitializer)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        internalsInitializer.setUserErrorParameterNull(this);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    public String build()");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        CacheBuilder cache_builder = this.cacheBuilder;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(cache_builder == null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            cache_builder = new CacheBuilder();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        else");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            return cache_builder.getExpansion();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        this.cacheBuilder = cache_builder;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        List<String> indentations = new LinkedList<>();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        StringBuilder sbIndentation = new StringBuilder();");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        StringBuilder sb0 = new StringBuilder();");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        MObjectMacroUserErrorHead minsert_1 = new MObjectMacroUserErrorHead();");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb0.append(minsert_1.build(null));");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb0.append(LINE_SEPARATOR);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb0.append(LINE_SEPARATOR);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb0.append(\"Parameter '\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb0.append(buildParamName());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb0.append(\"' cannot be null.\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        cache_builder.setExpansion(sb0.toString());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        return sb0.toString();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    @Override");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    String build(Context context) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        return build();");
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