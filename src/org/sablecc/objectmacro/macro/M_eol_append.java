/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.macro;

public class M_eol_append
        extends Macro {

    // ---- constructor ----

    M_eol_append() {

    }

    // ---- parent ----

    @Override
    Macro get_parent() {

        return null;
    }

    // ---- appendTo ----

    @Override
    public void appendTo(
            StringBuilder sb) {

        sb.append("    sb.append(EOL);");
        sb.append(EOL);
    }

}