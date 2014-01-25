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

        Talon leftDriveBottomLeft;
        Talon leftDriveTopMiddle;
        Talon leftDriveBottomRight;
        
        Talon rightDriveBottomLeft;
        Talon rightDriveTopMiddle;
        Talon rightDriveBottomRight;
        
        RelativeGyro driveGyro;

        JoystickScaler leftAnalogScaler = new JoystickScaler();
        JoystickScaler rightAnalogScaler = new JoystickScaler();

        public DriveTrain()
        {

            leftDriveBottomLeft = new Talon(ElectricalConstants.TOP_RIGHT_DRIVE_PWM);
            leftDriveTopMiddle = new Talon(ElectricalConstants.BACK_RIGHT_DRIVE_PWM);
            leftDriveBottomRight = new Talon(ElectricalConstants.FRONT_RIGHT_DRIVE_PWM);
            
            rightDriveBottomLeft = new Talon(ElectricalConstants.TOP_LEFT_DRIVE_PWM);
            rightDriveTopMiddle = new Talon(ElectricalConstants.BACK_LEFT_DRIVE_PWM);
            rightDriveBottomRight = new Talon(ElectricalConstants.FRONT_LEFT_DRIVE_PWM);

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
        leftDriveBottomRight.set(speed);
        leftDriveBottomLeft.set(speed);
        leftDriveTopMiddle.set(speed);
        }
        public void setRightSpeed(double speed)
        {
            if (Math.abs(speed) < 0.05 ) 
            {
                speed = 0.0;
            }
        rightDriveBottomRight.set(speed);
        rightDriveBottomLeft.set(speed);
        rightDriveTopMiddle.set(speed);
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
