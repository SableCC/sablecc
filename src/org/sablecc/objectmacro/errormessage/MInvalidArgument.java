/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

public class MInvalidArgument {

  private final String pArgumentText;
  private final MInvalidArgument mInvalidArgument = this;

  public MInvalidArgument(String pArgumentText) {
    if(pArgumentText == null) throw new NullPointerException();
    this.pArgumentText = pArgumentText;
  }

  String pArgumentText() {
    return this.pArgumentText;
  }

  private String rArgumentText() {
    return this.mInvalidArgument.pArgumentText();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(new MCommandLineErrorHead().toString());
    sb.append(System.getProperty("line.separator"));
    sb.append("The following argument is rejected:");
    sb.append(System.getProperty("line.separator"));
    sb.append(" ");
    sb.append(rArgumentText());
    sb.append(System.getProperty("line.separator"));
    sb.append("It is invalid.");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append(new MCommandLineErrorTail().toString());
    return sb.toString();
  }

}
