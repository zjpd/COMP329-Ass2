import lejos.hardware.Button;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {
	
	public static void main(String args[]) {
		//Map map = new Map();
		PilotControl pilot = new PilotControl();
		RobotComm communication = new RobotComm();
		//PilotMonitor monitor = new PilotMonitor(400, map);
		
		/* Wait for button press and begin running behaviours */
		
		Button.waitForAnyPress();
		Behavior mainBehavior = new ActionImpl(pilot, communication);
		Behavior[] bArray = {mainBehavior};
		Arbitrator arbitrator = new Arbitrator(bArray);
		arbitrator.go();
		
		pilot.closeRobot();
		Button.waitForAnyPress();
	}

}
