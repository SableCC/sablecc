/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.macro;

public class M_escape_append
        extends Macro {

    // ---- constructor ----

    M_escape_append(
            String p_char) {

        this.p_char = p_char;
    }

    // ---- parent ----

    @Override
    Macro get_parent() {

        return null;
    }

    // ---- parameters ----

    private final String p_char;

    String get_local_p_char() {

        return this.p_char;
    }

    // ---- parameter accessors ----

    private String cached_p_char;

    private String get_p_char() {

        String result = this.cached_p_char;

        if (result == null) {
            Macro current = this;

            while (!(current instanceof M_escape_append)) {
                current = current.get_parent();
            }

            result = ((M_escape_append) current).get_local_p_char();
            this.cached_p_char = result;
        }

        return result;
    }

    // ---- appendTo ----

    @Override
    public void appendTo(
            StringBuilder sb) {

        sb.append("    sb.append('");
        sb.append(get_p_char());
        sb.append("');");
        sb.append(EOL);
    }

}
