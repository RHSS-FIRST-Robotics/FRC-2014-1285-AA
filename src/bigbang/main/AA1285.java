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
    Joystick drivePad;
    
    public void robotInit() 
    {
        dsLCD = DriverStationLCD.getInstance();
        driveTrain = DriveTrain.getInstance();
        drivePad = new Joystick(GamepadConstants.DRIVE_USB_PORT);
    }


    public void autonomousPeriodic() {

    }


    public void teleopPeriodic() 
    {
        double leftAnalogY = drivePad.getRawAxis(GamepadConstants.LEFT_ANALOG_Y); //Assign variable to Analog Sticks
        double rightAnalogY = drivePad.getRawAxis(GamepadConstants.RIGHT_ANALOG_Y);
        
        if(drivePad.getRawButton(GamepadConstants.RIGHT_BUMPER)) // Half speed function
        {
            driveTrain.tankDrive(-leftAnalogY/2, -rightAnalogY/2, 3); 
        }
        else
        {
            driveTrain.tankDrive(-leftAnalogY, rightAnalogY, 3); // Normal speed
        }
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() 
    {

    }
    
}
