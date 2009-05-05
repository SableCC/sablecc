/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

public class MParamConstructorInit {

    private final String pName;

    private final MParamConstructorInit mParamConstructorInit = this;

    public MParamConstructorInit(
            String pName) {

        this.pName = pName;
    }

    String pName() {

        return this.pName;
    }

    private String rName() {

        return this.mParamConstructorInit.pName();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("    if(p");
        sb.append(rName());
        sb.append(" == null) throw new NullPointerException();");
        sb.append(System.getProperty("line.separator"));
        sb.append("    this.p");
        sb.append(rName());
        sb.append(" = p");
        sb.append(rName());
        sb.append(";");
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

}
