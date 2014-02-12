/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package bigbang.main;


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
    Joystick drivePad;
    Joystick toolPad;
    Compressor compressor;
    
    public void robotInit() 
    {
        dsLCD = DriverStationLCD.getInstance();
        driveTrain = DriveTrain.getInstance();
        intake = Intake.getInstance();
        drivePad = new Joystick(GamepadConstants.DRIVE_USB_PORT);
        toolPad = new Joystick(GamepadConstants.TOOL_USB_PORT);
        compressor = new Compressor(ElectricalConstants.COMPRESSOR_PRESSURE_SENSOR,ElectricalConstants.COMPRESSOR_RELAY);
        
        Constants.getInstance();
    }

    public void disabledInit(){
        Constants.load();
        compressor.stop();
    }
    
    public void disabledPeriodic(){
        
    }
    
    public void autonomousInit(){
        compressor.stop();
    }
    
    public void autonomousPeriodic() {
        
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
        
        driveTrain.tankDrive(-leftAnalogY, rightAnalogY, Constants.getInteger("tDriveJoyScaler")); // Normal speed
        intake.intakeBall(intakeJoy, Constants.getInteger("tIntakeJoyScaler"));
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() 
    {

    }
    
}
