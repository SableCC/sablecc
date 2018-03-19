/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

class DSeparator
        extends Directive {

    DSeparator(String value) {

        super(value);
    }

    @Override
    String apply(
            Integer index,
            String macro,
            Integer list_size) {

        if(index == list_size - 1){
            return macro;
        }

        return macro.concat(this.value);
    }
}
