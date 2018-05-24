package alternative_displays;

import alternative_displays.macro.*;

import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(
            String[] args){

        Macros m = new Macros();

        MClass mClass = m.newClass("MaClasse");
        MFor mFor = m.newFor("String", "test");

        mClass.addTest(mFor);
        MEnum mEnum = m.newEnum("Enum");

        List<Macro> macros = new LinkedList<>();
        MConstant mConstant = m.newConstant("A");
        MConstant mConstant2 = m.newConstant("B");

        macros.add(mConstant);
        macros.add(mConstant2);

        mEnum.addAllEnumConstants(macros);

        mConstant.addValues(m.newValue("1"));
        mConstant2.addValues(m.newValue("2"));

        mClass.addEnums(mEnum);
        System.out.println(mClass.build());
    }
}
