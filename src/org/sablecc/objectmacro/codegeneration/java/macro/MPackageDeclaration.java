/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

public class MPackageDeclaration
        extends
        Macro {

    private String field_PackageName;

    public MPackageDeclaration(
            String pPackageName) {

        setPPackageName(pPackageName);
    }

    private void setPPackageName(
            String pPackageName) {

        if (pPackageName == null) {
            throw ObjectMacroException.parameterNull("PackageName");
        }

        this.field_PackageName = pPackageName;
    }

    private String buildPackageName() {

        return this.field_PackageName;
    }

    private String getPackageName() {

        return this.field_PackageName;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setPackageDeclaration(this);
    }

    @Override
    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {
            buildState = new BuildState();
        }
        else if (buildState.getExpansion() == null) {
            throw ObjectMacroException.cyclicReference("PackageDeclaration");
        }
        else {
            return buildState.getExpansion();
        }
        this.build_state = buildState;

        StringBuilder sb0 = new StringBuilder();

        sb0.append("package ");
        sb0.append(buildPackageName());
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
