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
import java.util.logging.*;



public class DSEnv extends Environment {

	private DSModel model;
	private DSView view;
	private DSComm communicate;
	private Thread comThread;
	
	public static String HEADING = " ";
	public static mapGridDis[][] GridDistance = new mapGridDis[6][6];
	public static int[][] mapInform = new int[8][8];
	public static Location now;
	public static ArrayList<VictimInform> victims = new ArrayList<VictimInform>();
			
	public static final int GWIDTH = 8;
	public static final int GLENGTH = 8;
	public static final int Obstacle = 7;
	public static final int victim = 8;
	public static final int WIDTH = 25;	//10 inches 25.4
	public static final int LENGTH = 30;	//12 -- 30.48 cm in length
	public static final Term LOCATION = Literal.parseLiteral("location");
	public static final int CENTER_TO_EDGE_WIDTH = 5;
	public static final int CENTER_TO_EDGE_LENGTH = 7;
	
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
        if (action.equals(LOCATION)) { // you may improve this condition
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
    
    public void updateVictims() {
    	victims.add(new VictimInform(new Location(0, 0), "unknown"));
    	victims.add(new VictimInform(new Location(0, 3), "unknown"));
    	victims.add(new VictimInform(new Location(2, 2), "unknown"));
    	victims.add(new VictimInform(new Location(3, 3), "unknown"));
    	victims.add(new VictimInform(new Location(4, 2), "unknown"));
    }
    
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
    
    public void updateGridDistance() {
    	for(int i=0; i<6; i++) {
    		for(int j=0; j<6; j++) {
    			GridDistance[i][j].setNorthDistance(calculateNorth(i, j));
    			GridDistance[i][j].setEastDistance(calculateEast(i, j));
    			GridDistance[i][j].setSouthDistance(calculateSouth(i, j));
    			GridDistance[i][j].setWestDistance(calculateWest(i, j));
    			//System.out.println("The distance of "+i+", "+j+"is: "+GridDistance[i][j].getWestDistance());
    		}
    	}
    }
    
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
    	return num*LENGTH;
    }
    
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
    	return num*WIDTH;
    }
    
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
    	return num*LENGTH;
    }
    
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
    	return num*WIDTH;
    }
    
    public String getHeading(Location previous, Location now) {
    	if(previous.x-now.x == 1)
    		return "SOUTH";
    	else if(previous.x-now.x == -1)
    		return "NORTH";
    	else if(previous.y-now.y == 1)
    		return "WEST";
    	else
    		return "EAST";
    }
    
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
    
    public ArrayList<Location> getAccurateLocation(double[] distance, ArrayList<Location> locations) throws IOException, InterruptedException {
    	int direction = 0;
    	boolean isLength = false;
    	ArrayList<Location> returnlist = new ArrayList<Location>();
    	for(int i=0; i<distance.length; i++) {
    		if(distance[i] > 0) {
    			direction = i;
    			break;
    		}
    	}
    	for(int i=0; i<direction; i++) {
    		DSComm.sendMessage("LEFT");
    	}
    	
    	isLength = lengthOrWidth();
    	if(isLength) {
    		DSComm.sendMessage("FORWARD");
    		DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_LENGTH));
    	} else {
    		DSComm.sendMessage("FORWARD");
    		DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_WIDTH));
    	}
    	
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
    	
    	Location previous = new Location(0,0);
    	for(int i=0; i<locations.size(); i++) {
    		for(int j=0; j<tmplist.size(); j++) {
    			if(Math.abs(tmplist.get(j).x-locations.get(i).x)==1 && tmplist.get(j).y-locations.get(i).y==0) { 
    				returnlist.add(tmplist.get(j));
    				previous = locations.get(i);
    			} else if (Math.abs(tmplist.get(j).y-locations.get(i).y)==1 && tmplist.get(j).x-locations.get(i).x==0) {
    				returnlist.add(tmplist.get(j));
    				previous = locations.get(i);
    			}
    		}
    	}
    	if(returnlist.size()==1)
    		HEADING = getHeading(previous, returnlist.get(0));
    	
    	return returnlist;
    }
    
    public boolean lengthOrWidth() throws IOException, InterruptedException {
    	DSComm.sendMessage("FORWARD");
    	DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_WIDTH));
    	String color = DSComm.readMessage();
    	while(color.equals("NoMessage")) 
    		color = DSComm.readMessage();
    	if(color.equals("BLACK"))
    		return false;
    	else {
    		DSComm.sendMessage("BACK");
    		DSComm.sendMessage(String.valueOf(CENTER_TO_EDGE_WIDTH));
    		return true;
    	}
    }
    
    
    public Location comparingLocation(double[] distance, int x, int y) {
    	Location returnLocation = new Location(x, y);
    	Arrays.sort(distance);
    	double tmp[] = new double[4];
    	tmp[0] = GridDistance[x][y].getNorthDistance();
    	tmp[1] = GridDistance[x][y].getEastDistance();
    	tmp[2] = GridDistance[x][y].getSouthDistance();
    	tmp[3] = GridDistance[x][y].getWestDistance();
    	Arrays.sort(tmp);
    	for(int i=0; i<4; i++) {
    		if(tmp[i] != distance[i])
    			return null;
    	}
    	return returnLocation;
    }
    
    public double[] getAroundDistance() {
    	double distance[] = new double[4];
    	/* Scan for the distance to the obstacle or wall of the heading direction */
    	try {
    		for(int i=0; i<3; i++) {
    			DSComm.sendMessage("SCAN");
    			String str = DSComm.readMessage();
    			
    			System.out.println("The received message is: "+str);
    			
    			if(str.equals("NoMessage")){
    				//i --;
    				continue;
    			}
    			distance[0] += Double.valueOf(str);
    		}
    		distance[0] = distance[0]/3;
		
			/* Turn left and scan for the distance of heading direction */
			DSComm.sendMessage("left");
	    	for(int i=0; i<3; i++) {
	    		DSComm.sendMessage("SCAN");
	    		String str = DSComm.readMessage();
	    		
	    		System.out.println("The received message is: "+str);
	    		
	    		if(str.equals("NoMessage")){
	   				//i --;
	    			continue;
	    		}
	    		distance[1] += Double.valueOf(str);
	   		}
			distance[1] = distance[1]/3;
				
			/* Turn left and scan for the distance of heading direction */
			DSComm.sendMessage("left");
		    for(int i=0; i<3; i++) {
		   		DSComm.sendMessage("SCAN");
	  			String str = DSComm.readMessage();
	  			
	  			System.out.println("The received message is: "+str);
	  			
	 			if(str.equals("NoMessage")){
		   		//	i --;
		   			continue;
	    		}
	 			distance[2] += Double.valueOf(str);
		    }
			distance[2] = distance[2]/3;
			
			/* Turn left and scan for the distance of heading direction */
			DSComm.sendMessage("left");
	    	for(int i=0; i<3; i++) {
		   		DSComm.sendMessage("SCAN");
		    	String str = DSComm.readMessage();
		    	
		    	System.out.println("The received message is: "+str);
		    	
		  		if(str.equals("NoMessage")){
		  		//	i --;
		   			continue;
		   		}
		  		distance[3] += Double.valueOf(str);
	    	}
			distance[3] = distance[3]/3;
			DSComm.sendMessage("left");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return distance;
    }
    
    public Location getLocation() throws InterruptedException {
    	double[] distance = getAroundDistance();
    	try{
//			System.out.println("distance[0] "+distance[0]);
//			System.out.println("distance[1] "+distance[1]);
//			System.out.println("distance[2] "+distance[2]);
//			System.out.println("distance[3] "+distance[3]);
			ArrayList<Location> locations = new ArrayList<Location>();
			for(int i=0; i<6; i++) {
				for(int j=0; j<6; j++) {
					Location tmp = comparingLocation(distance, i, j);
					if(tmp != null) {
						locations.add(tmp);
						System.out.println("The detected location is "+tmp.x+", "+tmp.y);
					}
				}
			}
//			System.out.println(GridDistance[5][2].getNorthDistance());
//			System.out.println(GridDistance[5][2].getEastDistance());
//			System.out.println(GridDistance[5][2].getSouthDistance());
//			System.out.println(GridDistance[5][2].getWestDistance());
			while(locations.size()>1) {
				locations = getAccurateLocation(distance, locations);
			}
			model.setAgent(locations.get(0).y+1,locations.get(0).x+1);
			return locations.get(0);
		} catch (IOException e) {
			System.out.println("Fail in location part");
			e.printStackTrace();
			return null;
		}
    }
    
    public void scanColor(int index) throws IOException, InterruptedException {
    	DSComm.sendMessage("COLOR");
    	String message = DSComm.readMessage();
    	
    	System.out.println("The received message is: "+message);
    	
    	while(message.equals("NoMessage"))
    		message = DSComm.readMessage();
    	
    	if(message.equals("RED")) {
    		victims.get(index).setType("RED");
    		System.out.println("The red victim has found!");
    		
    	} else if(message.equals("GREEN")) {
    		
    	} else if(message.equals("BLUE")) {
    		
    	} else if(message.equals("NoColor")) {
    		
    	}
    }
    
    public void findVictim() throws IOException, InterruptedException {
    	for(int i=0; i<victims.size(); i++) {
    		Cell startCell = new Cell(now.x, now.y);
    		Cell targetCell = new Cell(victims.get(i).getLocation().x, victims.get(i).getLocation().y);
        	ArrayList<Cell> path = new PathFinder().findPath(new Map().getProbabilityMap(), targetCell, startCell);
        	if(path == null) {
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
			try{
				//setAgPos(0, 0, 0);
				
				//Location location = new Location(GWIDTH/2, GLENGTH/2);
				//setAgPos(1, location);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
//			add(victim, 3, 4);
			add(Obstacle, 2, 3);
			add(Obstacle, 1, 6);
			add(Obstacle, 3, 2);
			add(Obstacle, 5, 2);
			add(Obstacle, 5, 5);
			add(Obstacle, 6, 5);
			add(victim, 1, 1);
			add(victim, 4, 1);
			add(victim, 3, 3);
			add(victim, 4, 4);
			add(victim, 3, 5);
			
			add(Obstacle, 0, 1);
			add(Obstacle, 0, 2);
			add(Obstacle, 0, 3);
			add(Obstacle, 0, 4);
			add(Obstacle, 0, 5);
			add(Obstacle, 0, 6);
			add(Obstacle, 0, 7);
			add(Obstacle, 0, 0);
			add(Obstacle, 1, 0);
			add(Obstacle, 2, 0);
			add(Obstacle, 3, 0);
			add(Obstacle, 4, 0);
			add(Obstacle, 5, 0);
			add(Obstacle, 6, 0);
			add(Obstacle, 7, 0);
			add(Obstacle, 7, 1);
			add(Obstacle, 7, 2);
			add(Obstacle, 7, 3);
			add(Obstacle, 7, 4);
			add(Obstacle, 7, 5);
			add(Obstacle, 7, 6);
			add(Obstacle, 7, 7);
			add(Obstacle, 1, 7);
			add(Obstacle, 2, 7);
			add(Obstacle, 3, 7);
			add(Obstacle, 4, 7);
			add(Obstacle, 5, 7);
			add(Obstacle, 6, 7);
		}
		
		protected void setAgent(int x, int y) {
			setAgPos(0, x, y);
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
		
        public void drawVictim(Graphics g, int x, int y) {
        	System.out.println("unknown");
        	String type = getCurrentGridVictim();
        	super.draw(g, x, y, 1);
        	if(type.equals("unknown")) {
        		g.setColor(Color.black);
            	drawString(g, x, y, defaultFont, "Unknown");
        	} else if(type.equals("RED")) {
        		g.setColor(Color.red);
        		drawString(g, x, y, defaultFont, "Red victim");
        	} else if(type.equals(Color.blue)) {
        		g.setColor(Color.blue);
        		drawString(g, x, y, defaultFont, "Blue victim");
        	} else if(type.equals("GREEN")) {
        		g.setColor(Color.green);
        		drawString(g, x, y, defaultFont, "Green victim");
        	} 
        	
        	
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