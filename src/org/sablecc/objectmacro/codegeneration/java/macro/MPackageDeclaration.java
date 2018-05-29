/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MPackageDeclaration extends Macro {
    
    private DSeparator PackageNameSeparator;
    
    private DBeforeFirst PackageNameBeforeFirst;
    
    private DAfterLast PackageNameAfterLast;
    
    private DNone PackageNameNone;
    
    final List<String> list_PackageName;
    
    final Context PackageNameContext = new Context();
    
    final StringValue PackageNameValue;
    
    MPackageDeclaration(Macros macros){
        
        
        this.setMacros(macros);
        this.list_PackageName = new LinkedList<>();
        
        this.PackageNameValue = new StringValue(this.list_PackageName, this.PackageNameContext);
    }
    
    public void addAllPackageName(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("PackageName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("PackageName");
            }
    
            this.list_PackageName.add(string);
        }
    }
    
    public void addPackageName(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("PackageName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_PackageName.add(string);
    }
    
    private String buildPackageName() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_PackageName;
    
        int i = 0;
        int nb_strings = strings.size();
    
        if(this.PackageNameNone != null) {
            sb.append(this.PackageNameNone.apply(i, "", nb_strings));
        }
    
        for(String string : strings) {
    
            if(this.PackageNameBeforeFirst != null) {
                string = this.PackageNameBeforeFirst.apply(i, string, nb_strings);
            }
    
            if(this.PackageNameAfterLast != null) {
                string = this.PackageNameAfterLast.apply(i, string, nb_strings);
            }
    
            if(this.PackageNameSeparator != null) {
                string = this.PackageNameSeparator.apply(i, string, nb_strings);
            }
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getPackageName() {
        return this.PackageNameValue;
    }
    
    
    private void initPackageNameDirectives() {
        
    }
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setPackageDeclaration(this);
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
    
        initPackageNameDirectives();
    
    
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("package ");
        sb0.append(buildPackageName());
        sb0.append(";");
    
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