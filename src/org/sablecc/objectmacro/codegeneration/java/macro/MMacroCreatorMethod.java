/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;
import java.util.*;
public class MMacroCreatorMethod extends Macro {
    
    private DSeparator ClassNameSeparator;
    
    private DBeforeFirst ClassNameBeforeFirst;
    
    private DAfterLast ClassNameAfterLast;
    
    private DNone ClassNameNone;
    
    final List<String> list_ClassName;
    
    final Context ClassNameContext = new Context();
    
    final StringValue ClassNameValue;
    
    private DSeparator ArgsSeparator;
    
    private DBeforeFirst ArgsBeforeFirst;
    
    private DAfterLast ArgsAfterLast;
    
    private DNone ArgsNone;
    
    final List<Macro> list_Args;
    
    final Context ArgsContext = new Context();
    
    final MacroValue ArgsValue;
    
    private DSeparator VersionFactorySeparator;
    
    private DBeforeFirst VersionFactoryBeforeFirst;
    
    private DAfterLast VersionFactoryAfterLast;
    
    private DNone VersionFactoryNone;
    
    final List<Macro> list_VersionFactory;
    
    final Context VersionFactoryContext = new Context();
    
    final MacroValue VersionFactoryValue;
    
    private DSeparator ParametersSeparator;
    
    private DBeforeFirst ParametersBeforeFirst;
    
    private DAfterLast ParametersAfterLast;
    
    private DNone ParametersNone;
    
    final List<Macro> list_Parameters;
    
    final Context ParametersContext = new Context();
    
    final MacroValue ParametersValue;
    
    MMacroCreatorMethod(Macros macros){
        
        this.setMacros(macros);
        this.list_ClassName = new LinkedList<>();
        this.list_Args = new LinkedList<>();
        this.list_VersionFactory = new LinkedList<>();
        this.list_Parameters = new LinkedList<>();
        
        this.ClassNameValue = new StringValue(this.list_ClassName, this.ClassNameContext);
        this.ArgsValue = new MacroValue(this.list_Args, this.ArgsContext);
        this.VersionFactoryValue = new MacroValue(this.list_VersionFactory, this.VersionFactoryContext);
        this.ParametersValue = new MacroValue(this.list_Parameters, this.ParametersContext);
    }
    
    MMacroCreatorMethod(String pClassName, List<Macro> pArgs, List<Macro> pVersionFactory, List<Macro> pParameters, Macros macros){
        
        this.setMacros(macros);
        this.list_ClassName = new LinkedList<>();
        this.list_Args = new LinkedList<>();
        this.list_VersionFactory = new LinkedList<>();
        this.list_Parameters = new LinkedList<>();
        
        this.ClassNameValue = new StringValue(this.list_ClassName, this.ClassNameContext);
        this.ArgsValue = new MacroValue(this.list_Args, this.ArgsContext);
        this.VersionFactoryValue = new MacroValue(this.list_VersionFactory, this.VersionFactoryContext);
        this.ParametersValue = new MacroValue(this.list_Parameters, this.ParametersContext);
        if (pArgs != null) {
            this.addAllArgs(pArgs);
        }
        if (pVersionFactory != null) {
            this.addAllVersionFactory(pVersionFactory);
        }
        if (pParameters != null) {
            this.addAllParameters(pParameters);
        }
        
        if (pClassName != null) {
            this.addClassName(pClassName);
        }
    }
    
