/* This file is part of SableCC ( http://sablecc.org ).
 *
 * See the NOTICE file distributed with this work for copyright information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package entity;

import entity.macro.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

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

        MEntity mEntity = new MEntity(entity_name);
        mEntity.addPackage(new MPackageDeclaration("entities"));
        MSetter setter = new MSetter("id_" + entity_name.toLowerCase(), Integer.class.getSimpleName());
        MGetter getter = new MGetter("id_" + entity_name.toLowerCase(), Integer.class.getSimpleName());

        mEntity.addAttributes(createId("id_" + entity_name.toLowerCase(), "AUTO"));
        mEntity.addAccessors(setter);
        mEntity.addAccessors(getter);

        mEntity.addAttributes(createAttribute("A", Integer.class.getSimpleName(), false));
        setter = new MSetter("A", Integer.class.getSimpleName());
        getter = new MGetter("A", Integer.class.getSimpleName());
        mEntity.addAccessors(setter);
        mEntity.addAccessors(getter);

        mEntity.addAttributes(createAttribute("B", String.class.getSimpleName(), true));
        setter = new MSetter("B", String.class.getSimpleName());
        getter = new MGetter("B", String.class.getSimpleName());
        mEntity.addAccessors(setter);
        mEntity.addAccessors(getter);

        return mEntity;
    }

    private static MAttribute createId(
            String name,
            String generation_strategy){

        MAttribute mAttribute = new MAttribute(name, "Integer");
        MPrimaryKey mPrimaryKey = new MPrimaryKey();
        MIdIncrementationStrategy mIdIncrementationStrategy = null;
        if(!generation_strategy.equals("")){
            mIdIncrementationStrategy = new MIdIncrementationStrategy(generation_strategy);
        }

        mAttribute.addNotNull(new MNotNull());

        if (mIdIncrementationStrategy == null) {
            mAttribute.addId(mPrimaryKey);
        }
        else{
            mAttribute.addId(mPrimaryKey);
            mAttribute.addId(mIdIncrementationStrategy);
        }

        return mAttribute;
    }

    private static MAttribute createAttribute(
            String name,
            String type,
            boolean notNull){

        MAttribute mAttribute = new MAttribute(name, type);
        if(notNull) {
            mAttribute.addNotNull(new MNotNull());
        }

        return mAttribute;
    }

}
