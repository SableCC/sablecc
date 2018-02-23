package org.sablecc.objectmacro.codegeneration.java.structure;

import org.sablecc.objectmacro.codegeneration.java.macro.MMacro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lam on 17/11/17.
 */
public class Macro {

    private MMacro macro;

    private final List<String> parameters;

    private final List<String> internals;

    private final String name;

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListPackage = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getPackages() {return this.list_ListPackage; }
    public void setPackage(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listPackage) {this.list_ListPackage = listPackage;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListField = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getField() {return this.list_ListField; }
    public void setField(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listField) {this.list_ListField = listField;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListContextField = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getContextField() {return this.list_ListContextField; }
    public void setContextField(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listContextField) {this.list_ListContextField = listContextField;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListConstructor = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getConstructor() {return this.list_ListConstructor; }
    public void setConstructor(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listConstructor) {this.list_ListConstructor = listConstructor;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListSetter = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getSetter() {return this.list_ListSetter; }
    public void setSetter(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listSetter) {this.list_ListSetter = listSetter;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListBuilder = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getBuilder() {return this.list_ListBuilder; }
    public void setBuilder(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listBuilder) {this.list_ListBuilder = listBuilder;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListRef = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getRef() {return this.list_ListRef; }
    public void setRef(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listRef) {this.list_ListRef = listRef;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListRedefinedApplyInitializer = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getRedefinedApplyInitializer() {return this.list_ListRedefinedApplyInitializer; }
    public void setRedefinedApplyInitializer(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listRedefinedApplyInitializer) {this.list_ListRedefinedApplyInitializer = listRedefinedApplyInitializer;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListMacroBuilder = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getMacroBuilder() {return this.list_ListMacroBuilder; }
    public void setMacroBuilder(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listMacroBuilder) {this.list_ListMacroBuilder = listMacroBuilder;}

    private ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> list_ListEmptyBuilderWithContext = new ArrayList<>();
    public ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> getEmptyBuilderWithContext() {return this.list_ListEmptyBuilderWithContext; }
    public void setEmptyBuilderWithContext(ArrayList<org.sablecc.objectmacro.codegeneration.java.macro.Macro> listEmptyBuilderWithContext) {this.list_ListEmptyBuilderWithContext = listEmptyBuilderWithContext;}

    public Macro(
            String name,
            List<String> parameters,
            List<String> internals){
        this.name = name;
        this.parameters = parameters;
        this.internals = internals;
    }

    public List<String> getInternals() {

        return internals;
    }

    public List<String> getParameters() {

        return parameters;
    }

    private void buildMacro() {
        this.macro = new MMacro(name,
                list_ListPackage.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListPackage.size()]),
                list_ListField.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListField.size()]),
                list_ListContextField.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListContextField.size()]),
                list_ListConstructor.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListConstructor.size()]),
                list_ListSetter.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListSetter.size()]),
                list_ListBuilder.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListBuilder.size()]),
                list_ListRef.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListRef.size()]),
                list_ListRedefinedApplyInitializer.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListRedefinedApplyInitializer.size()]),
                list_ListMacroBuilder.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListMacroBuilder.size()]),
                list_ListEmptyBuilderWithContext.toArray(new org.sablecc.objectmacro.codegeneration.java.macro.Macro[list_ListEmptyBuilderWithContext.size()]) );
    }

    public MMacro getMacro() {
        buildMacro();
        return macro;
    }
}
