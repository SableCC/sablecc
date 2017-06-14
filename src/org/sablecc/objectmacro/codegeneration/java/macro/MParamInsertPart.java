/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MParamInsertPart {

  private final String pParamName;
  private final String pIndexBuilder;
  private final MParamInsertPart mParamInsertPart = this;
  private final List<Object> eContextArg = new LinkedList<Object>();

  public MParamInsertPart(String pParamName, String pIndexBuilder) {
    if(pParamName == null) throw new NullPointerException();
    this.pParamName = pParamName;
    if(pIndexBuilder == null) throw new NullPointerException();
    this.pIndexBuilder = pIndexBuilder;
  }

  public MContextArg newContextArg() {
    MContextArg lContextArg = new MContextArg();
    this.eContextArg.add(lContextArg);
    return lContextArg;
  }

  String pParamName() {
    return this.pParamName;
  }

  String pIndexBuilder() {
    return this.pIndexBuilder;
  }

  private String rIndexBuilder() {
    return this.mParamInsertPart.pIndexBuilder();
  }

  private String rParamName() {
    return this.mParamInsertPart.pParamName();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("        sb");
    sb.append(rIndexBuilder());
    sb.append(".append(build");
    sb.append(rParamName());
    sb.append("(");
    for(Object oContextArg : this.eContextArg) {
      sb.append(oContextArg.toString());
    }
    sb.append("));");
    sb.append(System.getProperty("line.separator"));
    return sb.toString();
  }

}
