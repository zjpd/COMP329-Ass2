import lejos.hardware.Button;
import lejos.robotics.Color;
import lejos.robotics.subsumption.Behavior;

public class TestCalibration {

	private static PilotRobot me;
	
	//stores values for all five checks into the array 
	static float[]north = new float[4];
	static float[]east = new float[4];
	static float[]south = new float[4];
	static float[]west = new float[4];
	
	//calculated mean for all checks and their five values stored into these variables
	//new values
	private static float newNorth;
	private static float newEast;
	private static float newSouth;
	private static float newWest;
	
	//check if the next cells are occupied
	private final static double CELLOCCUPIED = 0.25;
	
	//check if the next cells are empty
	private final static double CELLEMPTY = 0.30; 
	
	//giving each turn their angle 
	private final static int NORTH_TURN = 0;
	private final static int EAST_TURN = 90;
	private final static int SOUTH_TURN = 180;
	private final static int WEST_TURN = 270;

	public static void storeDistance() {
		float sum = 0;
		int i;
			
		//check north and calculate mean
		for(i=0;i<north.length;i++){
			//check north
			sum += me.checkFront();
	     }
		//calculate mean
		newNorth = sum/i;
		System.out.println("North : " + newNorth);
		
		me.getPilot().rotate(EAST_TURN);
		
		sum = 0;
		//check east and calculate mean 
		for (i=0;i<east.length;i++) {
			sum += me.checkRight(); 
		}
		newEast = sum/i;
		System.out.println("East : " + newEast);
		
		me.getPilot().rotate(EAST_TURN);
		
		sum = 0;
		//check south and calculate mean
		for (i=0;i<south.length;i++) {
			sum += me.checkBehind();
			
		}
		newSouth = sum/i;
		System.out.println("south : " + newSouth);
		
		
		me.getPilot().rotate(EAST_TURN);
		
		sum = 0;
		//check west and calculate mean
		for (i=0;i<west.length;i++) {
			sum += (float) me.checkLeft(); 
			 	
		}
		newWest = sum/i;
		System.out.println("west : " + newWest);

		//back to original position
		me.getPilot().rotate(EAST_TURN);
		
		try {
			Thread.sleep(1000);
		} catch (Exception e){}
		
		cellCalibration();
		
	}
	
	//this method stores the distance it has travelled
	public static float getDistanceTravelledB() {
		float travelledB;
		travelledB = me.getPilot().getMovement().getDistanceTraveled();
		return travelledB;
	}
	
	public static float sensorEast() {
		float east;
		me.sensorMotor.rotate(-90);
        east = me.getDistance();
        me.sensorMotor.rotate(90);
        return east;
	}
	 
	public static float sensorWest() {
		float west;
		me.sensorMotor.rotate(90);
        west = me.getDistance();
        me.sensorMotor.rotate(-90);
        return west;
	} 
	
	
	public static void calibration1() {
		
		//moves forward
		me.getPilot().forward();
		//till black line detected
		while (me.getColour() != Color.BLACK) {
			Thread.yield();
		}
		//stops if black found
		me.getPilot().stop();
		//robot moves backwards
		me.getPilot().backward();
		try {
			Thread.sleep(1000);
		} catch (Exception e){}
		//till black line detected
		while (me.getColour() != Color.BLACK) {
			Thread.yield();
		}
		float travelledDistance = getDistanceTravelledB();
		System.out.println("get distance travelled before stopped: " + travelledDistance);
		me.getPilot().stop();
		System.out.println("get distance travelled after stopped: " + travelledDistance);
		//travels the travelled the distance from black to black / 2
		me.getPilot().travel(travelledDistance / (float) 2);
		
	}
	
	
	public static void calibration3() {
		
		//moves forward
		me.getPilot().forward();
		//till bumper pressed
		while (!(me.leftBumper() && me.rightBumper())) {
			Thread.yield();
		}
		//get distance before stop
		getDistanceTravelledB();
		//stops if bumper pressed
		me.getPilot().stop();
		try {
			Thread.sleep(1000);
		} catch (Exception e){}
		//travel backward to original position
		me.getPilot().travel(-(getDistanceTravelledB()));
		
		me.getPilot().stop();
		
	}
	
	
	
	
	public static void cellCalibration() {
		//if the cell ahead and behind is empty then move into that cell until the black line is detected
		if (newNorth >= CELLEMPTY && newSouth >= CELLEMPTY) {
			calibration1();
			//checks cells to the right and left to perform calibration
			if (newEast >= CELLEMPTY && newWest >= CELLEMPTY ) {
				me.getPilot().rotate(EAST_TURN);
				calibration1();
				me.getPilot().rotate(-(EAST_TURN));
			}
			//if cell right is occupied
			else if (newEast <= CELLOCCUPIED) {
				me.getPilot().rotate(EAST_TURN);
				calibration3();
				me.getPilot().rotate(-(EAST_TURN));
			}
			//cell left is occupied
			else if (newWest<= CELLOCCUPIED) {
				me.getPilot().rotate(-(EAST_TURN));
				calibration3();
				me.getPilot().rotate(EAST_TURN);
			}	
		} else if (newNorth<=CELLOCCUPIED) {
			calibration3();
						
		} else if (newEast <= CELLOCCUPIED) {
			me.getPilot().rotate(EAST_TURN);
			calibration3();
			me.getPilot().rotate(-(EAST_TURN));
			
		}
		
		
	}
		
	public static void stopRobot(){
		if(Button.ESCAPE.isDown()) {
			System.exit(0);
		}
	}	
		
	
	
	public static void main(String[] args) {
		// Constructor - store a reference to the robot
		 me = new PilotRobot();
	    
		System.out.println("Start");
		
		Button.waitForAnyPress();
		if(Button.ESCAPE.isDown()) {
			System.exit(0);
		}
		storeDistance();
//		cellCalibration();
	
		
	}

}
