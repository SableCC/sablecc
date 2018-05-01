package back.cycle;

import back.cycle.macro.*;

public class Cycle2 {

    public static void main(String[] args){

        MA ma = new MA();
        MB mb = new MB();
        MC mc = new MC();
        MC mc1 = new MC();
        MA ma1 = new MA();
        MA ma2 = new MA();

        ma.addX(mb);
        mb.addY(mc);
        mc.addY(ma1);
        mb.addY(mc);
        mb.addY(mc1);
        mc1.addY(ma2);

        try{
            ma2.addX(mb);
            System.err.println("It should throw an exception here");
            System.exit(1);
        }
        catch(ObjectMacroException e){
            System.out.println(e.getMessage());
        }
    }
}
