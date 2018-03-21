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
