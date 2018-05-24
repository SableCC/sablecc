/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public  class MVersionEnumeration extends Macro{
    
    final List<Macro> list_PackageDeclaration;
    
    final Context PackageDeclarationContext = new Context();
    
    final InternalValue PackageDeclarationValue;
    
    private DSeparator PackageDeclarationSeparator;
    
    private DBeforeFirst PackageDeclarationBeforeFirst;
    
    private DAfterLast PackageDeclarationAfterLast;
    
    private DNone PackageDeclarationNone;
    
    final List<Macro> list_Versions;
    
    final Context VersionsContext = new Context();
    
    final InternalValue VersionsValue;
    
    private DSeparator VersionsSeparator;
    
    private DBeforeFirst VersionsBeforeFirst;
    
    private DAfterLast VersionsAfterLast;
    
    private DNone VersionsNone;
    
    public MVersionEnumeration(Macros macros){
        
        
        this.setMacros(macros);
        this.list_PackageDeclaration = new LinkedList<>();
        this.list_Versions = new LinkedList<>();
        
        this.PackageDeclarationValue = new InternalValue(this.list_PackageDeclaration, this.PackageDeclarationContext);
        this.VersionsValue = new InternalValue(this.list_Versions, this.VersionsContext);
    }
    
    public void addPackageDeclaration(MPackageDeclaration macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("PackageDeclaration");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("PackageDeclaration");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_PackageDeclaration.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addVersions(MPlainText macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Versions");
        }
        if(this.build_state != null){
            throw ObjectMacroException.cannotModify("PlainText");
        }
        
        if(this.getMacros() != macro.getMacros()){
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Versions.add(macro);
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
    
        for(Macro macro : macros){
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
    
    private String buildVersions(){
        StringBuilder sb = new StringBuilder();
        Context local_context = VersionsContext;
        List<Macro> macros = this.list_Versions;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.VersionsNone != null){
            sb.append(this.VersionsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.VersionsBeforeFirst != null){
                expansion = this.VersionsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.VersionsAfterLast != null){
                expansion = this.VersionsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.VersionsSeparator != null){
                expansion = this.VersionsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private InternalValue getPackageDeclaration(){
        return this.PackageDeclarationValue;
    }
    
    private InternalValue getVersions(){
        return this.VersionsValue;
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
    
    private void initVersionsInternals(Context context){
        for(Macro macro : this.list_Versions){
            macro.apply(new InternalsInitializer("Versions"){
                @Override
                void setPlainText(MPlainText mPlainText){
                
                    
                    
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
    
    private void initVersionsDirectives(){
        StringBuilder sb1 = new StringBuilder();
        sb1.append(", ");
        this.VersionsSeparator = new DSeparator(sb1.toString());
        this.VersionsValue.setSeparator(this.VersionsSeparator);
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setVersionEnumeration(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("VersionEnumeration");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
        initPackageDeclarationDirectives();
        initVersionsDirectives();
        
        initPackageDeclarationInternals(null);
        initVersionsInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        MHeader m1 = this.getMacros().newHeader();
        
        
        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildPackageDeclaration());
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("public enum VERSIONS");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        indentations.add(sb2.toString());
        sb1.append(buildVersions());
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