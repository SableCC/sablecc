package entity;

import entity.macro.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(
            String[] args){

        MEntity entity = createEntity("Person");
        File entities_directory = new File("tests/objectmacro/java/entities");
        if(!entities_directory.exists()){
            entities_directory.mkdir();
        }

        File directory = new File("tests/objectmacro/java/entities");
        if(!directory.exists()){
            directory.mkdir();
        }

        File destination = new File("tests/objectmacro/java/entities", "Person.java");
        writeFile(destination, entity.build());
        entity = createEntity("Book");
        destination = new File("tests/objectmacro/java/entities", "Book.java");
        writeFile(destination, entity.build());
    }

    private static void writeFile(
            File destination, String content){

        try {
            FileWriter fw = new FileWriter(destination);
            fw.write(content);
            fw.close();
        }
        catch (Exception e) {
            System.out.print(e.getMessage());
            System.exit(0);
        }
    }

    private static MEntity createEntity(
            String entity_name){

        MPackageDeclaration mPackageDeclaration = new MPackageDeclaration("entities");
        MAttribute[] attributes = new MAttribute[3];
        Macro[] accessors = new Macro[6];

        attributes[0] = createId("id_" + entity_name.toLowerCase(), "AUTO");
        MSetter setter = new MSetter("id_" + entity_name.toLowerCase(), Integer.class.getSimpleName());
        MGetter getter = new MGetter("id_" + entity_name.toLowerCase(), Integer.class.getSimpleName());
        accessors[0] = setter;
        accessors[1] = getter;

        attributes[1] = createAttribute("A", Integer.class.getSimpleName());
        setter = new MSetter("A", Integer.class.getSimpleName());
        getter = new MGetter("A", Integer.class.getSimpleName());
        accessors[2] = setter;
        accessors[3] = getter;

        attributes[2] = createAttribute("B", String.class.getSimpleName());
        setter = new MSetter("B", String.class.getSimpleName());
        getter = new MGetter("B", String.class.getSimpleName());
        accessors[4] = setter;
        accessors[5] = getter;

        return new MEntity(entity_name, new MPackageDeclaration[]{mPackageDeclaration}, attributes, new MRelationship[0], accessors);
    }

    private static MAttribute createId(
            String name,
            String generation_strategy){

        MPrimaryKey mPrimaryKey = new MPrimaryKey();
        MIdIncrementationStrategy mIdIncrementationStrategy = null;
        if(!generation_strategy.equals("")){
            mIdIncrementationStrategy = new MIdIncrementationStrategy(generation_strategy);
        }

        Macro[] id_related;
        if (mIdIncrementationStrategy == null) {
            id_related = new Macro[]{mPrimaryKey};
        }
        else{
            id_related = new Macro[]{mPrimaryKey, mIdIncrementationStrategy};
        }

        return new MAttribute(name, "Integer", id_related, new Macro[0]);
    }

    private static MAttribute createAttribute(
            String name,
            String type){


        return new MAttribute(name, type, new Macro[0], new Macro[]{new MNotNull()});
    }

}
