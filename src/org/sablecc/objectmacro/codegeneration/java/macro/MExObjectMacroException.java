/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.ArrayList;
import java.util.List;

public class MExObjectMacroException
        extends
        Macro {

    private final List<Macro> list_PackageDeclaration;

    private DSeparator PackageDeclarationSeparator;

    private DBeforeFirst PackageDeclarationBeforeFirst;

    private DAfterLast PackageDeclarationAfterLast;

    private DNone PackageDeclarationNone;

    private final InternalValue PackageDeclarationValue;

    private final Context PackageDeclarationContext = new Context();

    public MExObjectMacroException() {

        this.list_PackageDeclaration = new ArrayList<>();

        this.PackageDeclarationValue = new InternalValue(
                this.list_PackageDeclaration, this.PackageDeclarationContext);
    }

    public void addPackageDeclaration(
            MPackageDeclaration macro) {

        if (macro == null) {
            throw ObjectMacroException.parameterNull("PackageDeclaration");
        }
        if (this.build_state != null) {
            throw ObjectMacroException.cannotModify("ExObjectMacroException");
        }

        this.list_PackageDeclaration.add(macro);
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

        StringBuilder sb0 = new StringBuilder();
        sb0.append(LINE_SEPARATOR);
        this.PackageDeclarationBeforeFirst = new DBeforeFirst(sb0.toString());
        this.PackageDeclarationValue
                .setBeforeFirst(this.PackageDeclarationBeforeFirst);
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setExObjectMacroException(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException
                    .cyclicReference("ExObjectMacroException");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        initPackageDeclarationDirectives();

        initPackageDeclarationInternals(null);

        StringBuilder sb0 = new StringBuilder();

        MHeader minsert_1 = new MHeader();
        sb0.append(minsert_1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildPackageDeclaration());
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("public class ObjectMacroException");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        extends RuntimeException");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private ObjectMacroException(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String message)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        super(message);");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if(message == null)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "            throw new RuntimeException(\"message may not be null\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    private ObjectMacroException(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String message,");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            Throwable cause) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        super(message, cause);");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if (message == null) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "            throw new RuntimeException(\"message may not be null\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        if (cause == null) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "            throw new RuntimeException(\"cause may not be null\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("        }");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    static ObjectMacroException incorrectType(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String type,");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String param_name)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        return new ObjectMacroException(");
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "                new MIncorrectType(type, param_name).toString());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    static ObjectMacroException macroNull(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            Integer index,");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String paramName)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("        return new ObjectMacroException(");
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "                new MMacroNullInList(String.valueOf(index), paramName).toString());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    static ObjectMacroException parameterNull(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String paramName)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "        return new ObjectMacroException(new MParameterNull(paramName).toString());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    static ObjectMacroException cyclicReference(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String macroName)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "        return new ObjectMacroException(new MCyclicReference(macroName).toString());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("    static ObjectMacroException cannotModify(");
        sb0.append(LINE_SEPARATOR);
        sb0.append("            String macroName)");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append(
                "        return new ObjectMacroException(new MCannotModify(macroName).toString());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    }");
        sb0.append(LINE_SEPARATOR);
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
}
