/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbang.auton.commands;

import bigbang.auton.AutonCommand;
import bigbang.subsystems.Catapult;

/**
 *
 * @author Controls
 */
public class ShootBallTimeOutCommand implements AutonCommand{
    
    Catapult catapult;   
   
    public ShootBallTimeOutCommand(){
        catapult = Catapult.getInstance();
    }
    public void init() {

    }
    public boolean run() {
       catapult.disengageWinch(true);
       return true;
    }
    public void done() {
        catapult.setWinchPWM(0);
    }       
}