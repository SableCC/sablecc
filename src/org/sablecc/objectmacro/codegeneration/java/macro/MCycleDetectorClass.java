/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MCycleDetectorClass {

  private final List<Object> ePackageDeclaration = new LinkedList<Object>();
  private final List<Object> eImportJavaUtil = new LinkedList<Object>();

  public MCycleDetectorClass() {
  }

  public MPackageDeclaration newPackageDeclaration(String pPackageName) {
    MPackageDeclaration lPackageDeclaration = new MPackageDeclaration(pPackageName);
    this.ePackageDeclaration.add(lPackageDeclaration);
    return lPackageDeclaration;
  }

  public MImportJavaUtil newImportJavaUtil() {
    MImportJavaUtil lImportJavaUtil = new MImportJavaUtil();
    this.eImportJavaUtil.add(lImportJavaUtil);
    return lImportJavaUtil;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(new MHeader().toString());
    if(this.ePackageDeclaration.size() > 0) {
      sb.append(System.getProperty("line.separator"));
    }
    for(Object oPackageDeclaration : this.ePackageDeclaration) {
      sb.append(oPackageDeclaration.toString());
    }
    if(this.eImportJavaUtil.size() > 0) {
      sb.append(System.getProperty("line.separator"));
    }
    for(Object oImportJavaUtil : this.eImportJavaUtil) {
      sb.append(oImportJavaUtil.toString());
    }
    sb.append(System.getProperty("line.separator"));
    sb.append("public class CycleDetector {");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("    private Macro receiver;");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("    private Set<Macro> visited;");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("    void detectCycle(");
    sb.append(System.getProperty("line.separator"));
    sb.append("            Macro receiver,");
    sb.append(System.getProperty("line.separator"));
    sb.append("            Macro added){");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        this.visited = new LinkedHashSet<>();");
    sb.append(System.getProperty("line.separator"));
    sb.append("        this.receiver = receiver;");
    sb.append(System.getProperty("line.separator"));
    sb.append("        detectCycle(added);");
    sb.append(System.getProperty("line.separator"));
    sb.append("    }");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("    private void detectCycle(");
    sb.append(System.getProperty("line.separator"));
    sb.append("            Macro macro){");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        if(this.visited.contains(macro)){");
    sb.append(System.getProperty("line.separator"));
    sb.append("            return;");
    sb.append(System.getProperty("line.separator"));
    sb.append("        }");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        if(macro == this.receiver){");
    sb.append(System.getProperty("line.separator"));
    sb.append("            throw ObjectMacroException.cyclicReference(macro.getClass().getSimpleName());");
    sb.append(System.getProperty("line.separator"));
    sb.append("        }");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        this.visited.add(macro);");
    sb.append(System.getProperty("line.separator"));
    sb.append(System.getProperty("line.separator"));
    sb.append("        for(Macro child : macro.getChildren()){");
    sb.append(System.getProperty("line.separator"));
    sb.append("            detectCycle(child);");
    sb.append(System.getProperty("line.separator"));
    sb.append("        }");
    sb.append(System.getProperty("line.separator"));
    sb.append("    }");
    sb.append(System.getProperty("line.separator"));
    sb.append("}");
    sb.append(System.getProperty("line.separator"));
    return sb.toString();
  }

}
