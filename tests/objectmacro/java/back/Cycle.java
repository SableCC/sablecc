package back;

import back.macro.*;

/**
 * Created by lam on 24/01/18.
 */
public class Cycle {

    public static void main(String args[]){

        System.out.print("---------- Cyclic Reference ----------\n");
        MF f = new MF();
        MG g = new MG();
        MH h = new MH();
        f.addPY(h);
        f.addPX(g);
        f.addPX(g);
        h.addPLala(f);

        try{
            System.out.print(f.build());
            System.err.println("It should throw an exception here");
            System.exit(1);
        }catch(ObjectMacroException e){
            System.out.println(e.getMessage());
        }
    }
}
