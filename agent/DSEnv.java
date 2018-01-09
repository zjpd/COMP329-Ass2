// Environment code for project comp329DS.mas2j


import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.*;



public class DSEnv extends Environment {

	private DSModel model;
	private DSView view;
	private DSComm communicate;				//The wifi communication class between the agent and the robot
	private Thread comThread;				//The communication is implemented by the thread
	private Map pathMap;					//Map information for the path finding
	
	public static int locationRound = 0;	//The times that the localization 
	public static String HEADING = " ";
	public static mapGridDis[][] GridDistance = new mapGridDis[6][6];
	public static int[][] mapInform = new int[8][8];
	public static Location now;
	public static ArrayList<VictimInform> victims = new ArrayList<VictimInform>();
	public static ArrayList<ArrayList<Location>> scannedLocations = new ArrayList<ArrayList<Location>>();
	public static double[] distance = new double[4];
	//public static PathFinder pathfinder = new PathFinder();
	
	public static final int GWIDTH = 8;
	public static final int GLENGTH = 8;
	public static final int Obstacle = 4;
	public static final int unknownvictim = 8;
	public static final int redvictim = 16;
	public static final int bluevictim = 32;
	public static final int greenvictim = 64;
	public static final int WIDTH = 26;	//10 inches 25.4
	public static final int LENGTH = 34;	//12 -- 30.48 cm in length rectangle-34  square-26
	public static final Term LOCATION = Literal.parseLiteral("location");
	public static final int CENTER_TO_EDGE_WIDTH = 14;
	public static final int CENTER_TO_EDGE_LENGTH = 17;		//rectangle-17 square-14
	public static final int ZERO_DEVIATION = 13;			//rectangle-13 square-8
	
	public static final int[][] rightRotate = new int[][]{{0, -1}, {1, 0}};
	public static final int[][] leftRotate = new int[][]{{0, 1}, {-1, 0}};
	public static final int[] northHeading = new int[]{0, 1};
	public static final int[] southHeading = new int[]{0, -1};
	public static final int[] eastHeading = new int[]{1, 0};
	public static final int[] westHeading = new int[]{-1, 0};
	
    private Logger logger = Logger.getLogger("comp329DS.mas2j."+DSEnv.class.getName());

    /** Called before the MAS execution with the args informed in .mas2j */

    @Override

    public void init(String[] args) {

        super.init(args);

        //addPercept(ASSyntax.parseLiteral("percept(demo)"));
        for(int i=0; i<6; i++) {
        	for(int j=0; j<6; j++) {
        		GridDistance[i][j] = new mapGridDis();
        	}
        }
        updateMapInform();
        updateGridDistance();
        updateVictims();
        pathMap = new Map(mapInform);
		model = new DSModel();
		view = new DSView(model);
		model.setView(view);
		communicate = new DSComm();
		communicate.startup();
		comThread = new Thread(communicate);
		comThread.start();
    }



    @Override

