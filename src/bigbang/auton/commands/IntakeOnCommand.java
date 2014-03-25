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
public class IntakeOnCommand implements AutonCommand{
    Intake intake;   
    double pwmVal;
    double timeOutInSecs;
    
    public IntakeOnCommand (double pwm){
        this.pwmVal = pwm;
        intake = intake.getInstance();
    }
        
    public void init() {

    }

    public boolean run() {

       // intake.setSpeed(pwmVal);
        intake.intakeBall(pwmVal,1);
        //intake.setRollerPWM(pwmVal);
        //intake.setPG71(pwmVal);

        return true;
    }

    public void done() {
        intake.intakeBall(0, 1);
    }  
}
