/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package bigbang.main;


import bigbang.auton.AutonController;
import bigbang.subsystems.Catapult;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import bigbang.subsystems.DriveTrain;
import bigbang.subsystems.Intake;
import bigbang.utilities.Constants;
import bigbang.utilities.SendableChooser;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    SendableChooser autonSwitcher;
    AutonController ac = new AutonController();
    AutonSequences autonSeq = new AutonSequences();
    
    
    public void robotInit() 
    {
        compressor = new Compressor(ElectricalConstants.COMPRESSOR_PRESSURE_SENSOR,
                                    ElectricalConstants.COMPRESSOR_RELAY);
      
        dsLCD = DriverStationLCD.getInstance();
        driveTrain = DriveTrain.getInstance();
        intake = Intake.getInstance();
        catapult = Catapult.getInstance();
        drivePad = new Joystick(GamepadConstants.DRIVE_USB_PORT);
        toolPad = new Joystick(GamepadConstants.TOOL_USB_PORT);
        autonSwitcher = new SendableChooser();
        
        Constants.getInstance();
        //Constants.load();
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
        
        if (toolPad.getRawButton(GamepadConstants.X_BUTTON)){
            Constants.load();
            log("Reloading constants in disabled periodic");
        }
        
                //autonSwitcher.addDefault("Test", 0);
        autonSwitcher.addInteger("Test-Drive V1", 0); 
        autonSwitcher.addInteger("Test-Drive V2", 1);
        autonSwitcher.addInteger("Test-Turn", 2);
        autonSwitcher.addInteger("One Ball", 3);
        autonSwitcher.addInteger("Two Ball", 4);
        SmartDashboard.putData("Auton Selecter", autonSwitcher);  
                
    }
    
   public void autonomousInit(){
        compressor.stop();

        ac.clear();
        catapult.firstRun = true;

        driveTrain.resetGyro();
        driveTrain.resetEncoders();
        
         switch(autonSwitcher.getSelected()){
             case 0:
                ac = autonSeq.testAutonDriveV1();
                break;
            case 1:
                ac = autonSeq.testAutonDriveV2();
                break;                
            case 2:
                ac = autonSeq.testAutonTurn();
                break;
            case 3:
                ac = autonSeq.shootBallDriveForward();
                break;
            case 4:
                ac = autonSeq.twoBallDriveForward();
                break;
            case 5:
                ac = autonSeq.twoBallHotDriveForward();
                break;
            case 6:
                ac = autonSeq.oneBallHotDriveForward();
                break;

         }
    }
    
    public void autonomousPeriodic() {
        ac.executeCommands();
        updateDSLCD();
        updateSmartDashboard();
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
        //catapult.holdTrussPistonPos(toolPad.getRawButton(GamepadConstants.RIGHT_TRIGGER));
        
        //Winch code
        catapult.windWinch(winchJoy, toolPad.getRawButton(GamepadConstants.B_BUTTON), //preset one
                                     toolPad.getRawButton(GamepadConstants.A_BUTTON)); //preset two
        
       catapult.disengageWinch(toolPad.getRawButton(GamepadConstants.RIGHT_BUMPER));
       //catapult.setWinchPWM(winchJoy);

        updateSmartDashboard();
        updateDSLCD();
        
    }
    private void updateSmartDashboard() {
        SmartDashboard.putString("Left Target", SmartDashboard.getString("Left Target", "No Connection"));
        SmartDashboard.putString("Right Target", SmartDashboard.getString("Right Target", "No Connection"));
    }
    private void updateDSLCD() {
        
        dsLCD.println(DriverStationLCD.Line.kUser1, 1, "LEnc: "
        + (Math.floor(driveTrain.getLeftEncoderDist()*10) / 10.0) + " REnc: " 
        + (Math.floor(driveTrain.getRightEncoderDist()*10) / 10.0));
        
        dsLCD.println(DriverStationLCD.Line.kUser2, 1, "Gyro: "
        + Math.floor(driveTrain.getGyroAngle() * 100) / 100);
        
        dsLCD.println(DriverStationLCD.Line.kUser3, 1, "?:" + (catapult.winchOnTarget() ? 1 : 0) 
                                                      + " Catapult Pot: "+ catapult.getWinchPot());
        
        dsLCD.println(DriverStationLCD.Line.kUser4, 1, "WEngaged?: " + (catapult.isEngaged() ? 1 : 0)); //+ " TP?: " + (catapult.isTrussPistonExtended() ? 1 : 0));
        
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
