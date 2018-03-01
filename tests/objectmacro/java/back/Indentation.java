package back;

import back.macro.*;

public class Indentation {

    public static void main(
            String[] args){

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

        System.out.println(mFinalOutput.build());
    }
}
