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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Indentation {

    public static void main(
            String[] args){

        composers();
        indent();
    }

    private static void composers(){

        Macros macros = new Macros();

        MFinalOutput mFinalOutput = macros.newFinalOutput();

        MAuthor mAuthor = macros.newAuthor();
        MComposerDetail detail = macros.newComposerDetail();

        mAuthor.addName("Johann Sebastian Bach");
        detail.addName("Johann Sebastian Bach");
        detail.addBirth("1685");
        detail.addDeath("1750");
        mFinalOutput.addAuthors(mAuthor);
        mFinalOutput.addDetails(detail);

        mAuthor = macros.newAuthor();
        detail = macros.newComposerDetail();

        mAuthor.addName("George Frideric Handel");
        detail.addName("George Frideric Handel");
        detail.addBirth("1685");
        detail.addDeath("1759");
        mFinalOutput.addAuthors(mAuthor);
        mFinalOutput.addDetails(detail);

        mAuthor = macros.newAuthor();
        detail = macros.newComposerDetail();

        mAuthor.addName("Wolfgang Amadeus Mozart");
        detail.addName("Wolfgang Amadeus Mozart");
        detail.addBirth("1756");
        detail.addDeath("1791");
        mFinalOutput.addAuthors(mAuthor);
        mFinalOutput.addDetails(detail);

        String finalOuput = mFinalOutput.build();

        System.out.println(finalOuput);
    }

    private static void indent(){

        Macros macros = new Macros();

        MIndentA indentA = macros.newIndentA();
        indentA.addB("B ");
        System.out.println(indentA.build());
    }
}
