/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.macro;

import java.util.LinkedList;
import java.util.List;

public class M_macro_constructor_head
        extends Macro {

    // ---- constructor ----

    M_macro_constructor_head(
            Macro parent) {

        this.parent = parent;
    }

    // ---- parent ----

    private final Macro parent;

    @Override
    Macro get_parent() {

        return this.parent;
    }

    // ---- expands ----

    private final List<Macro> e_expand_4 = new LinkedList<Macro>();

    private final List<Macro> e_expand_5 = new LinkedList<Macro>();

    // ---- parameter accessors ----

    private String cached_p_macro_name;

    private String get_p_macro_name() {

        String result = this.cached_p_macro_name;

        if (result == null) {
            Macro current = this;

            while (!(current instanceof M_macro)) {
                current = current.get_parent();
            }

            result = ((M_macro) current).get_local_p_macro_name();
            this.cached_p_macro_name = result;
        }

        return result;
    }

    // ---- macro creators ----

    public M_macro_constructor_public new_macro_constructor_public() {

        M_macro_constructor_public result = new M_macro_constructor_public(this);
        this.e_expand_4.add(result);
        return result;
    }

    public M_macro_constructor_parent new_macro_constructor_parent() {

        M_macro_constructor_parent result = new M_macro_constructor_parent(this);
        this.e_expand_5.add(result);
        return result;
    }

    // ---- appendTo ----

    @Override
    public void appendTo(
            StringBuilder sb) {

        sb.append("  ");
        if (this.e_expand_4.size() == 0) {
        }
        else {
            boolean first = true;
            for (Macro macro : this.e_expand_4) {
                if (first) {
                    first = false;
                }
                else {
                }
                macro.appendTo(sb);
            }
        }
        sb.append("M_");
        sb.append(get_p_macro_name());
        sb.append("(");
        if (this.e_expand_5.size() == 0) {
        }
        else {
            boolean first = true;
            for (Macro macro : this.e_expand_5) {
                if (first) {
                    first = false;
                }
                else {
                }
                macro.appendTo(sb);
            }
        }
    }

}