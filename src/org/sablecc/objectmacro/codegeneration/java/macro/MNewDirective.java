/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MNewDirective
        extends
        Macro {

    private String field_DirectiveName;

    private String field_IndexBuilder;

    private final List<Macro> list_MacroBodyParts;

    private DSeparator MacroBodyPartsSeparator;

    private DBeforeFirst MacroBodyPartsBeforeFirst;

    private DAfterLast MacroBodyPartsAfterLast;

    private DNone MacroBodyPartsNone;

    private final InternalValue MacroBodyPartsValue;

    private Map<Context, String> field_ParamName = new LinkedHashMap<>();

    private final Context MacroBodyPartsContext = new Context();

    public MNewDirective(
            String pDirectiveName,
            String pIndexBuilder) {

        setPDirectiveName(pDirectiveName);

        setPIndexBuilder(pIndexBuilder);

        this.list_MacroBodyParts = new ArrayList<>();

        this.MacroBodyPartsValue = new InternalValue(this.list_MacroBodyParts,
                this.MacroBodyPartsContext);

    }

    private void setPDirectiveName(
            String pDirectiveName) {

        if (pDirectiveName == null) {

            throw ObjectMacroException.parameterNull("DirectiveName");

        }

        this.field_DirectiveName = pDirectiveName;

    }

    private void setPIndexBuilder(
            String pIndexBuilder) {

        if (pIndexBuilder == null) {

            throw ObjectMacroException.parameterNull("IndexBuilder");

        }

        this.field_IndexBuilder = pIndexBuilder;

    }

    public void addMacroBodyParts(
            MStringPart macro) {

        if (macro == null) {

            throw ObjectMacroException.parameterNull("MacroBodyParts");

        }

        this.list_MacroBodyParts.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    public void addMacroBodyParts(
            MParamInsertPart macro) {

        if (macro == null) {

            throw ObjectMacroException.parameterNull("MacroBodyParts");

        }

        this.list_MacroBodyParts.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    public void addMacroBodyParts(
            MEolPart macro) {

        if (macro == null) {

            throw ObjectMacroException.parameterNull("MacroBodyParts");

        }

        this.list_MacroBodyParts.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    public void addMacroBodyParts(
            MInsertMacroPart macro) {

        if (macro == null) {

            throw ObjectMacroException.parameterNull("MacroBodyParts");

        }

        this.list_MacroBodyParts.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    void setParamName(

            Context context,

            String value) {

        if (value == null) {

            throw new RuntimeException("value cannot be null here");

        }

        this.field_ParamName.put(context, value);

    }

    private String buildDirectiveName() {

        return this.field_DirectiveName;

    }

    private String buildIndexBuilder() {

        return this.field_IndexBuilder;

    }

    private String buildMacroBodyParts() {

        StringBuilder sb = new StringBuilder();

        Context local_context = this.MacroBodyPartsContext;

        List<Macro> macros = this.list_MacroBodyParts;

        int i = 0;

        int nb_macros = macros.size();

        String expansion = null;

        if (this.MacroBodyPartsNone != null) {

            sb.append(this.MacroBodyPartsNone.apply(i, "", nb_macros));

        }

        for (Macro macro : macros) {

            expansion = macro.build(local_context);

            if (this.MacroBodyPartsBeforeFirst != null) {

                expansion = this.MacroBodyPartsBeforeFirst.apply(i, expansion,
                        nb_macros);

            }

            if (this.MacroBodyPartsAfterLast != null) {

                expansion = this.MacroBodyPartsAfterLast.apply(i, expansion,
                        nb_macros);

            }

            if (this.MacroBodyPartsSeparator != null) {

                expansion = this.MacroBodyPartsSeparator.apply(i, expansion,
                        nb_macros);

            }

            sb.append(expansion);

            i++;

        }

        return sb.toString();

    }

    private String buildParamName(
            Context context) {

        return this.field_ParamName.get(context);

    }

    private String getDirectiveName() {

        return this.field_DirectiveName;

    }

    private String getIndexBuilder() {

        return this.field_IndexBuilder;

    }

    private InternalValue getMacroBodyParts() {

        return this.MacroBodyPartsValue;

    }

    private String getParamName(
            Context context) {

        return this.field_ParamName.get(context);

    }

    private void initMacroBodyPartsInternals(
            Context context) {

        for (Macro macro : this.list_MacroBodyParts) {

            macro.apply(new InternalsInitializer("MacroBodyParts") {

                @Override

                void setStringPart(
                        MStringPart mStringPart) {

                }

                @Override

                void setParamInsertPart(
                        MParamInsertPart mParamInsertPart) {

                }

                @Override

                void setEolPart(
                        MEolPart mEolPart) {

                }

                @Override

                void setInsertMacroPart(
                        MInsertMacroPart mInsertMacroPart) {

                }

            });

        }

    }

    private void initMacroBodyPartsDirectives() {

    }

    @Override

    void apply(

            InternalsInitializer internalsInitializer) {

        internalsInitializer.setNewDirective(this);

    }

    @Override

    public String build(
            Context context) {

        BuildState buildState = this.build_states.get(context);

        if (buildState == null) {

            buildState = new BuildState();

        }

        else if (buildState.getExpansion() == null) {

            throw ObjectMacroException.cyclicReference("NewDirective");

        }

        else {

            return buildState.getExpansion();

        }

        this.build_states.put(context, buildState);

        List<String> indentations = new LinkedList<>();

        StringBuilder sbIndentation = new StringBuilder();

        initMacroBodyPartsDirectives();

        initMacroBodyPartsInternals(context);

        StringBuilder sb0 = new StringBuilder();

        sb0.append("StringBuilder sb");

        sb0.append(buildIndexBuilder());

        sb0.append(" = new StringBuilder();");

        sb0.append(LINE_SEPARATOR);

        sb0.append(buildMacroBodyParts());

        sb0.append(LINE_SEPARATOR);

        sb0.append("this.");

        sb0.append(buildParamName(context));

        sb0.append(buildDirectiveName());

        sb0.append(" = new D");

        sb0.append(buildDirectiveName());

        sb0.append("(sb");

        sb0.append(buildIndexBuilder());

        sb0.append(".toString());");

        sb0.append(LINE_SEPARATOR);

        sb0.append("this.");

        sb0.append(buildParamName(context));

        sb0.append("Value.set");

        sb0.append(buildDirectiveName());

        sb0.append("(this.");

        sb0.append(buildParamName(context));

        sb0.append(buildDirectiveName());

        sb0.append(");");

        buildState.setExpansion(sb0.toString());

        return sb0.toString();

    }

    private String applyIndent(
            String macro,
            String indent) {

        StringBuilder sb = new StringBuilder();
        String[] lines = macro.split("\n");

        if (lines.length > 1) {
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                sb.append(indent).append(line);

                if (i < lines.length - 1) {
                    sb.append(LINE_SEPARATOR);
                }
            }
        }
        else {
            sb.append(indent).append(macro);
        }

        return sb.toString();
    }
}
