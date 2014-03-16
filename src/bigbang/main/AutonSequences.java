/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bigbang.main;

import bigbang.subsystems.DriveTrain;
import bigbang.auton.*;
import bigbang.auton.commands.*;

import bigbang.utilities.Constants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

                                                                                                                
/**
 *
 * @author Sagar
 */
public class AutonSequences {
        
    DriveTrain driveTrain = DriveTrain.getInstance();
     final int LEFT_HOT = 0;
    final int RIGHT_HOT = 1;
    final int DID_NOT_DETECT = 2;
   public AutonController shootBallDriveForward() { //finalized sequence
        AutonController ac = new AutonController();
        ac.clear();
        ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
        ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("OBDriveForwardDist"),
                                   Constants.getDouble("OBDriveAngle"),
                                   Constants.getDouble("OBDriveDistTimeout")));
        

        return ac;
    }
   
   public AutonController oneBallHotDriveForward() { //finalized sequence
        AutonController ac = new AutonController();
        ac.clear();
        Constants.getInstance();
       
        int whichGoal = 0;
        
        ac.addCommand(new WaitCommand(Constants.getDouble("camTrackTime")));
            
        if((SmartDashboard.getString("Left Target", "No Connection") == "Hot")) {
           ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("OBHTurnLeft1"),Constants.getDouble("OBHTurnLeftTimeout1")));
           whichGoal = LEFT_HOT;
        }
        else if((SmartDashboard.getString("Right Target", "No Connection") == "Hot")) {
           ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("OBHTurnRight1"),Constants.getDouble("OBHTurnRightTimeout1")));
           whichGoal = RIGHT_HOT;
        }
        else {
            ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("OBHTurnLeft1"),Constants.getDouble("OBHTurnLeftTimeout1")));
            whichGoal = DID_NOT_DETECT;
        }
        

        ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
        
        switch (whichGoal) {
            case LEFT_HOT:
            case DID_NOT_DETECT:
                ac.addCommand(new TurnDegreesTimeOutCommand(-Constants.getDouble("OBHTurnLeft1"),Constants.getDouble("OBHTurnLeftTimeout1")));
                break;
                
            case RIGHT_HOT:
                ac.addCommand(new TurnDegreesTimeOutCommand(-Constants.getDouble("OBHTurnRight1"),Constants.getDouble("OBHTurnRightTimeout1")));
                break;
        }
        
        ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("OBHDriveForwardDist"),
                                   Constants.getDouble("OBHDriveAngle"),
                                   Constants.getDouble("OBHDriveDistTimeout")));

        return ac;
    }
   
   public AutonController twoBallHotDriveForward() {
        AutonController ac = new AutonController();
        ac.clear();
        Constants.getInstance();
        
        int whichGoal = 0;
        
        ac.addCommand(new WaitCommand(Constants.getDouble("camTrackTime")));
            
        if((SmartDashboard.getString("Left Target", "No Connection") == "Hot")) {
           ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("TBHTurnLeft1"),Constants.getDouble("TBHTurnLeftTimeout1")));
           whichGoal = LEFT_HOT;
        }
        else if((SmartDashboard.getString("Right Target", "No Connection") == "Hot")) {
           ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("TBHTurnRight1"),Constants.getDouble("TBHTurnRightTimeout1")));
           whichGoal = RIGHT_HOT;
        }
        else {
            ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("TBHTurnLeft1"),Constants.getDouble("TBHTurnLeftTimeout1")));
            whichGoal = DID_NOT_DETECT;
        }
        

        ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
        
        ac.addCommand(new EngageWinchCommand(Constants.getDouble("bWinchShiftTime")));
        
        switch (whichGoal) {
            case LEFT_HOT:
            case DID_NOT_DETECT:
                ac.addCommand(new TwoParallelMotionsCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("TBHLeftTurnLeft2"),Constants.getDouble("TBHTurnTimeoutLeft2")),
                        0,
                        new TwoParallelMotionsCommand(new WindBackWinchTimeOutCommand(Constants.getDouble("TBHWinchSetpoint"),Constants.getDouble("TBHSecondShotWindTimeout")),
                        0,
                        new SetIntakePositionCommand())));
                break;
                
            case RIGHT_HOT:
                ac.addCommand(new TwoParallelMotionsCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("TBHRightTurnLeft2"),Constants.getDouble("TBHTurnTimeoutRight2")),
                        0,
                        new TwoParallelMotionsCommand(new WindBackWinchTimeOutCommand(Constants.getDouble("TBHWinchSetpoint"),Constants.getDouble("TBHSecondShotWindTimeout")),
                        0,
                        new SetIntakePositionCommand())));
                break;
        }
        
        ac.addCommand(new TwoParallelMotionsCommand(new DriveToPosTimeOutCommand(Constants.getDouble("TBHDriveForwardDist1"), 
                                             Constants.getDouble("TBHDriveAngle1"), 
                                             Constants.getDouble("TBHDriveDistTimeout1")),0,
                                             new TwoParallelMotionsCommand(new WindBackWinchTimeOutCommand(Constants.getDouble("TBHWinchSetpoint"),
                                             Constants.getDouble("TBHSecondShotWindTimeout")),
                                             0,(new IntakeTimeOutCommand(Constants.getDouble("TBHintakePWM"),Constants.getDouble("TBHintakeTime"))))));
        
        ac.addCommand(new TwoParallelMotionsCommand(new IntakeTimeOutCommand(Constants.getDouble("TBHintakePWM"),Constants.getDouble("TBHintakeTime")),
                 0, 
                 new WindBackWinchTimeOutCommand(Constants.getDouble("TBWinchSetpoint"),Constants.getDouble("TBSecondShotWindTimeout"))));
         
        ac.addCommand(new TwoParallelMotionsCommand(new WindBackWinchTimeOutCommand(Constants.getDouble("TBWinchSetpoint"),Constants.getDouble("TBSecondShotWindTimeout")),
                0,
                new TwoParallelMotionsCommand(new SetIntakePositionCommand(),
                0,
                new IntakeTimeOutCommand(Constants.getDouble("TBHintakePWM"),Constants.getDouble("TBHintakeTime")))));
         
        ac.addCommand(new IntakeTimeOutCommand(0,0.1));
        
        ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("TBHTurnToShoot"),Constants.getDouble("TBHTurnToShootTimeout")));
        
       
        
        ac.addCommand(new WaitCommand(Constants.getDouble("TBHFinalWait")));
        ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
        
        ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("TBHDriveForwardDist2"), 
                                                   Constants.getDouble("TBHDriveAngle2"), 
                                                   Constants.getDouble("TBHDriveDistTimeout2")));

        return ac;
    }

   public AutonController shootGoalAndAllianceDriveForward() {
       
       AutonController ac = new AutonController();
       ac.clear();
       
       ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
       ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("aAlliancePartnerAngle"), 
                                 Constants.getDouble("aTurnDegreesTimeOut")));
       //ac.addCommand(new DriveForwardIntakeBallTimeOutCommand(Constants.getDouble("aDistanceToAlliancePartner"), 
                                            //Constants.getDouble("aDriveForwardTimeOut")));
       ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
       ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("aDistanceToNewZone"),
                                  0,
                                  Constants.getDouble("aDriveToNewZoneTimeOut")));
       
       return ac;
       
   }
   public AutonController shootOtherGoalAndAllianceDriveForward() {
       
       AutonController ac = new AutonController();
       ac.clear();
       
       ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("aOtherGoalAngle"), 
                                 Constants.getDouble("aTurnDegreesTimeOut")));
       ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
       ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("aAlliancePartnerAngleTwo"), 
                                 Constants.getDouble("aTurnDegreesTimeOut")));
       //ac.addCommand(new DriveForwardIntakeBallTimeOutCommand(Constants.getDouble("aDistanceToAllianceTwo"), 
                                     //Constants.getDouble("aDriveForwardTimeOut")));
       
       ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("aTurnDegreesToGoal"), 
                                 Constants.getDouble("aTurnDegreesTimeOut")));
       ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
       ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("aDriveToNewZone"), 
                                  0, 
                                  Constants.getDouble("aDriveToNewZoneTimeOut")));
       
       return ac;
   }   
      
   
       
       public AutonController testAutonDriveV1() { //NEW CONSTANTS UPDATE!
        AutonController ac = new AutonController();
        ac.clear();
   
        Constants.getInstance();   
        ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("testDriveDistV2"), //new
                                                   Constants.getDouble("testDriveAngleV2"), //angle goal
                                                   Constants.getDouble("testDriveDistTimeoutV2"))); //new
      // ac.addCommand(new SetIntakePositionCommand());
       //ac.addCommand(new IntakeTimeOutCommand(Constants.getDouble("intakePWM"),Constants.getDouble("intakeTime")));
        return ac;
    }
    
    public AutonController testAutonDriveV2() { 
        AutonController ac = new AutonController();
        ac.clear();
   
        Constants.getInstance();   
        
        ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("testDriveDistV2"), //new
                                                     Constants.getDouble("testDriveAngleV2"), //angle goal
                                                     Constants.getDouble("testDriveDistTimeoutV2"))); //new
        ac.addCommand(new WaitCommand(1));
        ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
                
        return ac;
    }
    
    public AutonController driveAndShootOneBall() {
        AutonController ac = new AutonController();
        ac.clear();
        Constants.getInstance();
        ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("OBDriveForwardDist"), //new
                                                     Constants.getDouble("OBDriveAngle"), //angle goal
                                                     Constants.getDouble("OBDriveDistTimeout"))); //new
        return ac;
    }
    
    public AutonController twoBallDriveForward() {
        AutonController ac = new AutonController();
        ac.clear();
        Constants.getInstance();
        ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
        ac.addCommand(new EngageWinchCommand(Constants.getDouble("bWinchShiftTime")));
        //ac.addCommand(new WindBackWinchTimeOutCommand(Constants.getDouble("bWinchPosTwo"),Constants.getDouble("TBSecondShotWindTimeout")));
        ac.addCommand(new TwoParallelMotionsCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("twoBallTurn1"),Constants.getDouble("twoBallTurnTimeout1")),0,new TwoParallelMotionsCommand(
                                new WindBackWinchTimeOutCommand(Constants.getDouble("TBWinchSetpoint"),Constants.getDouble("TBSecondShotWindTimeout")),0,new SetIntakePositionCommand())));
        ac.addCommand(new TwoParallelMotionsCommand(new DriveToPosTimeOutCommand(Constants.getDouble("TBDriveForwardDist1"), 
                                                     Constants.getDouble("TBDriveAngle1"), 
                                                     Constants.getDouble("TBDriveDistTimeout1")),0,new TwoParallelMotionsCommand(new WindBackWinchTimeOutCommand(Constants.getDouble("TBWinchSetpoint"),Constants.getDouble("TBSecondShotWindTimeout")),0,(new IntakeTimeOutCommand(Constants.getDouble("intakePWM"),Constants.getDouble("intakeTime"))))));
                 //ac.addCommand(new WaitCommand(Constants.getDouble("TBWait")));
        ac.addCommand(new TwoParallelMotionsCommand(new IntakeTimeOutCommand(Constants.getDouble("intakePWM"),Constants.getDouble("TBWait")) , 0, new WindBackWinchTimeOutCommand(Constants.getDouble("TBWinchSetpoint"),Constants.getDouble("TBSecondShotWindTimeout"))));
         ac.addCommand(new TwoParallelMotionsCommand(new WindBackWinchTimeOutCommand(Constants.getDouble("TBWinchSetpoint"),Constants.getDouble("TBSecondShotWindTimeout")),0,new TwoParallelMotionsCommand(new SetIntakePositionCommand(),0,new IntakeTimeOutCommand(Constants.getDouble("intakePWM"),Constants.getDouble("intakeTime")))));
         ac.addCommand(new IntakeTimeOutCommand(0,0.1));
         ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("twoBallTurn2"),Constants.getDouble("twoBallTurnTimeout2")));
         ac.addCommand(new WaitCommand(Constants.getDouble("SecondWait")));
         ac.addCommand(new ShootBallTimeOutCommand(Constants.getDouble("TBWinchDisengageTimeout")));
         ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("TBDriveForwardDist2"), 
                                                     Constants.getDouble("TBDriveAngle2"), 
                                                     Constants.getDouble("TBDriveDistTimeout2")));
