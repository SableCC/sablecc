/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.sablecc.core.errormessage;

public class MUndefinedAlternativeTransformationReference {

    private final String pName;

    private final String pProdName;

    private final String pLine;

    private final String pChar;

    private final MUndefinedAlternativeTransformationReference mUndefinedAlternativeTransformationReference = this;

    public MUndefinedAlternativeTransformationReference(
            String pName,
            String pProdName,
            String pLine,
            String pChar) {

        if (pName == null) {
            throw new NullPointerException();
        }
        this.pName = pName;
        if (pProdName == null) {
            throw new NullPointerException();
        }
        this.pProdName = pProdName;
        if (pLine == null) {
            throw new NullPointerException();
        }
        this.pLine = pLine;
        if (pChar == null) {
            throw new NullPointerException();
        }
        this.pChar = pChar;
    }

    String pName() {

        return this.pName;
    }

    String pProdName() {

        return this.pProdName;
    }

    String pLine() {

        return this.pLine;
    }

    String pChar() {

        return this.pChar;
    }

    private String rLine() {

        return this.mUndefinedAlternativeTransformationReference.pLine();
    }

    private String rChar() {

        return this.mUndefinedAlternativeTransformationReference.pChar();
    }

    private String rName() {

        return this.mUndefinedAlternativeTransformationReference.pName();
    }

    private String rProdName() {

        return this.mUndefinedAlternativeTransformationReference.pProdName();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(new MSemanticErrorHead().toString());
        sb.append(System.getProperty("line.separator"));
        sb.append("Line: ");
        sb.append(rLine());
        sb.append(System.getProperty("line.separator"));
        sb.append("Char: ");
        sb.append(rChar());
        sb.append(System.getProperty("line.separator"));
        sb.append("\"");
        sb.append(rName());
        sb.append("\" is not defined in production transformation of \"");
        sb.append(rProdName());
        sb.append("\".");
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

}
