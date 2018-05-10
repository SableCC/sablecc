/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.sablecc.launcher.errormessage;

public class MInvalidDesinationDirectory {

  private final String pDestination;
  private final MInvalidDesinationDirectory mInvalidDesinationDirectory = this;

  public MInvalidDesinationDirectory(String pDestination) {
    if(pDestination == null) throw new NullPointerException();
    this.pDestination = pDestination;
  }

  String pDestination() {
    return this.pDestination;
  }

  private String rDestination() {
    return this.mInvalidDesinationDirectory.pDestination();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(new MCommandLineErrorHead().toString());
    sb.append(System.getProperty("line.separator"));
    sb.append("The \"");
    sb.append(rDestination());
    sb.append("\" destination is not a directory.");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append(new MCommandLineErrorTail().toString());
    return sb.toString();
  }

}
