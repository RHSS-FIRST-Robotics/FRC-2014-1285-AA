    /**
    *
    * @author Rohan K.
    */      

    package BigBang.subsystems;

    import edu.wpi.first.wpilibj.Encoder;
    import BigBang.utilities.RelativeGyro;
    import BigBang.main.ElectricalConstants;
    import BigBang.utilities.JoystickScaler;
    import edu.wpi.first.wpilibj.Talon;

    public class DriveTrain 
    {
        static DriveTrain inst = null;

        Encoder leftDriveEncoder;
        Encoder rightDriveEncoder;

        Talon leftDriveBackAndFront;
        Talon leftDriveTop;
        
        Talon rightDriveBackAndFront;
        Talon rightDriveTop;
        
        RelativeGyro driveGyro;

        JoystickScaler leftAnalogScaler = new JoystickScaler();
        JoystickScaler rightAnalogScaler = new JoystickScaler();

        public DriveTrain()
        {

            leftDriveBackAndFront = new Talon(ElectricalConstants.FRONT_AND_BACK_LEFT_DRIVE_PWM);
            leftDriveTop = new Talon(ElectricalConstants.TOP_LEFT_DRIVE_PWM);
            
            rightDriveBackAndFront = new Talon(ElectricalConstants.FRONT_AND_BACK_RIGHT_DRIVE_PWM);
            rightDriveTop = new Talon(ElectricalConstants.TOP_RIGHT_DRIVE_PWM);

        }
            public static DriveTrain getInstance() 
            {
            if(inst == null) 
            {
                inst = new DriveTrain();
            }
            return inst;
        }
        public void setLeftSpeed(double speed) 
        {

        if (Math.abs(speed) < 0.05 ) 
//If analog stick is less than 0.05, just set speed to 0. To avoid sensitive touch.
        {
            speed = 0.0;
        }
        leftDriveBackAndFront.set(speed);
        leftDriveTop.set(speed);
        }
        public void setRightSpeed(double speed)
        {
            if (Math.abs(speed) < 0.05 ) 
            {
                speed = 0.0;
            }
        rightDriveBackAndFront.set(speed);
        rightDriveTop.set(speed);
        }


        public void tankDrive (double leftJoy, double rightJoy, int scaledPower) 
        {
            setLeftSpeed(leftAnalogScaler.scaleJoystick(leftJoy, scaledPower)); //Set speed of both sides according 
            setRightSpeed(rightAnalogScaler.scaleJoystick(rightJoy, scaledPower));//to the scale of joystick
        }


        /************************ENCODER FUNCTIONS************************/

        public void setEncoderDistPerPulse(double leftDistPerPulse, double rightDistPerPulse) 
        {
            leftDriveEncoder.setDistancePerPulse(leftDistPerPulse); 
            rightDriveEncoder.setDistancePerPulse(rightDistPerPulse); 
        }

        public void stopEncoders() 
        {
            leftDriveEncoder.stop();
            rightDriveEncoder.stop();
        }

        public double getLeftEncoderDist() 
        {
            return leftDriveEncoder.getDistance();
        }

        public double getRightEncoderDist() 
        {
            return rightDriveEncoder.getDistance();
        }

        public double getLeftEncoderRaw() 
        {
            return leftDriveEncoder.getRaw();
        }

        public double getRightEncoderRaw() 
        {
            return rightDriveEncoder.getRaw();
        }

        public double getLeftEncoderRate() 
        {
            return leftDriveEncoder.getRate();
        }

        public double getRightEncoderRate() 
        {
            return rightDriveEncoder.getRate(); 
        }

    /************************RESET FUNCTIONS*************************/

        public void resetEncoders()
        {
            leftDriveEncoder.reset();
            rightDriveEncoder.reset();
        }
    /************************GYRO FUNCTIONS**************************/
    public double getGyroAngle()
    {
        return (driveGyro.getAngle() / 168.2)*180.0;
        

    }
    
    public void resetGyro()
    {
        driveGyro.reset();
    }
    


    }
