/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bigbang.auton.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import bigbang.pid.PIDController;
import bigbang.subsystems.DriveTrain;
import bigbang.utilities.Constants;
import bigbang.utilities.MathLogic;
import bigbang.auton.AutonCommand;


/**
 *
 * @author Sagar
 */
public class TurnDegreesTimeOutCommand implements AutonCommand {
    
    double angleGoal = 0;
    double maxPWM = 0;
    double power = 0;
    double timeout = 0.0;
    double lastError = 0;
    final double speedDeadband = .1;
    double angleDeadband = 0.5;
    double settleTime = .1;
    double maxRatePWM = 1;
    boolean almostDone = false;
    double leftStiction = 0;
    double rightStiction = 0;
    double PIDOut = 0;
    
    DriveTrain driveTrain;
    PIDController gyroPID;

    Timer timeOutTimer = new Timer();
    Timer timeSinceAlmost = new Timer();
    
    public TurnDegreesTimeOutCommand(double angle, 
                                     double timeOutInSecs) {
        Constants.getInstance();
        this.angleGoal = angle;
        this.timeout = timeOutInSecs;
        
        if (Math.abs (angleGoal) > 150) {
        gyroPID = new PIDController(Constants.getDouble("turnP180"),
                                    Constants.getDouble("turnI180"),
                                    Constants.getDouble("turnD180")); 
        }
        
        else if (Math.abs (angleGoal) < 150) {
        gyroPID = new PIDController(Constants.getDouble("turnPLessThan180"),
                        Constants.getDouble("turnILessThan180"),
                        Constants.getDouble("turnDLessThan180")); 
        }
        
        driveTrain = DriveTrain.getInstance();
        
    }
    
    public void init() {
        driveTrain.resetEncoders();
        driveTrain.resetGyro();
        
        timeOutTimer.reset();
        timeOutTimer.start();
       
        gyroPID.resetIntegral();
        gyroPID.resetDerivative();
    }
    
    public boolean run() {
        double error = angleGoal - driveTrain.getGyroAngle();
        double speed = maxRatePWM * gyroPID.calcPID(angleGoal, driveTrain.getGyroAngle());
        
        if(Math.abs(error - lastError) < speedDeadband && Math.abs(error) < angleDeadband) {
            
            if(!almostDone) {
                timeSinceAlmost.start();
            }
            almostDone = true;
        }
        
        else {
            almostDone = false;
            timeSinceAlmost.stop();
            timeSinceAlmost.reset();
        }
        
        lastError = error;
        
        driveTrain.setLeftPWM(speed);
        driveTrain.setRightPWM(speed);
        
        return timeSinceAlmost.get() > settleTime || timeOutTimer.get() > timeout;
    }
    
    public void done() {
        driveTrain.setLeftPWM(0);
        driveTrain.setRightPWM(0);
        
        timeSinceAlmost.stop();
        timeSinceAlmost.reset();
        
        gyroPID.resetIntegral();
        gyroPID.resetDerivative();
    }
    
}
