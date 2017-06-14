/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.intermediate.macro;

import java.util.*;

public class MMacroRef {

  private final List<Object> eSimpleName = new LinkedList<Object>();
  private final List<Object> eArgs = new LinkedList<Object>();

  public MMacroRef() {
  }

  public MSimpleName newSimpleName(String pName) {
    MSimpleName lSimpleName = new MSimpleName(pName);
    this.eSimpleName.add(lSimpleName);
    return lSimpleName;
  }

  public MArgs newArgs() {
    MArgs lArgs = new MArgs();
    this.eArgs.add(lArgs);
    return lArgs;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(" MacroRef {");
    sb.append(System.getProperty("line.separator"));
    sb.append(" Name = ");
    if(this.eSimpleName.size() > 1) {
      sb.append("{ ");
    }
    {
      boolean first = true;
      for(Object oSimpleName : this.eSimpleName) {
        if(first) {
          first = false;
        }
        else {
          sb.append(", ");
        }
        sb.append(oSimpleName.toString());
      }
    }
    if(this.eSimpleName.size() > 1) {
      sb.append(" }");
    }
    for(Object oArgs : this.eArgs) {
      sb.append(oArgs.toString());
    }
    sb.append(" }");
    sb.append(System.getProperty("line.separator"));
    return sb.toString();
  }

}
