import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.subsumption.Behavior;

public class ActionImpl implements Behavior {
	
	private PilotControl pilot;
	private boolean suppressed;
	private OdometryPoseProvider opp;
	private RobotComm communication;
	
	public ActionImpl(PilotControl pilot, RobotComm communication) {
		this.pilot = pilot;
		this.communication = communication;
	}
	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		
		suppressed = false;
		
		String message = communication.getMessage();
		while(!suppressed) {
			if(message.equals("NoMessage"))
				continue;
			switch(message) {
			case "DISTANCE" :
				communication.send(String.valueOf(pilot.getDistance()));
				break;
			case "COLOR" :
				communication.send(String.valueOf(pilot.getColor()));
				break;
			case "FORWARD" :
				message = communication.getMessage();
				pilot.getPilot().travel(Double.valueOf(message));
				break;
			case "LEFT" :
				pilot.getPilot().rotate(-90);
				break;
			case "RIGHT" :
				pilot.getPilot().rotate(90);
				break;
			case "BACK" :
				message = communication.getMessage();
				pilot.getPilot().rotate(90);
				pilot.getPilot().rotate(90);
				pilot.getPilot().travel(Double.valueOf(message));
				break;
			}
		}
	}

	@Override
	public void suppress() {
		suppressed = true;
	}
	
}
