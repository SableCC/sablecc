package alternative_displays;

import alternative_displays.macro.*;

import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(
            String[] args){

        Macros m = new Macros();

        MClass mClass = m.newClass();
        mClass.addName("maClasse");
        MFor mFor = m.newFor();
        mFor.addClassName("String");
        mFor.addParameterName("strings");

        MEnum mEnum = m.newEnum();
        mEnum.addEnumName("ENUM");

        List<Macro> macros = new LinkedList<>();
        MConstant mConstant = m.newConstant();
        mConstant.addName("A");
        MConstant mConstant2 = m.newConstant();
        mConstant2.addName("B");

        macros.add(mConstant);
        macros.add(mConstant2);

        mEnum.addAllEnumConstants(macros);

        MValue value = m.newValue();
        value.addValue("1");
        mConstant.addValues(value);

        value = m.newValue();
        value.addValue("2");
        mConstant2.addValues(value);

        mClass.addEnums(mEnum);
        System.out.println(mClass.build());
    }
}
