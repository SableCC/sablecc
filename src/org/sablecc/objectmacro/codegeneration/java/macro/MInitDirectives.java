/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MInitDirectives {

  private final String pParamName;
  private final MInitDirectives mInitDirectives = this;
  private final List<Object> eNewDirective = new LinkedList<Object>();

  public MInitDirectives(String pParamName) {
    if(pParamName == null) throw new NullPointerException();
    this.pParamName = pParamName;
  }

  public MNewDirective newNewDirective(String pDirectiveName, String pIndexBuilder) {
    MNewDirective lNewDirective = new MNewDirective(pDirectiveName, pIndexBuilder, mInitDirectives);
    this.eNewDirective.add(lNewDirective);
    return lNewDirective;
  }

  String pParamName() {
    return this.pParamName;
  }

  private String rParamName() {
    return this.mInitDirectives.pParamName();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("    private void init");
    sb.append(rParamName());
    sb.append("Directives(){");
    sb.append(System.getProperty("line.separator"));
    sb.append("        ");
    for(Object oNewDirective : this.eNewDirective) {
      sb.append(oNewDirective.toString());
    }
    sb.append("    }");
    sb.append(System.getProperty("line.separator"));
    return sb.toString();
  }

}
