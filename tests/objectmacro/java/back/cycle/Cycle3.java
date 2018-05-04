package back.cycle;

import back.cycle.macro.*;

public class Cycle3 {

    public static void main(String[] args){

        MA ma = new MA();
        MB mb = new MB();
        MC mc = new MC();
        MC mc1 = new MC();
        MA ma1 = new MA();
        MB mb1 = new MB();
        MA ma2 = new MA();

        ma.addX(mb);
        mb.addY(mc);
        mc.addY(ma1);
        mb.addY(mc);
        mb.addY(mc1);
        mc1.addY(ma2);
        mc1.addZ(mb1);
        mb1.addY(mc);

        try{
            ma1.addX(mb1);
            System.err.println("It should throw an exception here");
            System.exit(1);
        }
        catch(ObjectMacroException e){
            e.printStackTrace();
        }
    }
}
