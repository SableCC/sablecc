/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.sablecc.codegeneration.java.macro;

public class MNamedAlternativeHeader {

    private final MAlternative mAlternative;

    MNamedAlternativeHeader(
            MAlternative mAlternative) {

        if (mAlternative == null) {
            throw new NullPointerException();
        }
        this.mAlternative = mAlternative;
    }

    private String rName() {

        return this.mAlternative.pName();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("public abstract class N");
        sb.append(rName());
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

}
