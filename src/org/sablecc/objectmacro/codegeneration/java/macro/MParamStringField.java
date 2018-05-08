/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

public class MParamStringField
        extends
        Macro {

    private String field_Name;

    public MParamStringField(
            String pName) {

        setPName(pName);
    }

    private void setPName(
            String pName) {

        if (pName == null) {
            throw ObjectMacroException.parameterNull("Name");
        }

        this.field_Name = pName;
    }

    private String buildName() {

        return this.field_Name;
    }

    private String getName() {

        return this.field_Name;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setParamStringField(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("ParamStringField");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        StringBuilder sb0 = new StringBuilder();

        sb0.append("    private String field_");
        sb0.append(buildName());
        sb0.append(";");

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(
            Context context) {

        return build();
    }
}
