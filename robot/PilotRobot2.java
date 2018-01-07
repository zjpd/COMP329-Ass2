import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.hardware.port.Port;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

//this is the class where I will be initialising the sensors, sample providers and my variables which I will be calling in other classes.
public class PilotRobot {
    private EV3UltrasonicSensor uSensor;
    private EV3ColorSensor cSensor;
    private SampleProvider distSP, colourSP, leftTouch, rightTouch;
    private float[] distSample, colourSample, leftSample, rightSample;
    private MovePilot pilot;
    EV3MediumRegulatedMotor sensorMotor;
 
    public static double XDISTANCE = 152.4;
    public static double YDISTANCE = 198.12;
    public static int XGRID = 6;
    public static int YGRID = 6;
    public static double CELL_HEIGHT = YDISTANCE / YGRID;
    public static double CELL_WIDTH = XDISTANCE / XGRID;
    //remove the value once you have rotation working
    public String currentAxis = "Y";

    
    public static int direction = 0;
    
    /*
     * These were the variables of the map methods and these included arrays and position of the robot 
     * */
    //--------------------------MAPPING------------------
    //this array will give a reading of the map, print out a grid of the correct size
    private int[][] map;
    //This array will contain average reading for each cell from the uSensor
    //this will help update the map array
    private int[][] dataFound;
     
 
  
 
 
    //-----------------------------------------------------
 
