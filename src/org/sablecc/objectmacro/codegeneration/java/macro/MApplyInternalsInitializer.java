/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MApplyInternalsInitializer
        extends Macro {

    final List<Macro> list_RedefinedInternalsSetter;

    final Context RedefinedInternalsSetterContext = new Context();

    final InternalValue RedefinedInternalsSetterValue;

    private DSeparator RedefinedInternalsSetterSeparator;

    private DBeforeFirst RedefinedInternalsSetterBeforeFirst;

    private DAfterLast RedefinedInternalsSetterAfterLast;

    private DNone RedefinedInternalsSetterNone;

    private Map<Context, String> field_InternalName = new LinkedHashMap<>();

    public MApplyInternalsInitializer(
            Macros macros) {

        setMacros(macros);
        this.list_RedefinedInternalsSetter = new LinkedList<>();

        this.RedefinedInternalsSetterValue
                = new InternalValue(this.list_RedefinedInternalsSetter,
                        this.RedefinedInternalsSetterContext);
    }

    public void addRedefinedInternalsSetter(
            MRedefinedInternalsSetter macro) {

        if (macro == null) {
            throw ObjectMacroException
                    .parameterNull("RedefinedInternalsSetter");
        }

        if (getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }

        this.list_RedefinedInternalsSetter.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }

    void setInternalName(
            Context context,
            String value) {

        if (value == null) {
            throw new RuntimeException("value cannot be null here");
        }

        this.field_InternalName.put(context, value);
    }

    private String buildRedefinedInternalsSetter() {

        StringBuilder sb = new StringBuilder();
        Context local_context = this.RedefinedInternalsSetterContext;
        List<Macro> macros = this.list_RedefinedInternalsSetter;

        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;

        if (this.RedefinedInternalsSetterNone != null) {
            sb.append(
                    this.RedefinedInternalsSetterNone.apply(i, "", nb_macros));
        }

        for (Macro macro : macros) {
            expansion = macro.build(local_context);

            if (this.RedefinedInternalsSetterBeforeFirst != null) {
                expansion = this.RedefinedInternalsSetterBeforeFirst.apply(i,
                        expansion, nb_macros);
            }

            if (this.RedefinedInternalsSetterAfterLast != null) {
                expansion = this.RedefinedInternalsSetterAfterLast.apply(i,
                        expansion, nb_macros);
            }

            if (this.RedefinedInternalsSetterSeparator != null) {
                expansion = this.RedefinedInternalsSetterSeparator.apply(i,
                        expansion, nb_macros);
            }

            sb.append(expansion);
            i++;
        }

        return sb.toString();
    }

    String buildInternalName(
            Context context) {

        return this.field_InternalName.get(context);
    }

    private InternalValue getRedefinedInternalsSetter() {

        return this.RedefinedInternalsSetterValue;
    }

    String getInternalName(
            Context context) {

        return this.field_InternalName.get(context);
    }

    private void initRedefinedInternalsSetterInternals(
            Context context) {

        for (Macro macro : this.list_RedefinedInternalsSetter) {
            macro.apply(new InternalsInitializer("RedefinedInternalsSetter") {

                @Override
                void setRedefinedInternalsSetter(
                        MRedefinedInternalsSetter mRedefinedInternalsSetter) {

                }
            });
        }
    }

    private void initRedefinedInternalsSetterDirectives() {

        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        sb1.append(LINE_SEPARATOR);
        this.RedefinedInternalsSetterSeparator = new DSeparator(sb1.toString());
        this.RedefinedInternalsSetterValue
                .setSeparator(this.RedefinedInternalsSetterSeparator);
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setApplyInternalsInitializer(this);
    }

    @Override
    public String build(
            Context context) {

        BuildState buildState = this.build_states.get(context);

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException
                    .cyclicReference("ApplyInternalsInitializer");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_states.put(context, buildState);
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        initRedefinedInternalsSetterDirectives();

        initRedefinedInternalsSetterInternals(context);

        StringBuilder sb0 = new StringBuilder();

        sb0.append("macro.apply(new InternalsInitializer(\"");
        sb0.append(buildInternalName(context));
        sb0.append("\")");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("    ");
        indentations.add(sb2.toString());
        sb1.append(buildRedefinedInternalsSetter());
        sb0.append(applyIndent(sb1.toString(),
                indentations.remove(indentations.size() - 1)));
        sb0.append(LINE_SEPARATOR);
        sb0.append("});");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    private void setMacros(
            Macros macros) {

        if (macros == null) {
            throw new InternalException("macros cannot be null");
        }

        this.macros = macros;
    }
}