//        ac.addCommand(new SetIntakePositionCommand());
//        ac.addCommand(new TwoParallelMotionsCommand((new IntakeTimeOutCommand(Constants.getDouble("intakePWM"),Constants.getDouble("intakeTime"))),0,
//                new DriveToPosTimeOutCommand(Constants.getDouble("TBDriveForwardDist1"), 
//                                                     Constants.getDouble("TBDriveAngle1"), 
//                                                     Constants.getDouble("TBDriveDistTimeout1")))); 
//        ac.addCommand(new SetIntakePositionCommand());
//        ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("twoBallTurn2"),Constants.getDouble("twoBallTurnTimeout2")));
//                ac.addCommand(new DriveToPosTimeOutCommand(Constants.getDouble("TBDriveForwardDist2"), //new
//                                                     Constants.getDouble("TBDriveAngle2"), //angle goal
//                                                     Constants.getDouble("TBDriveDistTimeout2"))); //new
//        ac.addCommand(new ShootBallTimeOutCommand());
        return ac;
    }
    
    public AutonController testAutonTurn() { 
        AutonController ac = new AutonController();
        ac.clear();
   
        Constants.getInstance();   
        
        ac.addCommand(new TurnDegreesTimeOutCommand(Constants.getDouble("testAngleGoal"), 
                                                    Constants.getDouble("testAngleTimeout"))); 
        return ac;
    } 
 
}

