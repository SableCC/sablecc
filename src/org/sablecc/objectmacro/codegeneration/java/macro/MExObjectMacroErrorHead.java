/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

public class MExObjectMacroErrorHead extends Macro{

    private Macro list_PackageDeclaration[];

    private final Context PackageDeclarationContext = new Context();

    public MExObjectMacroErrorHead(Macro pPackageDeclaration[]){

        this.setPPackageDeclaration(pPackageDeclaration);
    }

    private void setPPackageDeclaration(Macro pPackageDeclaration[]){
        if(pPackageDeclaration == null){
            throw ObjectMacroException.parameterNull("PackageDeclaration");
        }

        Macro macros[] = pPackageDeclaration;
        this.list_PackageDeclaration = new Macro[macros.length];
        int i = 0;

        for(Macro macro : macros){
            if(macro == null){
                throw ObjectMacroException.macroNull(i, "PackageDeclaration");
            }

            macro.apply(new InternalsInitializer("PackageDeclaration"){
@Override
void setPackageDeclaration(MPackageDeclaration mPackageDeclaration){

        }
});

            this.list_PackageDeclaration[i++] = macro;

        }
    }

    private String buildPackageDeclaration(){

        StringBuilder sb0 = new StringBuilder();
        Context local_context = PackageDeclarationContext;
        Macro macros[] = this.list_PackageDeclaration;
                boolean first = true;
        int i = 0;

        for(Macro macro : macros){
            if(first){
            sb0.append(LINE_SEPARATOR);
    first = false;
}
            
            sb0.append(macro.build(local_context));
            i++;

                    }

        return sb0.toString();
    }

    private Macro[] getPackageDeclaration(){

        return this.list_PackageDeclaration;
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setExObjectMacroErrorHead(this);
    }

    @Override
    public String build(){

        String local_expansion = this.expansion;

        if(local_expansion != null){
            return local_expansion;
        }

        StringBuilder sb0 = new StringBuilder();

        MHeader minsert_1 = new MHeader();
                        sb0.append(minsert_1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildPackageDeclaration());
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("class MObjectMacroErrorHead ");
        sb0.append("{");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("  MObjectMacroErrorHead() ");
        sb0.append("{");
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
        sb0.append("    sb.append(\"*** OBJECT MACRO ERROR ***\");");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    sb.append(System.getProperty(\"line.separator\"));");
        sb0.append(LINE_SEPARATOR);
        sb0.append("    return sb.toString();");
        sb0.append(LINE_SEPARATOR);
        sb0.append("  }");
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("}");

        local_expansion = sb0.toString();
        this.expansion = local_expansion;
        return local_expansion;
    }

    @Override
    String build(Context context) {
        return build();
    }
}
