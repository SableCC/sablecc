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

        MFinalOutput mFinalOutput = new MFinalOutput();

        MAuthor mAuthor = new MAuthor("Johann Sebastian Bach");
        mAuthor.addDetails(new MDetail("BIRTH", "1685"));
        mAuthor.addDetails(new MDetail("DEATH", "1750"));

        mFinalOutput.addAuthors(mAuthor);

        mAuthor = new MAuthor("George Frideric Handel");
        mAuthor.addDetails(new MDetail("BIRTH", "1685"));
        mAuthor.addDetails(new MDetail("DEATH", "1759"));

        mFinalOutput.addAuthors(mAuthor);

        mAuthor = new MAuthor("Wolfgang Amadeus Mozart");
        mAuthor.addDetails(new MDetail("BIRTH", "1756"));
        mAuthor.addDetails(new MDetail("DEATH", "1791"));

        mFinalOutput.addAuthors(mAuthor);

        String finalOuput = mFinalOutput.build();

        System.out.println(finalOuput);
    }

    private static void indent(){

        MIndentA indentA = new MIndentA("B ");
        System.out.println(indentA.build());
    }
}
