/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbang.subsystems;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

import bigbang.main.ElectricalConstants;
import bigbang.utilities.Constants;
import bigbang.utilities.ToggleBoolean;


public class Catapult {

    static Catapult inst = null;
    
    Talon rightWinchMotor;
    Talon leftWinchMotor;
    
    DoubleSolenoid winchReleasePiston;
    DoubleSolenoid trussShotPiston;
    
    ToggleBoolean trussShotToggle;
    ToggleBoolean winchStateToggle;
    ToggleBoolean winchShiftToggle;
    
    AnalogChannel winchPot;
    
    Timer winchShiftTimer;

    final boolean ENGAGED = true;
    final boolean DISENGAGED = false;
    boolean winchPistonState = ENGAGED; //piston assumed to be engaged in gearbox in default state when robot is turned on

    final boolean EXTENDED = true;
    final boolean RETRACTED = false;
    boolean trussPistonState = RETRACTED; //piston assumed to be retracted in default state when robot is turned on
            
    double winchSetpoint;
    
    final int ENGAGE = 0;
    final int FULLY_ENGAGED = 1;   
    int setEngaged = ENGAGE;
    
    final int WIND_WINCH = 2;
    int setWinchPosSeq = WIND_WINCH;
    
    public Catapult()
    {
        
        winchPot= new AnalogChannel(ElectricalConstants.WINCH_POT);
    
        rightWinchMotor = new Talon(ElectricalConstants.RIGHT_WINCH_PWM);
        leftWinchMotor = new Talon(ElectricalConstants.LEFT_WINCH_PWM);
        
        trussShotPiston = new DoubleSolenoid (ElectricalConstants.TRUSS_ENGAGE, ElectricalConstants.TRUSS_DISENGAGE);
        winchReleasePiston = new DoubleSolenoid ( ElectricalConstants.WINCH_ENGAGE, ElectricalConstants.WINCH_DISENGAGE);
        
        trussShotToggle = new ToggleBoolean();
        winchStateToggle = new ToggleBoolean();
        winchShiftToggle = new ToggleBoolean();
        
        winchShiftTimer = new Timer();
        winchShiftTimer.start();

        Constants.getInstance();
    }

    public static Catapult getInstance() {
        if(inst == null) {
            inst = new Catapult();
        }
        return inst;
    }
    
    public void setWinchPWM(double pwm) {
        rightWinchMotor.set(pwm);
        leftWinchMotor.set(pwm);
    }
    
    public double getWinchPot() {
        return winchPot.getAverageValue();
    }
    
    public void setWinchPos(double setpoint) { 
        
        if (winchPistonState == DISENGAGED)
            setWinchPosSeq = ENGAGE;
        else
            setWinchPosSeq = WIND_WINCH;
        
        switch(setWinchPosSeq){
            case ENGAGE:
                engageWinch(true);
                if (winchPistonState == ENGAGED)
                    setWinchPosSeq = WIND_WINCH;
                break;    
            case WIND_WINCH:
                if (setpoint == getWinchPot())
                    setWinchPWM(0);
                else if(setpoint - getWinchPot() > Constants.getInteger("bWinchPosTolerance")) 
                    setWinchPWM(Constants.getDouble("bWinchWindBackSpeed"));
                else if(setpoint - getWinchPot() < Constants.getInteger("bWinchPosTolerance")) 
                    setWinchPWM(-Constants.getDouble("bWinchWindBackSpeed"));
                else 
                    setWinchPWM(0);  
                break;
        }
    }
    
    public void setWinch(double manualAdjustment, 
            boolean presetOne, boolean presetTwo){
            
            if (Math.abs(manualAdjustment) > 0.1) {
                if (winchPistonState == DISENGAGED)
                    engageWinch(true);
                else
                    setWinchPWM(manualAdjustment);
            }
            else {
                if (presetOne)
                    winchSetpoint = Constants.getDouble("bWinchPosOne");
                else if (presetTwo)
                    winchSetpoint = Constants.getDouble("bWinchPosTwo");
                else
                    winchSetpoint = getWinchPot();
                
                setWinchPos(winchSetpoint);
            }  
    }
    
    public void toggleWinchPiston(boolean winchPistonToggleButton) {        
        winchStateToggle.set(winchPistonToggleButton);
        
        if(winchStateToggle.get())
            winchPistonState = !winchPistonState;
        
        if(winchPistonState == DISENGAGED) 
            winchReleasePiston.set(DoubleSolenoid.Value.kForward);
        else 
            winchReleasePiston.set(DoubleSolenoid.Value.kReverse);
    }
    
    public void toggleTrussPistonPos(boolean trussPistonToggleButton) {     
        trussShotToggle.set(trussPistonToggleButton);
        
        if(trussShotToggle.get())
            trussPistonState = !trussPistonState;
        
        if(trussPistonState) 
            trussShotPiston.set(DoubleSolenoid.Value.kForward);
        else 
            trussShotPiston.set(DoubleSolenoid.Value.kReverse);
    }
    
    public void holdTrussPistonPos(boolean trussPistonButton) {
        if(trussPistonButton) {
            trussShotPiston.set(DoubleSolenoid.Value.kForward);
            trussPistonState = EXTENDED;
        }
        else {
            trussShotPiston.set(DoubleSolenoid.Value.kReverse);
            trussPistonState = RETRACTED;
        }
    }

    public void engageWinch(boolean winchShift) {
        winchShiftToggle.set(winchShift);
        
        if(winchPistonState == DISENGAGED && winchShiftToggle.get()){
            setEngaged = ENGAGE;
            winchShiftTimer.reset();
        }
        else
            setEngaged = FULLY_ENGAGED;
        
        switch (setEngaged){
            case ENGAGE:
                setWinchPWM(Constants.getDouble("bWinchShiftSpeed"));
                winchReleasePiston.set(DoubleSolenoid.Value.kForward);
                
                if (winchShiftTimer.get() > Constants.getDouble("bWinchShiftTime"))
                    setEngaged = FULLY_ENGAGED;
                break;
            case FULLY_ENGAGED:
                setWinchPWM(0);
                winchShiftToggle.set(false);
                winchPistonState = ENGAGED;
                break;
        }
        
         

        winchPistonState = true; 
    }

    public void disengageWinch(boolean disengage) {
        if(winchPistonState && disengage){
            winchReleasePiston.set(DoubleSolenoid.Value.kForward);
            winchPistonState = DISENGAGED;  
        }
    }

}
