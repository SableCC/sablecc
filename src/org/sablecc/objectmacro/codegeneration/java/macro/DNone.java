/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

class DNone
        extends
        Directive {

    DNone(
            String value) {

        super(value);
    }

    @Override
    String apply(
            Integer index,
            String macro,
            Integer list_size) {

        if (list_size == 0) {
            return this.value;
        }

        return macro;
    }
}
