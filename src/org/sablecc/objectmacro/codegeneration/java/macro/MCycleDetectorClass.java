/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MCycleDetectorClass
        extends Macro {

    final List<Macro> list_PackageDeclaration;

    final Context PackageDeclarationContext = new Context();

    final InternalValue PackageDeclarationValue;

    private DSeparator PackageDeclarationSeparator;

    private DBeforeFirst PackageDeclarationBeforeFirst;

    private DAfterLast PackageDeclarationAfterLast;

    private DNone PackageDeclarationNone;

    public MCycleDetectorClass(
            Macros macros) {

        setMacros(macros);
        this.list_PackageDeclaration = new LinkedList<>();

        this.PackageDeclarationValue = new InternalValue(
                this.list_PackageDeclaration, this.PackageDeclarationContext);
    }

    public void addPackageDeclaration(
            MPackageDeclaration macro) {

        if (macro == null) {
            throw ObjectMacroException.parameterNull("PackageDeclaration");
        }
        if (this.build_state != null) {
            throw ObjectMacroException.cannotModify("PackageDeclaration");
        }

        if (getMacros() != macro.getMacros()) {
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

        if (this.PackageDeclarationNone != null) {
            sb.append(this.PackageDeclarationNone.apply(i, "", nb_macros));
        }

        for (Macro macro : macros) {
            expansion = macro.build(local_context);

            if (this.PackageDeclarationBeforeFirst != null) {
                expansion = this.PackageDeclarationBeforeFirst.apply(i,
                        expansion, nb_macros);
            }

            if (this.PackageDeclarationAfterLast != null) {
                expansion = this.PackageDeclarationAfterLast.apply(i, expansion,
                        nb_macros);
            }

            if (this.PackageDeclarationSeparator != null) {
                expansion = this.PackageDeclarationSeparator.apply(i, expansion,
                        nb_macros);
            }

            sb.append(expansion);
            i++;
        }

        return sb.toString();
    }

    private InternalValue getPackageDeclaration() {

        return this.PackageDeclarationValue;
    }

    private void initPackageDeclarationInternals(
            Context context) {

        for (Macro macro : this.list_PackageDeclaration) {
            macro.apply(new InternalsInitializer("PackageDeclaration") {

                @Override
                void setPackageDeclaration(
                        MPackageDeclaration mPackageDeclaration) {

                }
            });
        }
    }

    private void initPackageDeclarationDirectives() {

        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.PackageDeclarationBeforeFirst = new DBeforeFirst(sb1.toString());
        this.PackageDeclarationValue
                .setBeforeFirst(this.PackageDeclarationBeforeFirst);
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setCycleDetectorClass(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("CycleDetectorClass");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();

        initPackageDeclarationDirectives();

        initPackageDeclarationInternals(null);

        StringBuilder sb0 = new StringBuilder();

        MHeader m1 = getMacros().newHeader();

        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildPackageDeclaration());
        sb0.append(LINE_SEPARATOR);
        MImportJavaUtil m2 = getMacros().newImportJavaUtil();

        sb0.append(m2.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("public class CycleDetector ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private Macro receiver;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private Set<Macro> visited;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    void detectCycle(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            Macro receiver,");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            Macro added)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        this.visited = new LinkedHashSet<>();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        this.receiver = receiver;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        detectCycle(added);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private void detectCycle(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            Macro macro)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(this.visited.contains(macro))");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            return;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(macro == this.receiver)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "            throw ObjectMacroException.cyclicReference(macro.getClass().getSimpleName());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        this.visited.add(macro);");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        for(Macro child : macro.getChildren())");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            detectCycle(child);");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
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