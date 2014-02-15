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
public class IntakeTimeOutCommand implements AutonCommand{
    Intake intake;   
    Timer t = new Timer();
    double pwmVal;
    double timeOutInSecs;
    
    public IntakeTimeOutCommand (double pwm, double timeOut){
        this.pwmVal = pwm;
        this.timeOutInSecs = timeOut;
        intake = intake.getInstance();
    }
        
    public void init() {
        t.reset();
        t.start();
    }

    public boolean run() {

       // intake.setSpeed(pwmVal);
        intake.intakeBall(pwmVal,1);
        //intake.setRollerPWM(pwmVal);
        //intake.setPG71(pwmVal);

        return t.get() > timeOutInSecs;
    }

    public void done() {
        intake.intakeBall(0, 1);
    }  
}
