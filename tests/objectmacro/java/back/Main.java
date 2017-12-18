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

public class Main {

    public static void main(
            String[] args){

        Macro[] macros = new Macro[3];
        macros[0] = new MB("First argument in MB0");
        macros[1] = new MB("First argument in MB1");
        macros[2] = new MB("First argument in MB2");

        Macro[] macros1 = new Macro[2];
        macros1[0] = new MC();
        macros1[1] = new MC();

        try{
            MA ma = new MA("First argument of MA", macros, macros1);
            macros[0] = new  MC();
            System.out.println(ma.build());
        }
        catch(ObjectMacroException e){
            System.err.println(e.getMessage());
            System.err.println("It should not throw an exception");
            System.exit(1);
        }
    }
}
