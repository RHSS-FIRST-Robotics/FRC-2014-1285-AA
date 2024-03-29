/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbang.auton.commands;

import edu.wpi.first.wpilibj.Timer;
import bigbang.auton.AutonCommand;
import bigbang.subsystems.Catapult;
import bigbang.utilities.Constants;

/**
 *
 * @author Controls
 */
public class ShootBallTimeOutCommand implements AutonCommand{
    
    Catapult catapult;   
    Timer t = new Timer();
    double setpoint;
    double timeOutInSecs;
   
    public ShootBallTimeOutCommand(double timeout){
        this.timeOutInSecs = timeout;
        catapult = Catapult.getInstance();
 
    }
    public void init() {
        t.reset();
        t.start();   
        catapult.setWinchPWM(0);
    }
    public boolean run() {

      
       catapult.disengageWinch(true);

        return t.get() > this.timeOutInSecs;
    }
    public void done() {
        //catapult.setWinchPWM(0);
    }       
}
