import lejos.robotics.subsumption.Behavior;

public class ActionImpl implements Behavior {
	
	private PilotControl pilot;
	private boolean suppressed;
	private RobotComm communication;
	
	public ActionImpl(PilotControl pilot) {
		this.pilot = pilot;
		communication = new RobotComm();
	}
	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		
		suppressed = false;
		
		String message = "";
		message = communication.getMessage();
		while(!suppressed) {
			//System.out.println("round");
			if(message.equals("NoMessage")) {
				message = communication.getMessage();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			//System.out.println("The received message is:" +message);
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
			case "left" :
				System.out.println("turn left now");
				pilot.getPilot().rotate(-87);
				break;
			case "RIGHT" :
				pilot.getPilot().rotate(87);
				break;
			case "BACK" :
				message = communication.getMessage();
				pilot.getPilot().rotate(-87);
				pilot.getPilot().rotate(-87);
				pilot.getPilot().travel(Double.valueOf(message));
				break;			
			}
			message = communication.getMessage();
			System.out.println("The received message is: "+message);
		}
		System.out.println("Something wrong");
	}

	@Override
	public void suppress() {
		suppressed = true;
	}
	
}
