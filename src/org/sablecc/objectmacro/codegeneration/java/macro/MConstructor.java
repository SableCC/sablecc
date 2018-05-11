/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MConstructor extends
        Macro {

    private String field_Name;

    private final List<Macro> list_FieldInitializers;

    private DSeparator FieldInitializersSeparator;

    private DBeforeFirst FieldInitializersBeforeFirst;

    private DAfterLast FieldInitializersAfterLast;

    private DNone FieldInitializersNone;

    private final InternalValue FieldInitializersValue;

    private final List<Macro> list_Parameters;

    private DSeparator ParametersSeparator;

    private DBeforeFirst ParametersBeforeFirst;

    private DAfterLast ParametersAfterLast;

    private DNone ParametersNone;

    private final InternalValue ParametersValue;

    private final List<Macro> list_InternalValuesInitializers;

    private DSeparator InternalValuesInitializersSeparator;

    private DBeforeFirst InternalValuesInitializersBeforeFirst;

    private DAfterLast InternalValuesInitializersAfterLast;

    private DNone InternalValuesInitializersNone;

    private final InternalValue InternalValuesInitializersValue;

    private final Context FieldInitializersContext = new Context();

    private final Context ParametersContext = new Context();

    private final Context InternalValuesInitializersContext = new Context();

    public MConstructor(
            String pName) {

        setPName(pName);

        this.list_FieldInitializers = new ArrayList<>();

        this.list_Parameters = new ArrayList<>();

        this.list_InternalValuesInitializers = new ArrayList<>();

        this.FieldInitializersValue = new InternalValue(
                this.list_FieldInitializers, this.FieldInitializersContext);

        this.ParametersValue = new InternalValue(this.list_Parameters,
                this.ParametersContext);

        this.InternalValuesInitializersValue
                = new InternalValue(this.list_InternalValuesInitializers,
                        this.InternalValuesInitializersContext);

    }

    private void setPName(
            String pName) {

        if (pName == null) {

            throw ObjectMacroException.parameterNull("Name");

        }

        this.field_Name = pName;

    }

    public void addFieldInitializers(
            MSetParam macro) {

        if (macro == null) {

            throw ObjectMacroException.parameterNull("FieldInitializers");

        }

        if (this.build_state != null) {

            throw ObjectMacroException.cannotModify("SetParam");

        }

        this.list_FieldInitializers.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    public void addFieldInitializers(
            MInitMacroParam macro) {

        if (macro == null) {

            throw ObjectMacroException.parameterNull("FieldInitializers");

        }

        if (this.build_state != null) {

            throw ObjectMacroException.cannotModify("InitMacroParam");

        }

        this.list_FieldInitializers.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    public void addFieldInitializers(
            MInitMacroInternal macro) {

        if (macro == null) {

            throw ObjectMacroException.parameterNull("FieldInitializers");

        }

        if (this.build_state != null) {

            throw ObjectMacroException.cannotModify("InitMacroInternal");

        }

        this.list_FieldInitializers.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    public void addFieldInitializers(
            MInitStringInternal macro) {

        if (macro == null) {

            throw ObjectMacroException.parameterNull("FieldInitializers");

        }

        if (this.build_state != null) {

            throw ObjectMacroException.cannotModify("InitStringInternal");

        }

        this.list_FieldInitializers.add(macro);

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

        this.list_Parameters.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    public void addInternalValuesInitializers(
            MInitInternalValue macro) {

        if (macro == null) {

            throw ObjectMacroException
                    .parameterNull("InternalValuesInitializers");

        }

        if (this.build_state != null) {

            throw ObjectMacroException.cannotModify("InitInternalValue");

        }

        this.list_InternalValuesInitializers.add(macro);

        this.children.add(macro);

        Macro.cycleDetector.detectCycle(this, macro);

    }

    private String buildName() {

        return this.field_Name;

    }

    private String buildFieldInitializers() {

        StringBuilder sb = new StringBuilder();

        Context local_context = this.FieldInitializersContext;

        List<Macro> macros = this.list_FieldInitializers;

        int i = 0;

        int nb_macros = macros.size();

        String expansion = null;

        if (this.FieldInitializersNone != null) {

            sb.append(this.FieldInitializersNone.apply(i, "", nb_macros));

        }

        for (Macro macro : macros) {

            expansion = macro.build(local_context);

            if (this.FieldInitializersBeforeFirst != null) {

                expansion = this.FieldInitializersBeforeFirst.apply(i,
                        expansion, nb_macros);

            }

            if (this.FieldInitializersAfterLast != null) {

                expansion = this.FieldInitializersAfterLast.apply(i, expansion,
                        nb_macros);

            }

            if (this.FieldInitializersSeparator != null) {

                expansion = this.FieldInitializersSeparator.apply(i, expansion,
                        nb_macros);

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

    private String buildInternalValuesInitializers() {

        StringBuilder sb = new StringBuilder();

        Context local_context = this.InternalValuesInitializersContext;

        List<Macro> macros = this.list_InternalValuesInitializers;

        int i = 0;

        int nb_macros = macros.size();

        String expansion = null;

        if (this.InternalValuesInitializersNone != null) {

            sb.append(this.InternalValuesInitializersNone.apply(i, "",
                    nb_macros));

        }

        for (Macro macro : macros) {

            expansion = macro.build(local_context);

            if (this.InternalValuesInitializersBeforeFirst != null) {

                expansion = this.InternalValuesInitializersBeforeFirst.apply(i,
                        expansion, nb_macros);

            }

            if (this.InternalValuesInitializersAfterLast != null) {

                expansion = this.InternalValuesInitializersAfterLast.apply(i,
                        expansion, nb_macros);

            }

            if (this.InternalValuesInitializersSeparator != null) {

                expansion = this.InternalValuesInitializersSeparator.apply(i,
                        expansion, nb_macros);

            }

            sb.append(expansion);

            i++;

        }

        return sb.toString();

    }

    private String getName() {

        return this.field_Name;

    }

    private InternalValue getFieldInitializers() {

        return this.FieldInitializersValue;

    }

    private InternalValue getParameters() {

        return this.ParametersValue;

    }

    private InternalValue getInternalValuesInitializers() {

        return this.InternalValuesInitializersValue;

    }

    private void initFieldInitializersInternals(
            Context context) {

        for (Macro macro : this.list_FieldInitializers) {

            macro.apply(new InternalsInitializer("FieldInitializers") {

                @Override

                void setSetParam(
                        MSetParam mSetParam) {

                }

                @Override

                void setInitMacroParam(
                        MInitMacroParam mInitMacroParam) {

                }

                @Override

                void setInitMacroInternal(
                        MInitMacroInternal mInitMacroInternal) {

                }

                @Override

                void setInitStringInternal(
                        MInitStringInternal mInitStringInternal) {

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

    private void initInternalValuesInitializersInternals(
            Context context) {

        for (Macro macro : this.list_InternalValuesInitializers) {

            macro.apply(new InternalsInitializer("InternalValuesInitializers") {

                @Override

                void setInitInternalValue(
                        MInitInternalValue mInitInternalValue) {

                }

            });

        }

    }

    private void initFieldInitializersDirectives() {

        StringBuilder sb0 = new StringBuilder();

        sb0.append(Macro.LINE_SEPARATOR);

        this.FieldInitializersBeforeFirst = new DBeforeFirst(sb0.toString());

        this.FieldInitializersValue
                .setBeforeFirst(this.FieldInitializersBeforeFirst);
        StringBuilder sb1 = new StringBuilder();

        sb1.append(Macro.LINE_SEPARATOR);

        this.FieldInitializersSeparator = new DSeparator(sb1.toString());

        this.FieldInitializersValue
                .setSeparator(this.FieldInitializersSeparator);

    }

    private void initParametersDirectives() {

        StringBuilder sb0 = new StringBuilder();

        sb0.append(", ");

        this.ParametersSeparator = new DSeparator(sb0.toString());

        this.ParametersValue.setSeparator(this.ParametersSeparator);

    }

    private void initInternalValuesInitializersDirectives() {

        StringBuilder sb0 = new StringBuilder();

        sb0.append(Macro.LINE_SEPARATOR);

        this.InternalValuesInitializersBeforeFirst
                = new DBeforeFirst(sb0.toString());

        this.InternalValuesInitializersValue
                .setBeforeFirst(this.InternalValuesInitializersBeforeFirst);
        StringBuilder sb1 = new StringBuilder();

        sb1.append(Macro.LINE_SEPARATOR);

        this.InternalValuesInitializersSeparator
                = new DSeparator(sb1.toString());

        this.InternalValuesInitializersValue
                .setSeparator(this.InternalValuesInitializersSeparator);

    }

    @Override

    void apply(

            InternalsInitializer internalsInitializer) {

        internalsInitializer.setConstructor(this);

    }

    @Override

    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {

            buildState = new BuildState();

        }

        else if (buildState.getExpansion() == null) {

            throw ObjectMacroException.cyclicReference("Constructor");

        }

        else {

            return buildState.getExpansion();

        }

        this.build_state = buildState;

        List<String> indentations = new LinkedList<>();

        StringBuilder sbIndentation = new StringBuilder();

        initFieldInitializersDirectives();

        initParametersDirectives();

        initInternalValuesInitializersDirectives();

        initFieldInitializersInternals(null);

        initParametersInternals(null);

        initInternalValuesInitializersInternals(null);

        StringBuilder sb0 = new StringBuilder();

        sb0.append("public M");

        sb0.append(buildName());

        sb0.append("(");

        sb0.append(buildParameters());

        sb0.append(")");

        sb0.append("{");

        sb0.append(Macro.LINE_SEPARATOR);

        sb0.append(buildFieldInitializers());

        sb0.append(Macro.LINE_SEPARATOR);

        sb0.append(buildInternalValuesInitializers());

        sb0.append(Macro.LINE_SEPARATOR);

        sb0.append("}");

        buildState.setExpansion(sb0.toString());

        return sb0.toString();

    }

    @Override

    String build(
            Context context) {

        return build();

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
                    sb.append(Macro.LINE_SEPARATOR);
                }
            }
        }
        else {
            sb.append(indent).append(macro);
        }

        return sb.toString();
    }
}
