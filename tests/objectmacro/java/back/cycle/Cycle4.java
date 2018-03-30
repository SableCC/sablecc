package back.cycle;

import back.cycle.macro.*;

public class Cycle4 {

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
        ma.addX(mb1);
        MC mc2 = new MC();
        mc2.addY(ma);

        try{
            mb1.addY(mc2);
            System.err.println("It should throw an exception here");
            System.exit(1);
        }
        catch(ObjectMacroException e){
            e.printStackTrace();
        }
    }

}
