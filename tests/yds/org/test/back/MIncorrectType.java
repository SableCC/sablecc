{Insert : header}

package org.test.back;


public class MIncorrectType {

  private final String pType;
  private final String pParamName;
  private final MIncorrectType mIncorrectType = this;

  public MIncorrectType(String pType, String pParamName) {
    if(pType == null) throw new NullPointerException();
    this.pType = pType;
    if(pParamName == null) throw new NullPointerException();
    this.pParamName = pParamName;
  }

  String pType() {
    return this.pType;
  }

  String pParamName() {
    return this.pParamName;
  }

  private String rType() {
    return this.mIncorrectType.pType();
  }

  private String rParamName() {
    return this.mIncorrectType.pParamName();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(new MObjectMacroErrorHead().toString());
    sb.append(System.getProperty("line.separator"));
    sb.append(rType());
    sb.append(" was not expected in parameter '");
    sb.append(rParamName());
    sb.append("'.");
    sb.append(System.getProperty("line.separator"));
    return sb.toString();
  }

}