/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbang.auton.commands;
import bigbang.auton.AutonCommand;
import edu.wpi.first.wpilibj.Timer;
import bigbang.subsystems.Catapult;

/**
 *
 * @author Controls
 */
public class EngageWinchCommand implements AutonCommand{
   
    Catapult catapult;   
    Timer t = new Timer();
    double timeOutInSecs;
   
    public EngageWinchCommand(double timeOut){
 
        this.timeOutInSecs = timeOut;
        catapult = Catapult.getInstance();
        
    }
    public void init() {  
        t.reset();
        t.start();
        
    }
    public boolean run() {

       catapult.engageWinch(true);

        return t.get() > timeOutInSecs;
    }
    public void done() {
        //catapult.setWinchPWM(0);
    }     
}