    public PilotRobot() {
         
        Brick myEV3 = BrickFinder.getDefault();
 
        //this port is for the small robot
        uSensor = new EV3UltrasonicSensor(myEV3.getPort("S3"));
        cSensor = new EV3ColorSensor(myEV3.getPort("S2"));
        // 1) Get a port for bumper instance
     	Port leftPort = LocalEV3.get().getPort("S1");
     	Port rightPort = LocalEV3.get().getPort("S4"); 
        
        
        //this port is for the small robot
        EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(myEV3.getPort("B"));
        EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(myEV3.getPort("D"));
        sensorMotor = new EV3MediumRegulatedMotor(myEV3.getPort("C"));
 
        distSP = uSensor.getDistanceMode();     // effective range of the sensor in Distance mode is about 5 to 50 centimeters
        colourSP = cSensor.getColorIDMode();	//returns the colour detected 
 
        distSample = new float[distSP.sampleSize()];        // Size is 1
        colourSample = new float[colourSP.sampleSize()];    // Size is 3
        
        // 2) Get an instance of the sensor on the port
        EV3TouchSensor leftBump = new EV3TouchSensor(leftPort);
     	EV3TouchSensor rightBump = new EV3TouchSensor(rightPort);
     	// 3) Get an instance of the sample provider (in this case the value is 0 or 1)
     	leftTouch= leftBump.getMode("Touch");
     	rightTouch= rightBump.getMode("Touch");
     	leftSample = new float[leftTouch.sampleSize()];
		rightSample = new float[rightTouch.sampleSize()];
		
        //this port is for the small robot
        Wheel leftWheel = WheeledChassis.modelWheel(leftMotor, 4.32).offset(-5.5);
        Wheel rightWheel = WheeledChassis.modelWheel(rightMotor, 4.32).offset(5.5);
 
        Chassis myChassis = new WheeledChassis( new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
 
        pilot = new MovePilot(myChassis);
        
        pilot.setLinearSpeed(15);
        pilot.setAngularAcceleration(10);
        pilot.setAngularSpeed(15);
      
     
    }
 
    //-------------------------------------------------------------
    //get robot direction and return the grid movement
    public int Direction() {
        direction = direction + 1;
        return direction;
    }
 
    //this will calculate the travelling distance for each cell depending on the axis. using the Direction() class
    public double distance() {
 
        if (Direction() % 2 == 0) {
            return YDISTANCE / YGRID;
 
        }
        else {
            return XDISTANCE / XGRID;
        }
 
    }
 
    //----------------------------------------------------------------
 
  //check distance straight on 
    public float checkFront() {
    	float straightSample;
        straightSample = getDistance();
        return straightSample;
 
    }
    
    //check right, motors will rotate, get a distance value, rotate back, and return the distance value 
    public float checkRight() {
 
    	float rightSample;
        rightSample = getDistance();
        return rightSample;
 
    }
    
    public float checkBehind() {
    	
    	float behindSample;
    	behindSample = getDistance();
    	return behindSample;
    }
    
    // check left. motors will rotate, get a distance value, rotate back, and return the distance value 
    public float checkLeft(){
 
    	float leftSample;
        leftSample = getDistance();
        return leftSample;
    }
 
    //------------------------------------------------------------------------------
    //use later
    //(robot.isLeftBumpPressed() || robot.isRightBumpPressed());
 
    
    
  
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
 
    //method to get the distance 
    public float getDistance() {
    	distSP.fetchSample(distSample, 0);    	
        return distSample[0];
    }
    
    public boolean leftBumper() {
    	leftTouch.fetchSample(leftSample, 0);
    	return leftSample[0] == 1;
    }
    
    public boolean rightBumper() {
    	rightTouch.fetchSample(rightSample, 0);
    	return rightSample[0] == 1;
    }
    
  //method to get the colour 
    public float getColour() {
        colourSP.fetchSample(colourSample, 0);
        return colourSample[0];   
    }
    
    //method to get the distance 
//    public float getDistance() {
//    	float sum = 0;
//    	for(int i=0; i<5; i++) {
//    		distSP.fetchSample(distSample, 0);
//    		sum += distSample[0];
//    	}
//    	distSample[0] = sum/5;
//        return distSample[0];
//    }
    
    
    //method to move the pilot
    public MovePilot getPilot() {
        return pilot;
    }
 
    //-------------------------------------------------------
 
    //--------------------------MAPPING----------------------
 
    //Here the two arrays used for mapping are set up according to the sizes required for the area being covered
    public void buildArrays() {
        map = new int[6][5];
        dataFound = new int[6][5];
        for(int a=0; a<6; a++) {
            for(int b=0; b<5; b++) {
                map[a][b] = 0;
                dataFound[a][b] = 0;
            }
        }
    }
    
   
        
        
        //---------------------------------------------------------------------------------------------------
        
       
        
        
 
      
    //This method uses the dataFound array to update the map array. If a cell in data found contains an integer greater than 0 then the
    //average of the sensor readings taken so far show an obstacle in that cell and so that particular cell value in the map array will be
    //set to 1 to reflect this. If the cell in dataFound contains an integer less than 0 then the average sensor readings show that there
    //is no obstacle in that cell and so the relevant cell in the map array will be set to 0 to reflect this.
    //The only exception to this is if the cell on the map array has already been set to 2 which indicates that that is the goal cell
     
     
   /* public void updateMap() {
    	
//        //update data is called so that that the readings can then be updated onto the map.
       
        for(int a=0; a<6; a++) {
            for(int b=0; b<5; b++) {
                if (dataFound[a][b]>0 && map[a][b]!=2) {
                    map[a][b] = 1;
                } else if (dataFound[a][b]<0 && map[a][b]!=2) {
                    map[a][b] = 0;
                }
            }
        }
    }*/
 
    //-----------------------MAPPING DISPLAYING GRID----------------------------------------
    //this method displays the grid on the screen of the robot brick
     public void displayGrid() {

         GraphicsLCD lcd = LocalEV3.get().getGraphicsLCD();
 
         for(int a=0; a<6; a++) {
	        for(int b=0; b<5; b++) {
	            if (map[a][b] == 1) {
	                lcd.fillRect(10*a, 10*b, 10, 10);
	            } else if (map[a][b] == 0) {
	                lcd.drawRect(10*a, 10*b, 10, 10);
	            }
	        }
	    }
     }
}