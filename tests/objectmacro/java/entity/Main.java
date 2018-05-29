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

    private static Macros m = new Macros();

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

        MEntity mEntity = m.newEntity();
        mEntity.addName(entity_name);
        MPackageDeclaration mPackageDeclaration = m.newPackageDeclaration();
        mPackageDeclaration.addPackageName("entities");
        mEntity.addPackage(mPackageDeclaration);

        MSetter setter = m.newSetter();
        setter.addName("id_" + entity_name.toLowerCase());
        setter.addName(Integer.class.getSimpleName());

        MGetter getter = m.newGetter();
        getter.addName("id_" + entity_name.toLowerCase());
        getter.addName(Integer.class.getSimpleName());

        mEntity.addAttributes(createId("id_" + entity_name.toLowerCase(), "AUTO"));
        mEntity.addAccessors(setter);
        mEntity.addAccessors(getter);

        mEntity.addAttributes(createAttribute("A", Integer.class.getSimpleName(), false));
        setter = m.newSetter();
        setter.addName("A" + entity_name.toLowerCase());
        setter.addName(Integer.class.getSimpleName());

        getter = m.newGetter();
        getter.addName("A" + entity_name.toLowerCase());
        getter.addName(Integer.class.getSimpleName());

        mEntity.addAccessors(setter);
        mEntity.addAccessors(getter);

        mEntity.addAttributes(createAttribute("B", String.class.getSimpleName(), true));
        setter = m.newSetter();
        setter.addName("B" + entity_name.toLowerCase());
        setter.addName(Integer.class.getSimpleName());

        getter = m.newGetter();
        getter.addName("B" + entity_name.toLowerCase());
        getter.addName(Integer.class.getSimpleName());

        mEntity.addAccessors(setter);
        mEntity.addAccessors(getter);

        return mEntity;
    }

    private static MAttribute createId(
            String name,
            String generation_strategy){

        MAttribute mAttribute = m.newAttribute();
        mAttribute.addName(name);
        mAttribute.addType(Integer.class.getSimpleName());
        MPrimaryKey mPrimaryKey = m.newPrimaryKey();
        MIdIncrementationStrategy mIdIncrementationStrategy = null;
        if(!generation_strategy.equals("")){
            mIdIncrementationStrategy = m.newIdIncrementationStrategy();
            mIdIncrementationStrategy.addStrategy(generation_strategy);
        }

        mAttribute.addNotNull(m.newNotNull());

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

        MAttribute mAttribute = m.newAttribute();
        mAttribute.addName(name);
        mAttribute.addType(type);
        if(notNull) {
            mAttribute.addNotNull(m.newNotNull());
        }

        return mAttribute;
    }

}
