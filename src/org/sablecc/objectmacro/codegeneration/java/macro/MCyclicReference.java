/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

public class MCyclicReference {

  private final String pMacroName;
  private final MCyclicReference mCyclicReference = this;

  public MCyclicReference(String pMacroName) {
    if(pMacroName == null) throw new NullPointerException();
    this.pMacroName = pMacroName;
  }

  String pMacroName() {
    return this.pMacroName;
  }

  private String rMacroName() {
    return this.mCyclicReference.pMacroName();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(new MObjectMacroErrorHead().toString());
    sb.append(System.getProperty("line.separator"));
    sb.append("An instance of \"");
    sb.append(rMacroName());
    sb.append("\" is a cyclic reference to the same instance.");
    sb.append(System.getProperty("line.separator"));
    return sb.toString();
  }

}