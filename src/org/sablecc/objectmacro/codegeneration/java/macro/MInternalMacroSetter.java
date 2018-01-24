/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MInternalMacroSetter {

  private final String pParamName;
  private final MInternalMacroSetter mInternalMacroSetter = this;
  private final List<Object> eApplyInternalsInitializer = new LinkedList<Object>();

  public MInternalMacroSetter(String pParamName) {
    if(pParamName == null) throw new NullPointerException();
    this.pParamName = pParamName;
  }

  public MApplyInternalsInitializer newApplyInternalsInitializer(String pParamName) {
    MApplyInternalsInitializer lApplyInternalsInitializer = new MApplyInternalsInitializer(pParamName);
    this.eApplyInternalsInitializer.add(lApplyInternalsInitializer);
    return lApplyInternalsInitializer;
  }

  String pParamName() {
    return this.pParamName;
  }

  private String rParamName() {
    return this.mInternalMacroSetter.pParamName();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("    void set");
    sb.append(rParamName());
    sb.append("(");
    sb.append(System.getProperty("line.separator"));
    sb.append("            Context context,");
    sb.append(System.getProperty("line.separator"));
    sb.append("            List<Macro> macros) {");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        if(macros == null){");
    sb.append(System.getProperty("line.separator"));
    sb.append("            throw new RuntimeException(\"macros cannot be null\");");
    sb.append(System.getProperty("line.separator"));
    sb.append("        }");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        final List<Macro> tempMacros = new ArrayList<>();");
    sb.append(System.getProperty("line.separator"));
    sb.append("        int i = 0;");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        for(Macro macro : macros){");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("            if(macro == null){");
    sb.append(System.getProperty("line.separator"));
    sb.append("                throw ObjectMacroException.macroNull(i, \"param_name\");");
    sb.append(System.getProperty("line.separator"));
    sb.append("            }");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("            ");
    for(Object oApplyInternalsInitializer : this.eApplyInternalsInitializer) {
      sb.append(oApplyInternalsInitializer.toString());
    }
    sb.append(System.getProperty("line.separator"));
    sb.append("            tempMacros.add(macro);");
    sb.append(System.getProperty("line.separator"));
    sb.append("        }");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        this.list_");
    sb.append(rParamName());
    sb.append(".put(context, tempMacros);");
    sb.append(System.getProperty("line.separator"));
    sb.append("    }");
    sb.append(System.getProperty("line.separator"));
    return sb.toString();
  }

}
