/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.LinkedList;
import java.util.List;

public class MEmptyBuilderWithContext extends
        Macro {

    public MEmptyBuilderWithContext() {

    }

    @Override

    void apply(

            InternalsInitializer internalsInitializer) {

        internalsInitializer.setEmptyBuilderWithContext(this);

    }

    @Override

    public String build() {

        BuildState buildState = this.build_state;

        if (buildState == null) {

            buildState = new BuildState();

        }

        else if (buildState.getExpansion() == null) {

            throw ObjectMacroException
                    .cyclicReference("EmptyBuilderWithContext");

        }

        else {

            return buildState.getExpansion();

        }

        this.build_state = buildState;

        List<String> indentations = new LinkedList<>();

        StringBuilder sbIndentation = new StringBuilder();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("@Override");

        sb0.append(Macro.LINE_SEPARATOR);

        sb0.append("String build(Context context) ");

        sb0.append("{");

        sb0.append(Macro.LINE_SEPARATOR);

        sb0.append(" return build();");

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
