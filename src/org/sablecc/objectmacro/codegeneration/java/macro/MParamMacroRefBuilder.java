/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MParamMacroRefBuilder
        extends Macro {

    String field_Name;

    final List<Macro> list_ContextName;

    final Context ContextNameContext = new Context();

    final InternalValue ContextNameValue;

    private DSeparator ContextNameSeparator;

    private DBeforeFirst ContextNameBeforeFirst;

    private DAfterLast ContextNameAfterLast;

    private DNone ContextNameNone;

    public MParamMacroRefBuilder(
            String pName,
            Macros macros) {

        setMacros(macros);
        setPName(pName);
        this.list_ContextName = new LinkedList<>();

        this.ContextNameValue = new InternalValue(this.list_ContextName,
                this.ContextNameContext);
    }

    private void setPName(
            String pName) {

        if (pName == null) {
            throw ObjectMacroException.parameterNull("Name");
        }

        this.field_Name = pName;
    }

    public void addContextName(
            MPlainText macro) {

        if (macro == null) {
            throw ObjectMacroException.parameterNull("ContextName");
        }
        if (this.build_state != null) {
            throw ObjectMacroException.cannotModify("PlainText");
        }

        if (getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }

        this.list_ContextName.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }

    String buildName() {

        return this.field_Name;
    }

    private String buildContextName() {

        StringBuilder sb = new StringBuilder();
        Context local_context = this.ContextNameContext;
        List<Macro> macros = this.list_ContextName;

        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;

        if (this.ContextNameNone != null) {
            sb.append(this.ContextNameNone.apply(i, "", nb_macros));
        }

        for (Macro macro : macros) {
            expansion = macro.build(local_context);

            if (this.ContextNameBeforeFirst != null) {
                expansion = this.ContextNameBeforeFirst.apply(i, expansion,
                        nb_macros);
            }

            if (this.ContextNameAfterLast != null) {
                expansion = this.ContextNameAfterLast.apply(i, expansion,
                        nb_macros);
            }

            if (this.ContextNameSeparator != null) {
                expansion = this.ContextNameSeparator.apply(i, expansion,
                        nb_macros);
            }

            sb.append(expansion);
            i++;
        }

        return sb.toString();
    }

    String getName() {

        return this.field_Name;
    }

    private InternalValue getContextName() {

        return this.ContextNameValue;
    }

    private void initContextNameInternals(
            Context context) {

        for (Macro macro : this.list_ContextName) {
            macro.apply(new InternalsInitializer("ContextName") {

                @Override
                void setPlainText(
                        MPlainText mPlainText) {

                }
            });
        }
    }

    private void initContextNameDirectives() {

        StringBuilder sb1 = new StringBuilder();
        sb1.append("context");
        this.ContextNameNone = new DNone(sb1.toString());
        this.ContextNameValue.setNone(this.ContextNameNone);
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setParamMacroRefBuilder(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("ParamMacroRefBuilder");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        initContextNameDirectives();

        initContextNameInternals(null);

        StringBuilder sb0 = new StringBuilder();

        sb0.append("private String build");
        sb0.append(buildName());
        sb0.append("()");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    StringBuilder sb = new StringBuilder();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    Context local_context = ");
        sb0.append(buildContextName());
        sb0.append(";");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    List<Macro> macros = this.list_");
        sb0.append(buildName());
        sb0.append(";");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    int i = 0;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    int nb_macros = macros.size();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    String expansion = null;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    if(this.");
        sb0.append(buildName());
        sb0.append("None != null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb.append(this.");
        sb0.append(buildName());
        sb0.append("None.apply(i, \"\", nb_macros));");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    for(Macro macro : macros)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        expansion = macro.build(local_context);");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(this.");
        sb0.append(buildName());
        sb0.append("BeforeFirst != null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            expansion = this.");
        sb0.append(buildName());
        sb0.append("BeforeFirst.apply(i, expansion, nb_macros);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(this.");
        sb0.append(buildName());
        sb0.append("AfterLast != null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            expansion = this.");
        sb0.append(buildName());
        sb0.append("AfterLast.apply(i, expansion, nb_macros);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(this.");
        sb0.append(buildName());
        sb0.append("Separator != null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            expansion = this.");
        sb0.append(buildName());
        sb0.append("Separator.apply(i, expansion, nb_macros);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        sb.append(expansion);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        i++;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return sb.toString();");
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
