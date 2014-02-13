/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package bigbang.main;


import bigbang.subsystems.Catapult;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import bigbang.subsystems.DriveTrain;
import bigbang.subsystems.Intake;
import bigbang.utilities.Constants;
import edu.wpi.first.wpilibj.Compressor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class AA1285 extends IterativeRobot {

    DriverStationLCD dsLCD;
    DriveTrain driveTrain;
    Intake intake;
    Catapult catapult;
    Joystick drivePad;
    Joystick toolPad;
    Compressor compressor;
    
    double lcdUpdateCycle = 0;
    
    public void robotInit() 
    {
        dsLCD = DriverStationLCD.getInstance();
        driveTrain = DriveTrain.getInstance();
        intake = Intake.getInstance();
        catapult = Catapult.getInstance();
        drivePad = new Joystick(GamepadConstants.DRIVE_USB_PORT);
        toolPad = new Joystick(GamepadConstants.TOOL_USB_PORT);
        compressor = new Compressor(ElectricalConstants.COMPRESSOR_PRESSURE_SENSOR,ElectricalConstants.COMPRESSOR_RELAY);
        
        dsLCD = DriverStationLCD.getInstance();
        Constants.getInstance();
    }

    public void disabledInit(){
        log("Entered disabledInit...reloading constants...");
        Constants.load();
        compressor.stop();
    }
    
    public void disabledPeriodic(){
        
        updateDSLCD();
        
        if(toolPad.getRawButton(GamepadConstants.A_BUTTON)){
            log("About to recalibrate gyro");
            driveTrain.recalibrateGyro();
            driveTrain.resetGyro();
            log("Finished recalibrating gyro");
        }
        
        if(toolPad.getRawButton(GamepadConstants.B_BUTTON)){
            driveTrain.resetEncoders();
            log("Reset Encoders");
        }
        
        if(toolPad.getRawButton(GamepadConstants.Y_BUTTON)){
            driveTrain.resetGyro();
            log("Reset Gyro");
        }
    }
    
    public void autonomousInit(){
        compressor.stop();
    }
    
    public void autonomousPeriodic() {
        updateDSLCD();
    }

    public void teleopInit(){
        compressor.start();
    }
    
    public void teleopPeriodic() 
    {
        compressor.start();
        double leftAnalogY = drivePad.getRawAxis(GamepadConstants.LEFT_ANALOG_Y); //Assign variable to Analog Sticks
        double rightAnalogY = drivePad.getRawAxis(GamepadConstants.RIGHT_ANALOG_Y);
        double intakeJoy = toolPad.getRawAxis(GamepadConstants.LEFT_ANALOG_Y);
        double winchJoy = toolPad.getRawAxis(GamepadConstants.RIGHT_ANALOG_Y);

        if(drivePad.getRawButton(GamepadConstants.RIGHT_BUMPER)) //rightBumper is half speed button for driver!
            driveTrain.tankDrive(-leftAnalogY/2, rightAnalogY/2, 1);
        else
            driveTrain.tankDrive(-leftAnalogY, rightAnalogY, Constants.getInteger("tDriveJoyScaler"));
        
        intake.intakeBall(intakeJoy, Constants.getInteger("tIntakeJoyScaler"));
        
        intake.setIntakePosTeleop(toolPad.getRawButton(GamepadConstants.LEFT_BUMPER));
        
        //Truss Piston
        catapult.holdTrussPistonPos(toolPad.getRawButton(GamepadConstants.RIGHT_TRIGGER));
        
        //Winch code
        catapult.windWinch(winchJoy, toolPad.getRawButton(GamepadConstants.B_BUTTON), //preset one
                                     toolPad.getRawButton(GamepadConstants.A_BUTTON)); //preset two
        
        catapult.disengageWinch(toolPad.getRawButton(GamepadConstants.RIGHT_BUMPER));
        
        
        updateDSLCD();
        
    }
    
    private void updateDSLCD() {
        
        dsLCD.println(DriverStationLCD.Line.kUser1, 1, "LEnc: "
        + (Math.floor(driveTrain.getLeftEncoderDist()*10) / 10.0) + " REnc: " 
        + (Math.floor(driveTrain.getRightEncoderDist()*10) / 10.0));
        
        dsLCD.println(DriverStationLCD.Line.kUser2, 1, "Gyro: "
        + Math.floor(driveTrain.getGyroAngle() * 100) / 100);
        
        dsLCD.println(DriverStationLCD.Line.kUser3, 1, "?: " + (catapult.winchOnTarget() ? 1 : 0) 
                                                      + " Catapult Pot: "+ catapult.getWinchPot());
        
//        dsLCD.println(DriverStationLCD.Line.kUser4, 1, "Limit Switch: "
//        + intake.ballDetected());
        
        if ((lcdUpdateCycle % 50) == 0) {
            dsLCD.updateLCD();
        } 
        else {
            lcdUpdateCycle++;
        } 
    }
    
    private void log(Object aObject){
        System.out.println(String.valueOf(aObject));
    }
    
}
