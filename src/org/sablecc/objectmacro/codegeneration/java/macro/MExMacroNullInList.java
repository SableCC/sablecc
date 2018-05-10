/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MExMacroNullInList extends Macro{
    
    private final List<Macro> list_PackageDeclaration;
    
    
    private DSeparator PackageDeclarationSeparator;
    
    private DBeforeFirst PackageDeclarationBeforeFirst;
    
    private DAfterLast PackageDeclarationAfterLast;
    
    private DNone PackageDeclarationNone;
    
    
    private final InternalValue PackageDeclarationValue;
    
    private final Context PackageDeclarationContext = new Context();
    
    public MExMacroNullInList(){
        
        
            this.list_PackageDeclaration = new ArrayList<>();
        
            this.PackageDeclarationValue = new InternalValue(this.list_PackageDeclaration, this.PackageDeclarationContext);
    }
    
        public void addPackageDeclaration(MPackageDeclaration macro){
            if(macro == null){
                throw ObjectMacroException.parameterNull("PackageDeclaration");
            }
                    if(this.build_state != null){
                throw ObjectMacroException.cannotModify("PackageDeclaration");
            }
    
            this.list_PackageDeclaration.add(macro);
        }
    
    private String buildPackageDeclaration(){
        StringBuilder sb = new StringBuilder();
        Context local_context = PackageDeclarationContext;
        List<Macro> macros = this.list_PackageDeclaration;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.PackageDeclarationNone != null){
            sb.append(this.PackageDeclarationNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros){
            expansion = macro.build(local_context);
    
            if(this.PackageDeclarationBeforeFirst != null){
                expansion = this.PackageDeclarationBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.PackageDeclarationAfterLast != null){
                expansion = this.PackageDeclarationAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.PackageDeclarationSeparator != null){
                expansion = this.PackageDeclarationSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private InternalValue getPackageDeclaration(){
        return this.PackageDeclarationValue;
    }
    private void initPackageDeclarationInternals(Context context){
        for(Macro macro : this.list_PackageDeclaration){
            macro.apply(new InternalsInitializer("PackageDeclaration"){
            @Override
            void setPackageDeclaration(MPackageDeclaration mPackageDeclaration){
            
                
                
            }
            });
        }
    }
    
    private void initPackageDeclarationDirectives(){
        StringBuilder sb0 = new StringBuilder();
        sb0.append(LINE_SEPARATOR);
        this.PackageDeclarationBeforeFirst = new DBeforeFirst(sb0.toString());
        this.PackageDeclarationValue.setBeforeFirst(this.PackageDeclarationBeforeFirst);
    }
    @Override
     void apply(
             InternalsInitializer internalsInitializer){
    
         internalsInitializer.setExMacroNullInList(this);
     }
    
    @Override
    public String build(){
    
        BuildState buildState = this.build_state;
    
        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("ExMacroNullInList");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_state = buildState;
        List<String> indentations = new LinkedList<>();
        StringBuilder sbIndentation = new StringBuilder();
    
    
    initPackageDeclarationDirectives();
    
    initPackageDeclarationInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        MHeader minsert_1 = new MHeader();
        
        
        sb0.append(minsert_1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildPackageDeclaration());
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("public class MMacroNullInList ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("  private final String pIndex;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  private final String pParamName;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  private final MMacroNullInList mMacroNullInList = this;");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("  public MMacroNullInList(String pIndex, String pParamName) ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    if(pIndex == null) throw new NullPointerException();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    this.pIndex = pIndex;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    if(pParamName == null) throw new NullPointerException();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    this.pParamName = pParamName;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("  String pIndex() ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return this.pIndex;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("  String pParamName() ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return this.pParamName;");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("  private String rIndex() ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return this.mMacroNullInList.pIndex();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("  private String rParamName() ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return this.mMacroNullInList.pParamName();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("  @Override");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  public String toString() ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    StringBuilder sb = new StringBuilder();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    sb.append(\"A macro is null at index \");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    sb.append(rIndex());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    sb.append(\" in the list '\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    sb.append(rParamName());");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    sb.append(\"'.\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    sb.append(System.getProperty(\"line.separator\"));");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return sb.toString();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");
    
        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }
    
    @Override
     String build(Context context) {
         return build();
     }
    private String applyIndent(
                            String macro,
                            String indent){

            StringBuilder sb = new StringBuilder();
            String[] lines = macro.split( "\n");

            if(lines.length > 1){
                for(int i = 0; i < lines.length; i++){
                    String line = lines[i];
                    sb.append(indent).append(line);

                    if(i < lines.length - 1){
                        sb.append(LINE_SEPARATOR);
                    }
                }
            }
            else{
                sb.append(indent).append(macro);
            }

            return sb.toString();
    }
}