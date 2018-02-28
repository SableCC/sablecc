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
package back;

import back.macro.*;

import java.util.ArrayList;
import java.util.List;

public class IncorrectType {

    public static void main(
            String[] args){

        System.out.println("---------- Incorrect type ----------");
        List<MB> macros = new ArrayList<>();
        macros.add(new MB("First argument in MB0"));
        macros.add(new MB("First argument in MB1"));
        macros.add(new MB("First argument in MB2"));

        try{
            MA ma = new MA("First argument of MA");
            for(MB macro : macros){
                ma.addY(macro);
            }

            ma.build();
            System.err.println("It should throw an exception here");
            System.exit(1);
        }
        catch(ObjectMacroException e){
            System.out.println(e.getMessage());
        }
    }
}
