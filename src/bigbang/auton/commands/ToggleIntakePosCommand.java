/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbang.auton.commands;

import edu.wpi.first.wpilibj.Timer;
import bigbang.auton.AutonCommand;
import bigbang.subsystems.Intake;

/**
 *
 * @author Shubham
 */
public class ToggleIntakePosCommand implements AutonCommand{
    Intake intake;   
    
    public ToggleIntakePosCommand (){
        intake = intake.getInstance();
    }
        
    public void init() {
        intake.toggleIntakePosAuton(false);
    }

    public boolean run() {
        intake.toggleIntakePosAuton(true);
        return true;
    }

    public void done() {

    } 
}
