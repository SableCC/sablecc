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

package back.cycle;

import back.cycle.macro.*;

public class Cycle {

    public static void main(String[] args){

        System.out.print("---------- Cyclic Reference ----------\n");

        Macros m = new Macros();

        MA ma = m.newA();
        MB mb = m.newB();
        MC mc = m.newC();

        ma.addX(mb);
        mb.addY(mc);

        try{
            mc.addY(ma);
            System.err.println("It should throw an exception here");
            System.exit(1);
        }
        catch(ObjectMacroException e){
            e.printStackTrace();
        }
    }
}
