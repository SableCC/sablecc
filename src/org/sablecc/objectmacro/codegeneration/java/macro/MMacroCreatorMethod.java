/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MMacroCreatorMethod
        extends Macro {

    String field_ClassName;

    final List<Macro> list_Args;

    final Context ArgsContext = new Context();

    final InternalValue ArgsValue;

    private DSeparator ArgsSeparator;

    private DBeforeFirst ArgsBeforeFirst;

    private DAfterLast ArgsAfterLast;

    private DNone ArgsNone;

    final List<Macro> list_Parameters;

    final Context ParametersContext = new Context();

    final InternalValue ParametersValue;

    private DSeparator ParametersSeparator;

    private DBeforeFirst ParametersBeforeFirst;

    private DAfterLast ParametersAfterLast;

    private DNone ParametersNone;

    final List<Macro> list_VersionFactory;

    final Context VersionFactoryContext = new Context();

    final InternalValue VersionFactoryValue;

    private DSeparator VersionFactorySeparator;

    private DBeforeFirst VersionFactoryBeforeFirst;

    private DAfterLast VersionFactoryAfterLast;

    private DNone VersionFactoryNone;

    public MMacroCreatorMethod(
            String pClassName,
            Macros macros) {

        setMacros(macros);
        setPClassName(pClassName);
        this.list_Args = new LinkedList<>();
        this.list_Parameters = new LinkedList<>();
        this.list_VersionFactory = new LinkedList<>();

        this.ArgsValue = new InternalValue(this.list_Args, this.ArgsContext);
        this.ParametersValue = new InternalValue(this.list_Parameters,
                this.ParametersContext);
        this.VersionFactoryValue = new InternalValue(this.list_VersionFactory,
                this.VersionFactoryContext);
    }

    private void setPClassName(
            String pClassName) {

        if (pClassName == null) {
            throw ObjectMacroException.parameterNull("ClassName");
        }

        this.field_ClassName = pClassName;
    }

    public void addArgs(
            MParamArg macro) {

        if (macro == null) {
            throw ObjectMacroException.parameterNull("Args");
        }
        if (this.build_state != null) {
            throw ObjectMacroException.cannotModify("ParamArg");
        }

        if (getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }

        this.list_Args.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }

    public void addParameters(
            MStringParam macro) {

        if (macro == null) {
            throw ObjectMacroException.parameterNull("Parameters");
        }
        if (this.build_state != null) {
            throw ObjectMacroException.cannotModify("StringParam");
        }

        if (getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }

        this.list_Parameters.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }

    public void addVersionFactory(
            MSwitchVersion macro) {

        if (macro == null) {
            throw ObjectMacroException.parameterNull("VersionFactory");
        }
        if (this.build_state != null) {
            throw ObjectMacroException.cannotModify("SwitchVersion");
        }

        if (getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }

        this.list_VersionFactory.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }

    String buildClassName() {

        return this.field_ClassName;
    }

    private String buildArgs() {

        StringBuilder sb = new StringBuilder();
        Context local_context = this.ArgsContext;
        List<Macro> macros = this.list_Args;

        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;

        if (this.ArgsNone != null) {
            sb.append(this.ArgsNone.apply(i, "", nb_macros));
        }

        for (Macro macro : macros) {
            expansion = macro.build(local_context);

            if (this.ArgsBeforeFirst != null) {
                expansion = this.ArgsBeforeFirst.apply(i, expansion, nb_macros);
            }

            if (this.ArgsAfterLast != null) {
                expansion = this.ArgsAfterLast.apply(i, expansion, nb_macros);
            }

            if (this.ArgsSeparator != null) {
                expansion = this.ArgsSeparator.apply(i, expansion, nb_macros);
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

        if (this.ParametersNone != null) {
            sb.append(this.ParametersNone.apply(i, "", nb_macros));
        }

        for (Macro macro : macros) {
            expansion = macro.build(local_context);

            if (this.ParametersBeforeFirst != null) {
                expansion = this.ParametersBeforeFirst.apply(i, expansion,
                        nb_macros);
            }

            if (this.ParametersAfterLast != null) {
                expansion = this.ParametersAfterLast.apply(i, expansion,
                        nb_macros);
            }

            if (this.ParametersSeparator != null) {
                expansion = this.ParametersSeparator.apply(i, expansion,
                        nb_macros);
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

        if (this.VersionFactoryNone != null) {
            sb.append(this.VersionFactoryNone.apply(i, "", nb_macros));
        }

        for (Macro macro : macros) {
            expansion = macro.build(local_context);

            if (this.VersionFactoryBeforeFirst != null) {
                expansion = this.VersionFactoryBeforeFirst.apply(i, expansion,
                        nb_macros);
            }

            if (this.VersionFactoryAfterLast != null) {
                expansion = this.VersionFactoryAfterLast.apply(i, expansion,
                        nb_macros);
            }

            if (this.VersionFactorySeparator != null) {
                expansion = this.VersionFactorySeparator.apply(i, expansion,
                        nb_macros);
            }

            sb.append(expansion);
            i++;
        }

        return sb.toString();
    }

    String getClassName() {

        return this.field_ClassName;
    }

    private InternalValue getArgs() {

        return this.ArgsValue;
    }

    private InternalValue getParameters() {

        return this.ParametersValue;
    }

    private InternalValue getVersionFactory() {

        return this.VersionFactoryValue;
    }

    private void initArgsInternals(
            Context context) {

        for (Macro macro : this.list_Args) {
            macro.apply(new InternalsInitializer("Args") {

                @Override
                void setParamArg(
                        MParamArg mParamArg) {

                }
            });
        }
    }

    private void initParametersInternals(
            Context context) {

        for (Macro macro : this.list_Parameters) {
            macro.apply(new InternalsInitializer("Parameters") {

                @Override
                void setStringParam(
                        MStringParam mStringParam) {

                }
            });
        }
    }

    private void initVersionFactoryInternals(
            Context context) {

        for (Macro macro : this.list_VersionFactory) {
            macro.apply(new InternalsInitializer("VersionFactory") {

                @Override
                void setSwitchVersion(
                        MSwitchVersion mSwitchVersion) {

                    mSwitchVersion.setClassName(
                            MMacroCreatorMethod.this.VersionFactoryContext,
                            getClassName());
                    mSwitchVersion.setArgs(
                            MMacroCreatorMethod.this.VersionFactoryContext,
                            getArgs());
                }
            });
        }
    }

    private void initArgsDirectives() {

        StringBuilder sb1 = new StringBuilder();
        sb1.append(", ");
        this.ArgsSeparator = new DSeparator(sb1.toString());
        this.ArgsValue.setSeparator(this.ArgsSeparator);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(", ");
        this.ArgsAfterLast = new DAfterLast(sb2.toString());
        this.ArgsValue.setAfterLast(this.ArgsAfterLast);
    }

    private void initParametersDirectives() {

        StringBuilder sb1 = new StringBuilder();
        sb1.append(", ");
        this.ParametersSeparator = new DSeparator(sb1.toString());
        this.ParametersValue.setSeparator(this.ParametersSeparator);
    }

    private void initVersionFactoryDirectives() {

        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.VersionFactoryBeforeFirst = new DBeforeFirst(sb1.toString());
        this.VersionFactoryValue.setBeforeFirst(this.VersionFactoryBeforeFirst);
        StringBuilder sb2 = new StringBuilder();
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

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setMacroCreatorMethod(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("MacroCreatorMethod");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        initArgsDirectives();
        initParametersDirectives();
        initVersionFactoryDirectives();

        initArgsInternals(null);
        initParametersInternals(null);
        initVersionFactoryInternals(null);

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
        sb0.append(applyIndent(sb1.toString(),
                indentations.remove(indentations.size() - 1)));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return m");
        sb0.append(buildClassName());
        sb0.append(";");
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(
            Context context) {

        return build();
    }

    private void setMacros(
            Macros macros) {

        if (macros == null) {
            throw new InternalException("macros cannot be null");
        }

        this.macros = macros;
    }
}