    public boolean executeAction(String agName, Structure action) {

        logger.info("executing: "+action+", but not implemented!");
        if (action.equals(LOCATION)) { 
             try {
            	now = getLocation();
            	findVictim();
            	System.out.println("End!");
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return true; // the action was executed with success
    }



    /** Called before the end of MAS execution */

    @Override

    public void stop() {

        super.stop();

    }
    
    /**
     * Add the locations of the victims into the array.
     */
    public void updateVictims() {
    	victims.add(new VictimInform(new Location(1, 1), "unknown"));
    	victims.add(new VictimInform(new Location(1, 5), "unknown"));
    	victims.add(new VictimInform(new Location(3, 3), "unknown"));
    	victims.add(new VictimInform(new Location(4, 4), "unknown"));
    	victims.add(new VictimInform(new Location(5, 3), "unknown"));
    }
    
    /**
     * load the map information into an array
     */
    public void updateMapInform() {
    	int[] x1 = new int[]{1,1,1,1,1,1,1,1};
    	int[] x2 = new int[]{1,0,0,0,0,0,0,1};
    	int[] x3 = new int[]{1,0,0,1,0,1,0,1};
    	int[] x4 = new int[]{1,0,1,0,0,0,0,1};
    	int[] x5 = new int[]{1,0,0,0,0,0,0,1};
    	int[] x6 = new int[]{1,0,0,0,0,1,1,1};
    	int[] x7 = new int[]{1,1,0,0,0,0,0,1};
    	int[] x8 = new int[]{1,1,1,1,1,1,1,1};
    	mapInform[0] = x1;
    	mapInform[1] = x2;
    	mapInform[2] = x3;
    	mapInform[3] = x4;
    	mapInform[4] = x5;
    	mapInform[5] = x6;
    	mapInform[6] = x7;
    	mapInform[7] = x8;
    	
    	for(int i=0; i<8; i++) {
    		for(int j=0; j<8; j++) {
    			System.out.print(mapInform[i][j]+" ");
    		}
    		System.out.println();
    	}
    }
    
    /**
     * Record the around distance of each grid
     */
    public void updateGridDistance() {
    	
    	for(int i=0; i<6; i++) {
    		for(int j=0; j<6; j++) {
    			GridDistance[i][j].setNorthDistance(calculateNorth(i, j));
    			GridDistance[i][j].setWestDistance(calculateWest(i, j));
    			GridDistance[i][j].setSouthDistance(calculateSouth(i, j));
    			GridDistance[i][j].setEastDistance(calculateEast(i, j));
    		}
    	
    	}
    }
    
    /**
     * Calculate the distance between the pointed grid and the obstacle which the direction is north
     * @param x
     * @param y
     * @return distance
     */
    public double calculateNorth(int x, int y) {
    	int num = 0;
    	y++;
    	for(int i=x; i>=0; i--) {
    		if(mapInform[i][y] != 1){
    			num++;
    		}else{
    			break;
    		}
    	}
    	//System.out.println(num);
    	if(num>2)
    		return 2*LENGTH;
    	else
    		return num*LENGTH;
    }
    
    /**
     * Calculate the distance between the pointed grid and the obstacle which the direction is east
     * @param x
     * @param y
     * @return distance
     */
    public double calculateEast(int x, int y) {
    	int num = 0;
    	x++;
    	y += 2;
    	for(int i=y; i<7; i++) {
    		if(mapInform[x][i] != 1){
    			num++;
    		}else{
    			break;
    		}
    	}
    	if(num>2)
    		return 2*WIDTH;
    	else
    		return num*WIDTH;
    }
    
    /**
     * Calculate the distance between the pointed grid and the obstacle which the direction is south
     * @param x
     * @param y
     * @return distance
     */
    public double calculateSouth(int x, int y) {
    	int num = 0;
    	y++;
    	x += 2;
    	for(int i=x; i<7; i++) {
    		if(mapInform[i][y] != 1){
    			num++;
    		}else{
    			break;
    		}
    	}
    	if(num>2)
    		return 2*LENGTH;
    	else
    		return num*LENGTH;
    }
    
    /**
     * Calculate the distance between the pointed grid and the obstacle which the direction is east
     * @param x
     * @param y
     * @return
     */
    public double calculateWest(int x, int y) {
    	int num = 0;
    	x++;
    	for(int i=y; i>=0; i--) {
    		if(mapInform[x][i] != 1){
    			num++;
    		}else{
    			break;
    		}
    	}
    	if(num>2)
    		return 2*WIDTH;
    	else
    		return num*WIDTH;
    }
    
    /**
     * Calculate the heading of the robot by using the matrix multiplication.
     * The heading is represented as the vector matrix
     * @param matrix
     * @return
     */
    public int[] matrixMultiply(int[] matrix) {
    	int tmp[] = new int[2];
    	tmp[0] = rightRotate[0][0] * matrix[0] + rightRotate[0][1] * matrix[1];
    	tmp[1] = rightRotate[1][0] * matrix[0] + rightRotate[1][1] * matrix[1];
    	System.out.println("robot turned");
    	return tmp;
    }
    
    /**
     * Turn left represented by the matrix multiplication
     * @param times how many times does the robot turn left
     */
    public void matrixLeftRotate(int times) {
    	System.out.println("The left rotate times is "+times);
    	if(times==0)
    		return;
    	int[] tmp = new int[2];
    	
    	if(HEADING.equals("NORTH")) {
    		tmp[0] = leftRotate[0][0] * northHeading[0] + leftRotate[1][0] * northHeading[1];
    		tmp[1] = leftRotate[0][1] * northHeading[0] + leftRotate[1][1] * northHeading[1];
    		
    	} else if(HEADING.equals("EAST")) {
    		tmp[0] = leftRotate[0][0] * eastHeading[0] + leftRotate[1][0] * eastHeading[1];
    		tmp[1] = leftRotate[0][1] * eastHeading[0] + leftRotate[1][1] * eastHeading[1];
    	
    	} else if(HEADING.equals("SOUTH")) {
    		tmp[0] = leftRotate[0][0] * southHeading[0] + leftRotate[1][0] * southHeading[1];
    		tmp[1] = leftRotate[0][1] * southHeading[0] + leftRotate[1][1] * southHeading[1];
    	
    	} else {
    		tmp[0] = leftRotate[0][0] * westHeading[0] + leftRotate[1][0] * westHeading[1];
    		tmp[1] = leftRotate[0][1] * westHeading[0] + leftRotate[1][1] * westHeading[1];
    	
    	}
    	
    	times --;
		for(int i=0; i<times; i++) {
			System.out.println("matrix:"+tmp[0]+" "+tmp[1]);
			ArrayList<Integer> tmp2 = new ArrayList<Integer>();
			tmp2.add(tmp[0]);
			tmp2.add(tmp[1]);
			System.out.println("tmp matrix: "+tmp2.get(0)+" "+tmp2.get(1));
			tmp[0] = leftRotate[0][0] * tmp2.get(0) + leftRotate[1][0] * tmp2.get(1);
    		tmp[1] = leftRotate[0][1] * tmp2.get(0) + leftRotate[1][1] * tmp2.get(1);
		}
		System.out.println("matrix:"+tmp[0]+" "+tmp[1]);
		//System.out.println("tmp matrix: "+tmp2[0]+" "+tmp2[1]);
		if(Arrays.equals(tmp, northHeading))
    		HEADING = "NORTH";
    	else if(Arrays.equals(tmp, southHeading))
    		HEADING = "SOUTH";
    	else if(Arrays.equals(tmp, westHeading))
    		HEADING = "WEST";
    	else
    		HEADING = "EAST";
    }
    
    /**
     * Turn right represented by the matrix multiplication
     * @param times how many times does the robot turn right
     */
    public void matrixRightRotate(int times) {
    	System.out.println("The right rotate times is "+times);
    	if(times==0)
    		return;
    	int[] tmp = new int[2];
    	
    	if(HEADING.equals("NORTH")) {
    		tmp[0] = rightRotate[0][0] * northHeading[0] + rightRotate[1][0] * northHeading[1];
    		tmp[1] = rightRotate[0][1] * northHeading[0] + rightRotate[1][1] * northHeading[1];
    		
    	} else if(HEADING.equals("EAST")) {
    		tmp[0] = rightRotate[0][0] * eastHeading[0] + rightRotate[1][0] * eastHeading[1];
    		tmp[1] = rightRotate[0][1] * eastHeading[0] + rightRotate[1][1] * eastHeading[1];
    	
    	} else if(HEADING.equals("SOUTH")) {
    		tmp[0] = rightRotate[0][0] * southHeading[0] + rightRotate[1][0] * southHeading[1];
    		tmp[1] = rightRotate[0][1] * southHeading[0] + rightRotate[1][1] * southHeading[1];
    	
    	} else {
    		tmp[0] = rightRotate[0][0] * westHeading[0] + rightRotate[1][0] * westHeading[1];
    		tmp[1] = rightRotate[0][1] * westHeading[0] + rightRotate[1][1] * westHeading[1];
    	
    	}
    	
    	times --;
		for(int i=0; i<times; i++) {
			System.out.println("matrix:"+tmp[0]+" "+tmp[1]);
			ArrayList<Integer> tmp2 = new ArrayList<Integer>();
			tmp2.add(tmp[0]);
			tmp2.add(tmp[1]);
			System.out.println("tmp matrix: "+tmp2.get(0)+" "+tmp2.get(1));
			tmp[0] = rightRotate[0][0] * tmp2.get(0) + rightRotate[1][0] * tmp2.get(1);
    		tmp[1] = rightRotate[0][1] * tmp2.get(0) + rightRotate[1][1] * tmp2.get(1);
		}
		System.out.println("matrix:"+tmp[0]+" "+tmp[1]);
		if(Arrays.equals(tmp, northHeading))
    		HEADING = "NORTH";
    	else if(Arrays.equals(tmp, southHeading))
    		HEADING = "SOUTH";
    	else if(Arrays.equals(tmp, westHeading))
    		HEADING = "WEST";
    	else
    		HEADING = "EAST";
    }
    
    /**
     * Get the heading direction by using the previous location and the current location
     * @param previous
     * @param now
     * @param direction how many times does the robot turned before
     * @return
     */
    public String getHeading(Location previous, Location now, int direction) {
    	int[] resultHeading = new int[2];
    	if(direction != 0) {
        	if(previous.x-now.x == 1) {
        		for(int i=0; i<direction; i++) {
        			resultHeading = matrixMultiply(southHeading);
        		}
        	} else if(previous.x-now.x == -1) {
        		for(int i=0; i<direction; i++) {
        			resultHeading = matrixMultiply(northHeading);
        		}
        	} else if(previous.y-now.y == 1) {
        		for(int i=0; i<direction; i++) {
        			resultHeading = matrixMultiply(westHeading);
        		}
        	} else {
        		for(int i=0; i<direction; i++) {
        			resultHeading = matrixMultiply(eastHeading);
        		}
        	}
        	if(Arrays.equals(resultHeading, northHeading))
        		return "NORTH";
        	else if(Arrays.equals(resultHeading, southHeading))
        		return "SOUTH";
        	else if(Arrays.equals(resultHeading, westHeading))
        		return "WEST";
        	else
        		return "EAST";
    	} else {
    		if(previous.x-now.x == 1)
    			return "NORTH";
    		else if(previous.x-now.x == -1)
    			return "SOUTH";
    		else if(previous.y-now.y == 1)
    			return "WEST";
    		else
    			return "EAST";
    	}

    }
    
    /**
     * Get the heading direction
     * @param direction
     */
    public void getHeading(int direction) {
    	Location previous;
    	Location current;
    
    	previous = new Location(scannedLocations.get(0).get(scannedLocations.get(0).size()-2).x, scannedLocations.get(0).get(scannedLocations.get(0).size()-2).y);
        current = new Location(scannedLocations.get(0).get(scannedLocations.get(0).size()-1).x, scannedLocations.get(0).get(scannedLocations.get(0).size()-1).y);
    	
    	
    	System.out.println("Previous loation "+previous+", current location "+current);
    	HEADING = getHeading(previous, current, direction);
    }
    
    /**
     * Compare the detected distance with the stored distance information
     * @param distance
     * @param x
     * @param y
     * @return location
     */
    public Location compareLocation(double[] distance, int x, int y) {
    	Location returnLocation = new Location(x, y);
    	for(int i=0; i<distance.length; i++) {
    		if(distance[i] == GridDistance[x][y].getNorthDistance())
    			continue;
    		else if(distance[i] == GridDistance[x][y].getEastDistance())
    			continue;
    		else if(distance[i] == GridDistance[x][y].getSouthDistance())
    			continue;
    		else if(distance[i] == GridDistance[x][y].getWestDistance())
    			continue;
    		else
    			return null;
    	}
    	return returnLocation;
    }
    
    /**
     * Reduce the similar locations stored in the array list
     * @param direction
     */
    public void reduceLocations(int direction) {
    	if(locationRound == 1){			//if it is the first time of the relevant location reduction, every data point should be compared with the other data point
    		ArrayList<ArrayList<Location>> tmpscanned = new ArrayList<ArrayList<Location>>();
    		for(int i=0; i<scannedLocations.get(scannedLocations.size()-2).size(); i++) {
        		for(int j=0; j<scannedLocations.get(scannedLocations.size()-1).size(); j++) {
        			if(Math.abs(scannedLocations.get(scannedLocations.size()-1).get(j).x - scannedLocations.get(scannedLocations.size()-2).get(i).x)==1 &&
        					scannedLocations.get(scannedLocations.size()-1).get(j).y == scannedLocations.get(scannedLocations.size()-2).get(i).y) {
        				ArrayList<Location> tmp = new ArrayList<Location>();
        				tmp.add(scannedLocations.get(scannedLocations.size()-2).get(i));
        				tmp.add(scannedLocations.get(scannedLocations.size()-1).get(j));
        				System.out.println("The relevant location "+scannedLocations.get(scannedLocations.size()-2).get(i)+"\t"+scannedLocations.get(scannedLocations.size()-1).get(j));
        				tmpscanned.add(tmp);
        			} else if(Math.abs(scannedLocations.get(scannedLocations.size()-1).get(j).y - scannedLocations.get(scannedLocations.size()-2).get(i).y)==1 &&
        					scannedLocations.get(scannedLocations.size()-1).get(j).x == scannedLocations.get(scannedLocations.size()-2).get(i).x) {
        				ArrayList<Location> tmp = new ArrayList<Location>();
        				tmp.add(scannedLocations.get(scannedLocations.size()-2).get(i));
        				tmp.add(scannedLocations.get(scannedLocations.size()-1).get(j));
        				System.out.println("The relevant location "+scannedLocations.get(scannedLocations.size()-2).get(i)+"\t"+scannedLocations.get(scannedLocations.size()-1).get(j));
        				tmpscanned.add(tmp);
        			}
        		}
        	}
    		
    		System.out.println("The tmp size "+tmpscanned.size());
    		scannedLocations = tmpscanned;
    		System.out.println("The scan size "+scannedLocations.size());
    	} else {
    		System.out.println("location round: "+locationRound);
    		System.out.println("The size is: "+scannedLocations.get(scannedLocations.size()-1).size());
    		System.out.println("The scanned size is: "+scannedLocations.size());
    		for(int a=0; a<scannedLocations.get(scannedLocations.size()-1).size(); a++)
    			System.out.println(scannedLocations.get(scannedLocations.size()-1).get(a).x+"\t"+scannedLocations.get(scannedLocations.size()-1).get(a).y);
    			
//    		ArrayList<ArrayList<Location>> tmplocations = new ArrayList<ArrayList<Location>>();
    		int index = -1;
    		for(int i=0; i<scannedLocations.get(scannedLocations.size()-1).size(); i++) {
    			for(int j=0; j<scannedLocations.size()-1; j++) {
    				
    				if(index == j) {
    					System.out.println("Duplicate location found!!!");

    					if(Math.abs(scannedLocations.get(scannedLocations.size()-1).get(i).x - scannedLocations.get(j).get(scannedLocations.get(j).size()-2).x) == 1 &&
        						scannedLocations.get(scannedLocations.size()-1).get(i).y == scannedLocations.get(j).get(scannedLocations.get(j).size()-2).y) {
        					System.out.println("x greater");
        					
        					for(int k=0; k<scannedLocations.get(j).size()-1; k++) {
        						System.out.println("The compared size "+scannedLocations.get(j).size());
        						if(!scannedLocations.get(j).get(k).equals(scannedLocations.get(scannedLocations.size()-1).get(i))) {
        							
        							if((scannedLocations.get(j).size()-1) == (locationRound+1)) 
        								break;
        							
//        							ArrayList<Location> tmpLo = scannedLocations.get(j);
//        							tmpLo.add(scannedLocations.get(scannedLocations.size()-1).get(i));
//        							tmplocations.add(tmpLo);
//        							System.out.println("The size of tmp "+tmpLo.size());
        							
        							scannedLocations.get(j).add(scannedLocations.get(scannedLocations.size()-1).get(i));
        							
        							ArrayList<Location> tmplist = scannedLocations.get(j);
        							tmplist.set(tmplist.size()-1, scannedLocations.get(scannedLocations.size()-1).get(i));
        							scannedLocations.add((j+1), tmplist);
        							
        							System.out.println("The first location: "+scannedLocations.get(j+1).get(k)+" The detected: "+scannedLocations.get(scannedLocations.size()-1).get(i));
        							index = j;
        						} else {
        							System.out.println("equal location "+scannedLocations.get(j).get(k)+" The detected: "+scannedLocations.get(scannedLocations.size()-1).get(i));
        							scannedLocations.get(j).remove(scannedLocations.get(j).size()-1);
        							break;
        						}
        					}
    					
    					}  else if(Math.abs(scannedLocations.get(scannedLocations.size()-1).get(i).y - scannedLocations.get(j).get(scannedLocations.get(j).size()-2).y) == 1 &&
        						scannedLocations.get(scannedLocations.size()-1).get(i).x == scannedLocations.get(j).get(scannedLocations.get(j).size()-2).x) {
        					System.out.println("y greater");
        					for(int k=0; k<scannedLocations.get(j).size(); k++) {
        						if(!scannedLocations.get(j).get(k).equals(scannedLocations.get(scannedLocations.size()-1).get(i))) {
        							
        							if(scannedLocations.get(j).size() == (locationRound+1))
        								break;
        							
//        							ArrayList<Location> tmpLo = scannedLocations.get(j);
//        							tmpLo.add(scannedLocations.get(scannedLocations.size()-1).get(i));
//        							tmplocations.add(tmpLo);
//        							System.out.println("The size of tmp "+tmpLo.size());
        							
        							scannedLocations.get(j).add(scannedLocations.get(scannedLocations.size()-1).get(i));
        							ArrayList<Location> tmplist = scannedLocations.get(j);
        							tmplist.set(tmplist.size()-1, scannedLocations.get(scannedLocations.size()-1).get(i));
        							scannedLocations.add((j+1), tmplist);
        							index = j;
        							System.out.println("The first location: "+scannedLocations.get(j+1).get(k)+" The detected: "+scannedLocations.get(scannedLocations.size()-1).get(i));
        						} else {
        							System.out.println("equal location "+scannedLocations.get(j).get(k)+" The detected: "+scannedLocations.get(scannedLocations.size()-1).get(i));
        							scannedLocations.get(j).remove(scannedLocations.get(j).size()-1);
        						//	tmplocations.get(tmplocations.size()-1).remove(tmplocations.get(tmplocations.size()-1).size()-1);
        							break;
        						}
        					}
    					}
        					
    				} else {
    					
    					
    					if(Math.abs(scannedLocations.get(scannedLocations.size()-1).get(i).x - scannedLocations.get(j).get(scannedLocations.get(j).size()-1).x) == 1 &&
        						scannedLocations.get(scannedLocations.size()-1).get(i).y == scannedLocations.get(j).get(scannedLocations.get(j).size()-1).y) {
        					System.out.println("x greater");
        					
        					for(int k=0; k<scannedLocations.get(j).size(); k++) {
        						System.out.println("The compared size "+scannedLocations.get(j).size());
        						if(!scannedLocations.get(j).get(k).equals(scannedLocations.get(scannedLocations.size()-1).get(i))) {
        							
        							if(scannedLocations.get(j).size() == (locationRound+1))
        								break;
        								
        							
//        							ArrayList<Location> tmpLo = scannedLocations.get(j);
//        							tmpLo.add(scannedLocations.get(scannedLocations.size()-1).get(i));
//        							tmplocations.add(tmpLo);
//        							System.out.println("The size of tmp "+tmpLo.size());
        							
        							System.out.println("The first location: "+scannedLocations.get(j).get(k)+" The detected: "+scannedLocations.get(scannedLocations.size()-1).get(i));
        							scannedLocations.get(j).add(scannedLocations.get(scannedLocations.size()-1).get(i));
        							index = j;
        						} else {
        							System.out.println("equal location "+scannedLocations.get(j).get(k)+" The detected: "+scannedLocations.get(scannedLocations.size()-1).get(i));
        							scannedLocations.get(j).remove(scannedLocations.get(j).size()-1);
        							break;
        						}
        					}
        					
        				} else if(Math.abs(scannedLocations.get(scannedLocations.size()-1).get(i).y - scannedLocations.get(j).get(scannedLocations.get(j).size()-1).y) == 1 &&
        						scannedLocations.get(scannedLocations.size()-1).get(i).x == scannedLocations.get(j).get(scannedLocations.get(j).size()-1).x) {
        					System.out.println("y greater");
        					for(int k=0; k<scannedLocations.get(j).size(); k++) {
        						if(!scannedLocations.get(j).get(k).equals(scannedLocations.get(scannedLocations.size()-1).get(i))) {
        							
        							if(scannedLocations.get(j).size() == (locationRound+1))
        								break;
        							
//        							ArrayList<Location> tmpLo = scannedLocations.get(j);
//        							tmpLo.add(scannedLocations.get(scannedLocations.size()-1).get(i));
//        							tmplocations.add(tmpLo);
//        							System.out.println("The size of tmp "+tmpLo.size());
        							
        							scannedLocations.get(j).add(scannedLocations.get(scannedLocations.size()-1).get(i));
        							System.out.println("The first location: "+scannedLocations.get(j).get(k)+" The detected: "+scannedLocations.get(scannedLocations.size()-1).get(i));
        						} else {
        							System.out.println("equal location "+scannedLocations.get(j).get(k)+" The detected: "+scannedLocations.get(scannedLocations.size()-1).get(i));
        							scannedLocations.get(j).remove(scannedLocations.get(j).size()-1);
        						//	tmplocations.get(tmplocations.size()-1).remove(tmplocations.get(tmplocations.size()-1).size()-1);
        							break;
        						}
        					}
        				}
    				}
    			}
    		}
    	//	scannedLocations = null;
    	//	scannedLocations = tmplocations;
    		scannedLocations.remove(scannedLocations.size()-1);
    		System.out.println("The scanned size "+scannedLocations.size());
    		
//    		for(int i=0; i<scannedLocations.size(); i++) {
//        		
//    			if(scannedLocations.get(i).get(scannedLocations.get(i).size()-1).equals(scannedLocations.get(i).get(scannedLocations.get(i).size()-2)))
//    				scannedLocations.get(i).remove(scannedLocations.get(i).size()-1);
//				
//			}
    		
    		
    		for(int i=0; i<scannedLocations.size(); i++) {
    		
    			System.out.println("reach location size "+scannedLocations.get(i).size());
				
    			for(int j=0; j<scannedLocations.get(i).size(); j++) {
				
    				System.out.println("Stored detected locations "+scannedLocations.get(i).get(j));
				
    			}
			}
    		
    		Iterator<ArrayList<Location>> iter = scannedLocations.iterator();
    		
    		while(iter.hasNext()) {
    			if(iter.next().size() != (locationRound+1))
    				iter.remove();
    		}
    		
    		boolean locationFind = true;
    		Location label = scannedLocations.get(0).get(scannedLocations.get(0).size()-1);
    		for(int i=1; i<scannedLocations.size(); i++) {
    			
    			if(!label.equals(scannedLocations.get(i).get(scannedLocations.get(i).size()-1)))
    				locationFind = false;
    		}
    		
    		if(locationFind)
    			getHeading(direction);
    	}
    	System.out.println("The direction times is "+direction+" "+locationRound+" "+scannedLocations.size());
    	if(scannedLocations.size()==1)
    		getHeading(direction);

    }
    
    /**
     * To get the accurate location of the robot by moving the robot into another grid.
     * @throws IOException
     * @throws InterruptedException
     */
    public void getAccurateLocation() throws IOException, InterruptedException {
    
    	int direction = 0;
    	locationRound ++;			//to store the scanned times
    	boolean isLength = false;
    	
    	/* To identify whether the scanned distance is more than 12 which is one cell near the obstacle or the wall.
    	 * If it isn't, then the robot need to rotate to the distance and move forward for next distance detection*/
    	for(int i=0; i<distance.length; i++) {
    		System.out.println("The distance "+i+" is "+distance[i]);
    		if(distance[i] > 12) {
    			System.out.println("The distance "+i+" is "+distance[i]);
    			direction = i;
    			break;
    		}
    	}
    	System.out.println("The i is set as" + direction);
    	for(int i=0; i<direction; i++) {
    		DSComm.sendMessage("left");
    	}
    	
    	/* To identify the direction of the robot.*/
    	isLength = lengthOrWidth();
    	if(isLength) {
    		DSComm.sendMessage("FORWARD");
    		DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_LENGTH));
    	} else {
    		DSComm.sendMessage("FORWARD");
    		DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_WIDTH-1));
    	}
    	for(int i=0; i<direction; i++) {
    		DSComm.sendMessage("RIGHT");
    	}
    	
    	/*Scan again*/
    	distance = getAroundDistance();
    	ArrayList<Location> tmplist = new ArrayList<Location>();
    	for(int i=0; i<6; i++) {
			for(int j=0; j<6; j++) {
				Location tmp = comparingLocation(distance, i, j);
				if(tmp != null) {
					tmplist.add(tmp);
					System.out.println("The detected location is "+tmp.x+", "+tmp.y);
				}
			}
		}
    	scannedLocations.add(tmplist);
    	reduceLocations(direction);			//to get the relevant location
    	System.out.println("The heading is: "+HEADING);
    }
    
    /**
     * To identify whether the direction of the robot is facing to the length or the width of the grid
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean lengthOrWidth() throws IOException, InterruptedException {
    	DSComm.sendMessage("FORWARD");
    	DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_WIDTH));
    	DSComm.sendMessage("COLOR");
    	String color = DSComm.readMessage();
    	while(color.equals("NoMessage")) 
    		color = DSComm.readMessage();
    	if(color.equals("BLACK"))
    		return false;
    	else {
    		//DSComm.sendMessage("BACK");
    		//DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_WIDTH));
    		//DSComm.sendMessage("left");
    		//DSComm.sendMessage("left");
    		DSComm.sendMessage("FORWARD");
    		DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_LENGTH-CENTER_TO_EDGE_WIDTH));
    		return true;
    	}
    }
    
    /**
     * Compare the detected distance with the stored distance information
     * @param distance
     * @param x
     * @param y
     * @return
     */
    public Location comparingLocation(double[] distance, int x, int y) {
    	Location returnLocation = new Location(x, y);
    	int[] tmpdis = new int[4];
    	tmpdis[0] = (int)Math.floor(distance[0]);
    	tmpdis[1] = (int)Math.floor(distance[1]);
    	tmpdis[2] = (int)Math.floor(distance[2]);
    	tmpdis[3] = (int)Math.floor(distance[3]);
    	Arrays.sort(tmpdis);
    	
    	double tmp[] = new double[4];
    	tmp[0] = GridDistance[x][y].getNorthDistance();
    	tmp[1] = GridDistance[x][y].getWestDistance();
    	tmp[2] = GridDistance[x][y].getSouthDistance();
    	tmp[3] = GridDistance[x][y].getEastDistance();
    	Arrays.sort(tmp);
    	System.out.println(tmp[0] +" "+tmp[1]+" "+tmp[2]+" "+tmp[3]+"\t"+tmpdis[0]+" "+tmpdis[1]+" "+tmpdis[2]+" "+tmpdis[3]);
    	
    	if(tmp[0]<2*WIDTH)
    		if(tmp[0]-ZERO_DEVIATION >= tmpdis[0] || tmp[0]+ZERO_DEVIATION <= tmpdis[0])
    			return null;
    	else
    		if(tmp[0]-ZERO_DEVIATION >= tmpdis[0])
    			return null;
    	
    	if(tmp[1]<2*WIDTH)
    		if(tmp[1]-ZERO_DEVIATION >= tmpdis[1] || tmp[1]+ZERO_DEVIATION <= tmpdis[1])
    			return null;
    	else
    		if(tmp[1]-ZERO_DEVIATION >= tmpdis[1])
    			return null;
    	
    	if(tmp[2]<2*WIDTH)
    		if((tmp[2]-ZERO_DEVIATION) >= tmpdis[2] || (tmp[2]+ZERO_DEVIATION) <= tmpdis[2])
    			return null;
    	else
    		if(tmp[2]-ZERO_DEVIATION >= tmpdis[2])
    			return null;
    	
    	if(tmp[3]<2*WIDTH)
    		if(tmp[3]-ZERO_DEVIATION >= tmpdis[3] || tmp[3]+ZERO_DEVIATION <= tmpdis[3])
    			return null;
    	else
    		if(tmp[3]-ZERO_DEVIATION >= tmpdis[3])
    			return null;
    		
    	return returnLocation;
    }
    
    /**
     * GEt the around distance information by using the ultrasonic sensor on the robot
     * @return
     */
    public double[] getAroundDistance() {
    	/* Scan for the distance to the obstacle or wall of the heading direction */
    	try {
    		for(int i=0; i<3; i++) 
    			DSComm.sendMessage("DISTANCE");

			/* Turn left and scan for the distance of heading direction */
			DSComm.sendMessage("left");
	    	for(int i=0; i<3; i++) 
	    		DSComm.sendMessage("DISTANCE");
				
			/* Turn left and scan for the distance of heading direction */
			DSComm.sendMessage("left");
		    for(int i=0; i<3; i++) 
		   		DSComm.sendMessage("DISTANCE");
			
			/* Turn left and scan for the distance of heading direction */
			DSComm.sendMessage("left");
	    	for(int i=0; i<3; i++) 
		   		DSComm.sendMessage("DISTANCE");
			DSComm.sendMessage("left");
			
			String message = " ";
			int num = 0;
			double[] tmp = new double[12];
			while(num < 12) {
				message = DSComm.readMessage();
				if(message.equals("NoMessage"))
					continue;
				tmp[num] = Math.floor(Double.valueOf(message)*100);
				System.out.println("The received message is: "+tmp[num]);
				num ++;
			}
			distance[0] = (tmp[0]+tmp[1]+tmp[2])/3;
			distance[1] = (tmp[3]+tmp[4]+tmp[5])/3;
			distance[2] = (tmp[6]+tmp[7]+tmp[8])/3;
			distance[3] = (tmp[9]+tmp[10]+tmp[11])/3;
			System.out.println("The distance is "+distance[0]);
			System.out.println("The distance is "+distance[1]);
			System.out.println("The distance is "+distance[2]);
			System.out.println("The distance is "+distance[3]);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return distance;
    }
    
    /**
     * Get the location of the robot
     * @return
     * @throws InterruptedException
     */
    public Location getLocation() throws InterruptedException {
    
    	distance = getAroundDistance();	//get the scanned distance
    	double[] tmparray = distance;				//to store the scanned distance for sorting
    	
    	try{
			ArrayList<Location> locations = new ArrayList<Location>();
			for(int i=0; i<6; i++) {
				for(int j=0; j<6; j++) {
					Location tmp = comparingLocation(tmparray, i, j);
					if(tmp != null) {
						locations.add(tmp);
						System.out.println("The detected location is "+tmp.x+", "+tmp.y);
					}
				}
			}
			scannedLocations.add(locations);					//scannedlocations for storing scanned distance for localization
			//Thread.sleep(1000);
			System.out.println("distance[0] "+distance[0]);
			System.out.println("distance[1] "+distance[1]);
			System.out.println("distance[2] "+distance[2]);
			System.out.println("distance[3] "+distance[3]);
			
			System.out.println("The location size "+locations.size());
			
			/*while the location and the heading haven't been detected, one more scanning should be implemented */
			
			while(HEADING.equals(" ")) {
				getAccurateLocation();
			}
			
			
			/*Set the agent onto the map*/
			model.setAgent(scannedLocations.get(0).get(scannedLocations.get(0).size()-1).y+1,scannedLocations.get(0).get(scannedLocations.get(0).size()-1).x+1);
			return new Location(scannedLocations.get(0).get(scannedLocations.get(0).size()-1).x+1,scannedLocations.get(0).get(scannedLocations.get(0).size()-1).y+1);
			
				//getHeading(new Location(locations.get(0).x, locations.get(0).y), distance);
			
			//getAccurateLocation(distance, locations);
			//System.out.println("location finished");
		} catch (IOException e) {
			System.out.println("Fail in location part");
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * Scan the color of the grid. RGB mode has been applied.
     * @param index
     * @throws IOException
     * @throws InterruptedException
     */
    public void scanColor(int index) throws IOException, InterruptedException {
    	DSComm.sendMessage("COLOR");
    	String message = DSComm.readMessage();
    	
    	System.out.println("The received message is: "+message);
    	
    	while(message.equals("NoMessage"))
    		message = DSComm.readMessage();
    	
    	if(message.equals("RED")) {
    		victims.get(index).setType("RED");
    		System.out.println("The red victim has found at point "+now.x+","+now.y+"!");
    		model.setVictim(redvictim, now.y, now.x);
    	} else if(message.equals("GREEN")) {
    		victims.get(index).setType("GREEN");
    		System.out.println("The green victim has found at point "+now.x+","+now.y+"!");
    		model.setVictim(greenvictim, now.y, now.x);
    	} else if(message.equals("BLUE")) {
    		victims.get(index).setType("BLUE");
    		System.out.println("The blue victim has found at point "+now.x+","+now.y+"!");
    		model.setVictim(bluevictim, now.y, now.x);
    	} else if(message.equals("NoColor")) {
    		victims.get(index).setType("NoColor");
    		System.out.println("There is no color found at point "+now.x+","+now.y+"!");
    	}
    }
    
    /**
     * Navigation of the robot. move the robot to the next grid by the path which was supplied by the PathFinder class
     * @param path
     * @throws IOException
     */
    public void movePilot(ArrayList<Cell> path) throws IOException {
    	int currentHeading = 0;
    	int nextHeading = 0;
    	
    	for(int i=path.size()-2; i>=0 ; i--) {
    		System.out.println("The current heading is: "+HEADING );
        	if(HEADING.equals("NORTH"))
        		currentHeading = 0;
        	else if(HEADING.equals("WEST"))
        		currentHeading = 1;
        	else if(HEADING.equals("SOUTH"))
        		currentHeading = 2;
        	else
        		currentHeading = 3;
        	
    		Cell nextCell = path.get(i);
    		System.out.println("The next cell "+nextCell.getCellXPos()+", "+nextCell.getCellYPos());
    		System.out.println("The current cell "+now);
    		
    		if(now.x > nextCell.getCellXPos())
    			nextHeading = 0;
    		else if(now.y > nextCell.getCellYPos())
    			nextHeading = 1;
    		else if(now.x < nextCell.getCellXPos())
    			nextHeading = 2;
    		else
    			nextHeading = 3;
    		System.out.println("The current heading "+currentHeading+" and the next heading "+nextHeading);
    		int rotateTimes = nextHeading - currentHeading;
    		if(rotateTimes>=0)
    			matrixLeftRotate(rotateTimes);
    		else if(rotateTimes<0)
    			matrixRightRotate(Math.abs(rotateTimes));
    		
    		switch(rotateTimes) {
    			case 0:
    				break;
    			case 1:
    				DSComm.sendMessage("left");
    				break;
    			case 2:
    				DSComm.sendMessage("left");
    				DSComm.sendMessage("left");
    				break;
    			case 3:
    				DSComm.sendMessage("RIGHT");
    				break;
    			case -1:
    				DSComm.sendMessage("RIGHT");
    				break;
    			case -2:
    				DSComm.sendMessage("left");
    				DSComm.sendMessage("left");
    				break;
    			case -3:
    				DSComm.sendMessage("left");
    		}
    		
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
    		
    		if(now.x == (nextCell.getCellXPos())) {
    			DSComm.sendMessage("FORWARD");
    			DSComm.sendMessage(String.valueOf(WIDTH));
    		} else if(now.y == (nextCell.getCellYPos())) {
    			DSComm.sendMessage("FORWARD");
    			DSComm.sendMessage(String.valueOf(LENGTH));
    		}
    		now.x = nextCell.getCellXPos();
    		now.y = nextCell.getCellYPos();
    		model.setAgent(now.y, now.x);
    		try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Using A star algorithm to get the path which leads to the destination grid.
     * @throws IOException
     * @throws InterruptedException
     */
    public void findVictim() throws IOException, InterruptedException {
    	for(int i=0; i<victims.size(); i++) {
    		PathFinder pathfinder = new PathFinder();
    		
    		for(int q = 0; q<pathMap.map.length ; q++)
    		{
    			for(int j = 0; j<pathMap.map.length ; j++)
        		{
        			pathMap.map[q][j].previous = null;
        		}
    		
    		}
    		
    		
    		Cell startCell = pathMap.map[now.x][now.y];
    		
    		
    		Cell targetCell = pathMap.map[victims.get(i).getLocation().x][victims.get(i).getLocation().y];
    		
    		pathfinder.openSet.clear();
    		pathfinder.closedSet.clear();


    		pathfinder.openSet.add(startCell);
    		System.out.println("start location "+now);
    		System.out.println("target location "+victims.get(i).getLocation());
    	
    		
    		System.out.println("target  cell "+targetCell.getCellProbability());
        	ArrayList<Cell> path = pathfinder.findPath(pathMap.map, targetCell, startCell);
        	
  
        	if(path == null) {
        		scanColor(i);
        	} else {
        		movePilot(path);
        		System.out.println(path.size());
        		scanColor(i);
        	}
    	}
    }
    
    public String getCurrentGridVictim() {
		for(int i=0; i<victims.size(); i++) {
			if(now.x == victims.get(i).getLocation().x && now.y == victims.get(i).getLocation().y)
				return victims.get(i).getType();
		}
		return "NoType";
	}
    
    /**
     * Records victims information which includes the victims' type and the location of the victims
     * @author jin
     *
     */
	class VictimInform {
		
		private Location location;
		private String type;
		
		public VictimInform(Location location, String type) {
			this.location = location;
			this.type = type;
		}
		
		public void setLocation(Location location) { this.location = location;}
		public void setType(String type) {this.type = type;}
		
		public Location getLocation() {return location;}
		public String getType() {return type;}
	}
    
	class DSModel extends GridWorldModel {
		
		protected boolean findVictim = false;
		
		protected DSModel() {
			super(GWIDTH, GLENGTH, 2);
			
			add(Obstacle, 2, 3);
			add(Obstacle, 1, 6);
			add(Obstacle, 3, 2);
			add(Obstacle, 5, 2);
			add(Obstacle, 5, 5);
			add(Obstacle, 6, 5);
			add(unknownvictim, 1, 1);
			add(unknownvictim, 5, 1);
			add(unknownvictim, 3, 3);
			add(unknownvictim, 4, 4);
			add(unknownvictim, 3, 5);
			
			addWall(0, 0, 7, 0);
			addWall(0, 0, 0, 7);
			addWall(7, 0, 7, 7);
			addWall(0, 7, 7, 7);
			
			
		}
		
		protected void setAgent(int x, int y) {
			setAgPos(0, x, y);
		}
		
		protected void setVictim(int value, int x, int y) {
			add(value, x, y);
		}
	}
    
	class DSView extends GridWorldView {

		private static final long serialVersionUID = 1L;

		public DSView(DSModel model) {
            super(model, "DS World", 800);
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true);
            repaint();
        }
		
		public void draw(Graphics g, int x, int y, int object) {
			super.draw(g, x, y, object);
			switch (object) {
				case Obstacle:
					drawObstacle(g, x, y);
					break;
				case unknownvictim:
					drawUnknownVictim(g, x, y);
					break;
				case redvictim:
					drawRedVictim(g, x, y);
					break;
				case bluevictim:
					drawBlueVictim(g, x, y);
					break;
				case greenvictim:
					drawGreenVictim(g, x, y);
			}
		}
		
		
        @Override
        public void drawAgent(Graphics g, int x, int y, Color c, int id) {
            String label = "R"+(id+1);
            c = Color.blue;
            if (id == 0) {
                c = Color.yellow;
                if (((DSModel)model).findVictim) {
                    label += " - G";
                    c = Color.orange;
                }
            }
            super.drawAgent(g, x, y, c, -1);
            if (id == 0) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.white);                
            }
            super.drawString(g, x, y, defaultFont, label);
        }

        public void drawObstacle(Graphics g, int x, int y) {
            super.drawObstacle(g, x, y);
            g.setColor(Color.white);
            drawString(g, x, y, defaultFont, "Obstacle");
        }
		
        public void drawUnknownVictim(Graphics g, int x, int y) {
        	System.out.println("unknown");
        	g.setColor(Color.GRAY);
        	drawString(g, x, y, defaultFont, "unknown");
        }
        
        public void drawBlueVictim(Graphics g, int x, int y) {
        	System.out.println("blue");
        	g.setColor(Color.blue);
        	drawString(g, x, y, defaultFont, "blue");
        }
        
        public void drawRedVictim(Graphics g, int x, int y) {
        	System.out.println("red");
        	g.setColor(Color.red);
        	drawString(g, x, y, defaultFont, "red");
        }
        
        public void drawGreenVictim(Graphics g, int x, int y) {
        	System.out.println("green");
        	g.setColor(Color.green);
        	drawString(g, x, y, defaultFont, "green");
        }
        
        public void drawAgent(Graphics g, int x, int y, int id) {
        	switch (id){
        		case 1:	
        			super.drawAgent(g, x, y, Color.LIGHT_GRAY, 1);
        			drawString(g, x, y, defaultFont, "agent1");
        		break;
        		case 2: 
        			super.drawAgent(g, x, y, Color.BLACK, 2);
        			drawString(g, x, y, defaultFont, "agent2");
        		break;
        	}
        }
	}
	
	class mapGridDis {
		
		protected double[] gridDistance = new double[4];
		
		/**
		 * Set the distance for the grid which is from the obstacle or wall to the object
		 * @param dis distance
		 */
		public void setNorthDistance(double dis) {
			gridDistance[0] = dis;
		}
		public void setEastDistance(double dis) {
			gridDistance[1] = dis;
		}
		public void setSouthDistance(double dis) {
			gridDistance[2] = dis;
		}
		public void setWestDistance(double dis) {
			gridDistance[3] = dis;
		}
		public double getNorthDistance() {return gridDistance[0];}
		public double getEastDistance() {return gridDistance[1];}
		public double getSouthDistance() {return gridDistance[2];}
		public double getWestDistance() {return gridDistance[3];}
	}
}