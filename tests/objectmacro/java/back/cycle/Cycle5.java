package back.cycle;

import back.cycle.macro.MF;
import back.cycle.macro.MH;
import back.cycle.macro.ObjectMacroException;

public class Cycle5 {

    public static void main(String[] args){

        MF f = new MF();
        MH h = new MH();
        MH h2 = new MH();
        MF f2 = new MF();

        f.addY(h);
        h.addLala(f2);
        f2.addY(h2);

        try{
            h2.addLala(f);
            System.err.println("It should throw an exception here");
            System.exit(1);
        }
        catch(ObjectMacroException e){
            e.printStackTrace();
        }
    }
}
