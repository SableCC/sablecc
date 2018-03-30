package back.cycle;

import back.cycle.macro.*;

public class Cycle {

    public static void main(String[] args){

        System.out.print("---------- Cyclic Reference ----------\n");

        MA ma = new MA();
        MB mb = new MB();
        MC mc = new MC();

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
