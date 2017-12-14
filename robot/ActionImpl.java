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
				if(pilot.getColor() == 1)
					communication.send("BLACK");
				else if(pilot.getColor() == 2)
					communication.send("BLUE");
				else if(pilot.getColor() == 3)
					communication.send("GREEN");
				else if(pilot.getColor() == 5)
					communication.send("RED");
				else if(pilot.getColor() == 0)
					communication.send("NoColor");
				else
					communication.send("WrongColor");
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
