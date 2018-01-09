import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;

public class PilotControl {
	
	private MovePilot pilot;
	private EV3TouchSensor leftBumper, rightBumper;
	private EV3UltrasonicSensor uSensor;
	private EV3ColorSensor ColorSensor;
	private SampleProvider lPress, rPress, uDistance, ColorProvider;
	private float[] lSample, rSample, uSample, ColorSample;
	private OdometryPoseProvider opp;
	
	private static final double WHEEL_DIAMETER = 4.4;
	
	public PilotControl() {
		/* Setup Pilot */
		Brick myEV3 = BrickFinder.getDefault();
		Wheel leftWheel = WheeledChassis.modelWheel(Motor.B, WHEEL_DIAMETER).offset(-5.55);
		Wheel rightWheel = WheeledChassis.modelWheel(Motor.D, WHEEL_DIAMETER).offset(5.55);
		Chassis myChassis = new WheeledChassis( new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new MovePilot(myChassis);
		pilot.setAngularSpeed(50);
		pilot.setLinearSpeed(8);
		/* Setup Pose Provider */
		opp = new OdometryPoseProvider(pilot);
		
		/* Setup Bumper Sensors */
		leftBumper = new EV3TouchSensor(myEV3.getPort("S1"));
		lPress = leftBumper.getTouchMode();
		lSample = new float[lPress.sampleSize()];
		rightBumper = new EV3TouchSensor(myEV3.getPort("S4"));
		rPress = rightBumper.getTouchMode();
		rSample = new float[rPress.sampleSize()];
		
		/* Setup Ultrasonic sensor */
		uSensor = new EV3UltrasonicSensor(myEV3.getPort("S3"));
		uDistance = uSensor.getDistanceMode();
		uSample = new float[uDistance.sampleSize()];
		//uRange = 0.4;
		
		/* Setup Color Sensor */
		ColorSensor = new EV3ColorSensor(myEV3.getPort("S2"));
		ColorProvider = ColorSensor.getRGBMode();
		ColorSample = new float[ColorProvider.sampleSize()];
	}
	
	public void closeRobot() {
		pilot.stop();
	}
	
	// Method returns boolean based on if left bumper pressed
	public boolean isLeftBumperPressed() {
		lPress.fetchSample(lSample, 0);
		if (lSample[0] == 0) return false;
		return true;
	}
		
	// Method returns boolean based on if right bumper pressed
	public boolean isRightBumperPressed() {
		rPress.fetchSample(rSample, 0);
		if (rSample[0] == 0) return false;
		return true;
	}
	
	public float getDistance() {
		uDistance.fetchSample(uSample, 0);
		return uSample[0];
	}
	
	public void rotateDistanceSensor(int angleDegree) {
		Motor.C.rotate(angleDegree);
	}
	
	/**
	 * 0-7 none, black, blue, green, yellow, red, white, brown
	 * @return
	 */
	public int getColor() {
		ColorProvider.fetchSample(ColorSample, 0);
		System.out.println("Color: "+ColorSample.length);
		if(ColorSample[0]<0.1 && ColorSample[1]<0.1 && ColorSample[2]<0.1)
			return 1;
		else if(ColorSample[0]<0.1 && ColorSample[1]<0.2 && ColorSample[1]>0.1 && ColorSample[2]>0.1)
			return 2;
		else if(ColorSample[0]>0.1 && ColorSample[1]<0.03 && ColorSample[2]<0.03)
			return 5;
		else if(ColorSample[0]<0.1 && ColorSample[1]>0.1 && ColorSample[2]<0.1)
			return 3;
		else
			return 0;
	}
	
	public OdometryPoseProvider getPoseProvider() {
		return opp;
	}
	
	public double getWheelDiameter() {
		return WHEEL_DIAMETER;
	}
	
	public MovePilot getPilot() {
		return pilot;
	}
}