    public void addAllClassName(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("ClassName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("ClassName");
            }
    
            this.list_ClassName.add(string);
        }
    }
    
    public void addClassName(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("ClassName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_ClassName.add(string);
    }
    
    public void addAllArgs(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("Args");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "Args");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeArgs(macro);
            this.list_Args.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeArgs (Macro macro) {
        macro.apply(new InternalsInitializer("Args"){
            @Override
            void setParamArg(MParamArg mParamArg){
                
            
            
            }
        });
    }
    
    public void addArgs(MParamArg macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Args");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Args.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addAllVersionFactory(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("VersionFactory");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "VersionFactory");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeVersionFactory(macro);
            this.list_VersionFactory.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeVersionFactory (Macro macro) {
        macro.apply(new InternalsInitializer("VersionFactory"){
            @Override
            void setSwitchVersion(MSwitchVersion mSwitchVersion){
                
            
            
            }
        });
    }
    
    public void addVersionFactory(MSwitchVersion macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("VersionFactory");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_VersionFactory.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addAllParameters(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("Parameters");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "Parameters");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeParameters(macro);
            this.list_Parameters.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeParameters (Macro macro) {
        macro.apply(new InternalsInitializer("Parameters"){
            @Override
            void setMacroParam(MMacroParam mMacroParam){
                
            
            
            }
            
            @Override
            void setStringParam(MStringParam mStringParam){
                
            
            
            }
        });
    }
    
    public void addParameters(MMacroParam macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Parameters");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Parameters.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addParameters(MStringParam macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("Parameters");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_Parameters.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    private String buildClassName() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_ClassName;
    
        int i = 0;
        int nb_strings = strings.size();
    
        if(this.ClassNameNone != null) {
            sb.append(this.ClassNameNone.apply(i, "", nb_strings));
        }
    
        for(String string : strings) {
    
            if(this.ClassNameBeforeFirst != null) {
                string = this.ClassNameBeforeFirst.apply(i, string, nb_strings);
            }
    
            if(this.ClassNameAfterLast != null) {
                string = this.ClassNameAfterLast.apply(i, string, nb_strings);
            }
    
            if(this.ClassNameSeparator != null) {
                string = this.ClassNameSeparator.apply(i, string, nb_strings);
            }
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildArgs() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.ArgsContext;
        List<Macro> macros = this.list_Args;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.ArgsNone != null) {
            sb.append(this.ArgsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.ArgsBeforeFirst != null) {
                expansion = this.ArgsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.ArgsAfterLast != null) {
                expansion = this.ArgsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.ArgsSeparator != null) {
                expansion = this.ArgsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildVersionFactory() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.VersionFactoryContext;
        List<Macro> macros = this.list_VersionFactory;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.VersionFactoryNone != null) {
            sb.append(this.VersionFactoryNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.VersionFactoryBeforeFirst != null) {
                expansion = this.VersionFactoryBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.VersionFactoryAfterLast != null) {
                expansion = this.VersionFactoryAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.VersionFactorySeparator != null) {
                expansion = this.VersionFactorySeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildParameters() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.ParametersContext;
        List<Macro> macros = this.list_Parameters;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.ParametersNone != null) {
            sb.append(this.ParametersNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.ParametersBeforeFirst != null) {
                expansion = this.ParametersBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.ParametersAfterLast != null) {
                expansion = this.ParametersAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.ParametersSeparator != null) {
                expansion = this.ParametersSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getClassName() {
        return this.ClassNameValue;
    }
    
    MacroValue getArgs() {
        return this.ArgsValue;
    }
    
    MacroValue getVersionFactory() {
        return this.VersionFactoryValue;
    }
    
    MacroValue getParameters() {
        return this.ParametersValue;
    }
    
    private void initArgsInternals(Context context) {
        for(Macro macro : this.list_Args) {
            macro.apply(new InternalsInitializer("Args"){
                @Override
                void setParamArg(MParamArg mParamArg){
                    
                
                
                }
            });
        }
    }
    
    private void initVersionFactoryInternals(Context context) {
        for(Macro macro : this.list_VersionFactory) {
            macro.apply(new InternalsInitializer("VersionFactory"){
                @Override
                void setSwitchVersion(MSwitchVersion mSwitchVersion){
                    
                    
                    mSwitchVersion.setClassName(VersionFactoryContext, getClassName());
                    mSwitchVersion.setArgs(VersionFactoryContext, getArgs());
                }
            });
        }
    }
    
    private void initParametersInternals(Context context) {
        for(Macro macro : this.list_Parameters) {
            macro.apply(new InternalsInitializer("Parameters"){
                @Override
                void setMacroParam(MMacroParam mMacroParam){
                    
                
                
                }
                
                @Override
                void setStringParam(MStringParam mStringParam){
                    
                
                
                }
            });
        }
    }
    
    private void initClassNameDirectives() {
        
    }
    
    private void initArgsDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(", ");
        this.ArgsSeparator = new DSeparator(sb1.toString());
        this.ArgsValue.setSeparator(this.ArgsSeparator);StringBuilder sb2 = new StringBuilder();
        sb2.append(", ");
        this.ArgsAfterLast = new DAfterLast(sb2.toString());
        this.ArgsValue.setAfterLast(this.ArgsAfterLast);
    }
    
    private void initVersionFactoryDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.VersionFactoryBeforeFirst = new DBeforeFirst(sb1.toString());
        this.VersionFactoryValue.setBeforeFirst(this.VersionFactoryBeforeFirst);StringBuilder sb2 = new StringBuilder();
        sb2.append("m");
        sb2.append(buildClassName());
        sb2.append(" = new M");
        sb2.append(buildClassName());
        sb2.append("(");
        sb2.append(buildArgs());
        sb2.append("this);");
        this.VersionFactoryNone = new DNone(sb2.toString());
        this.VersionFactoryValue.setNone(this.VersionFactoryNone);
    }
    
    private void initParametersDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(", ");
        this.ParametersSeparator = new DSeparator(sb1.toString());
        this.ParametersValue.setSeparator(this.ParametersSeparator);
    }
    
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setMacroCreatorMethod(this);
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
    
        
        initClassNameDirectives();
        initArgsDirectives();
        initVersionFactoryDirectives();
        initParametersDirectives();
        
        initArgsInternals(null);
        initVersionFactoryInternals(null);
        initParametersInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
        
        sb0.append("public M");
        sb0.append(buildClassName());
        sb0.append(" new");
        sb0.append(buildClassName());
        sb0.append("(");
        sb0.append(buildParameters());
        sb0.append(")");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    M");
        sb0.append(buildClassName());
        sb0.append(" m");
        sb0.append(buildClassName());
        sb0.append(";");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        indentations.add(sb2.toString());
        sb1.append(buildVersionFactory());
        sb0.append(applyIndent(sb1.toString(), indentations.remove(indentations.size() - 1)));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return m");
        sb0.append(buildClassName());
        sb0.append(";");
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