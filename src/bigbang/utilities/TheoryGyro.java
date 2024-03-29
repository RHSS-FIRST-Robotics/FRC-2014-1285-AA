/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2012. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package bigbang.utilities;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.communication.UsageReporting;


/**
 * Use a rate gyro to return the robots heading relative to a starting position.
 * The Gyro class tracks the robots heading based on the starting position. As the robot
 * rotates the new heading is computed by integrating the rate of rotation returned
 * by the sensor. When the class is instantiated, it does a short calibration routine
 * where it samples the gyro while at rest to determine the default offset. This is
 * subtracted from each sample to determine the heading.
 */
public class TheoryGyro extends SensorBase {
  static final int kOversampleBits = 10;
  static final int kAverageBits = 0;
  static final double kSamplesPerSecond = 50.0;
  static final double kCalibrationSampleTime = 5.0;
  static final double kDefaultVoltsPerDegreePerSecond = 0.007;
  AnalogChannel m_analog;
  double m_voltsPerDegreePerSecond;
  double m_offset;
  boolean m_channelAllocated;
  AccumulatorResult result;

  /**
    * Initialize the gyro.
    * Calibrate the gyro by running for a number of samples and computing the center value for this
    * part. Then use the center value as the Accumulator center value for subsequent measurements.
    * It's important to make sure that the robot is not moving while the centering calculations are
    * in progress, this is typically done when the robot is first turned on while it's sitting at
    * rest before the competition starts.
    */
  public void initGyro() {
    result = new AccumulatorResult();
    if (m_analog == null) {
      System.out.println("Null m_analog");
    }
    m_voltsPerDegreePerSecond = kDefaultVoltsPerDegreePerSecond;
    m_analog.setAverageBits(kAverageBits);
    m_analog.setOversampleBits(kOversampleBits);
    double sampleRate = kSamplesPerSecond * (1 << (kAverageBits + kOversampleBits));
    m_analog.getModule().setSampleRate(sampleRate);

    Timer.delay(1.0);
    m_analog.initAccumulator();

    Timer.delay(kCalibrationSampleTime);

    m_analog.getAccumulatorOutput(result);

    int center = (int) ((double)result.value / (double)result.count + .5);

    m_offset = ((double)result.value / (double)result.count) - (double)center;

    m_analog.setAccumulatorCenter(center);

    m_analog.setAccumulatorDeadband(0); ///< TODO: compute / parameterize this
    m_analog.resetAccumulator();

    UsageReporting.report(UsageReporting.kResourceType_Gyro, m_analog.getChannel(), m_analog.getModuleNumber()-1);
  }

  /**
    * Gyro constructor given a slot and a channel.
  .
    * @param slot The cRIO slot for the analog module the gyro is connected to.
    * @param channel The analog channel the gyro is connected to.
    */
  public TheoryGyro(int slot, int channel) {
    m_analog = new AnalogChannel(slot, channel);
    m_channelAllocated = true;
    initGyro();
  }

  /**
    * Gyro constructor with only a channel.
    *
    * Use the default analog module slot.
    *
    * @param channel The analog channel the gyro is connected to.
    */
  public TheoryGyro(int channel) {
    m_analog = new AnalogChannel(channel);
    m_channelAllocated = true;
    initGyro();
  }

  /**
    * Gyro constructor with a precreated analog channel object.
    * Use this constructor when the analog channel needs to be shared. There
    * is no reference counting when an AnalogChannel is passed to the gyro.
    * @param channel The AnalogChannel object that the gyro is connected to.
    */
  public TheoryGyro(AnalogChannel channel) {
    m_analog = channel;
    if (m_analog == null) {
        System.err.println("Analog channel supplied to Gyro constructor is null");
    } else {
        m_channelAllocated = false;
        initGyro();
    }
  }

  /**
    * Reset the gyro.
    * Resets the gyro to a heading of zero. This can be used if there is significant
    * drift in the gyro and it needs to be recalibrated after it has been running.
    */
  public void reset() {
    if (m_analog != null) {
        m_analog.resetAccumulator();
    }
  }

  /**
    * Delete (free) the accumulator and the analog components used for the gyro.
    */
  public void free() {
    if (m_analog != null && m_channelAllocated) {
        m_analog.free();
    }
    m_analog = null;
  }

  /**
    * Return the actual angle in degrees that the robot is currently facing.
    *
    * The angle is based on the current accumulator value corrected by the oversampling rate, the
    * gyro type and the A/D calibration values.
    * The angle is continuous, that is can go beyond 360 degrees. This make algorithms that wouldn't
    * want to see a discontinuity in the gyro output as it sweeps past 0 on the second time around.
    *
    * @return the current heading of the robot in degrees. This heading is based on integration
    * of the returned rate from the gyro.
    */
  public double getAngle() {
    if (m_analog == null) {
      return 0.0;
    } else {
      m_analog.getAccumulatorOutput(result);

      long value = result.value - (long) (result.count * m_offset);

      double scaledValue = value * 1e-9 * m_analog.getLSBWeight() * (1 << m_analog.getAverageBits()) /
              (m_analog.getModule().getSampleRate() * m_voltsPerDegreePerSecond);

      return scaledValue;
    }
  }

  /**
    * Set the gyro type based on the sensitivity.
    * This takes the number of volts/degree/second sensitivity of the gyro and uses it in subsequent
    * calculations to allow the code to work with multiple gyros.
    *
    * @param voltsPerDegreePerSecond The type of gyro specified as the voltage that represents one degree/second.
    */
  public void setSensitivity(double voltsPerDegreePerSecond) {
    m_voltsPerDegreePerSecond = voltsPerDegreePerSecond;
  }

}
