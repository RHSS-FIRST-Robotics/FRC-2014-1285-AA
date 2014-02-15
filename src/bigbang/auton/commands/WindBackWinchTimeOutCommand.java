/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbang.auton.commands;

import edu.wpi.first.wpilibj.Timer;
import bigbang.subsystems.Catapult;
import bigbang.utilities.Constants; 
import bigbang.auton.AutonCommand;

/**
 *
 * @author Shubham
 */
public class WindBackWinchTimeOutCommand implements AutonCommand {
    Catapult catapult;   
    Timer t = new Timer();
    
    double winchPos;
    double timeOutInSecs;
   
    public WindBackWinchTimeOutCommand(double setpoint, double timeOut){ 
        catapult = Catapult.getInstance();
        
        this.winchPos = setpoint;
            
        
        this.timeOutInSecs = timeOut; 
        Constants.getInstance();
    }
    public void init()
    {
        t.reset();
        t.start();   
    }
    public boolean run(){
       
//        if(!(catapult.getWinchPot() == winchPos))
//            catapult.setWinchPos(winchPos, Constants.getDouble("CatapultPotPosTolerance"));
//        else
//            catapult.setWinchPWM(0);
        catapult.setWinchPos(winchPos);
        return t.get() > timeOutInSecs ;
    }
    public void done()
    {
        catapult.setWinchPWM(0);
    }       

}
