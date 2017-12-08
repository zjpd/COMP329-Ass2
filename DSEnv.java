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
import java.util.logging.*;



public class DSEnv extends Environment {

	private DSModel model;
	private DSView view;
	private DSComm communicate;
	private Thread comThread;
	
	public static mapGridDis[][] GridDistance = new mapGridDis[6][6];
	public static int[][] mapInform = new int[8][8];
			
	public static final int GWIDTH = 8;
	public static final int GLENGTH = 8;
	public static final int Victim = 16;
	public static final int Obstacle = 7;
	public static final int unknown = 8;
	public static final int WIDTH = 25;	//10 inches 25.4
	public static final int LENGTH = 30;	//12 -- 30.48 cm in length
	
    private Logger logger = Logger.getLogger("comp329DS.mas2j."+DSEnv.class.getName());

    /** Called before the MAS execution with the args informed in .mas2j */

    @Override

    public void init(String[] args) {

        super.init(args);

        //addPercept(ASSyntax.parseLiteral("percept(demo)"));
        updateMapInform();
        updateGridDistance();
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
        if (action.equals("location")) { // you may improve this condition
             try {
				DSComm.sendMessage(action.toString());
				String location = DSComm.readMessage(); //location format -- x,y
				
				while(location.equals("NoMessage"))
					location = DSComm.readMessage();
				
				int currentX = Integer.valueOf(location.substring(0,1));
				int currentY = Integer.valueOf(location.substring(location.length()-1, location.length()));
				model.setAgent(currentX, currentY);
				
				while(!location.equals("end")) {
					location = DSComm.readMessage();
					currentX = Integer.valueOf(location.substring(0,1));
					currentY = Integer.valueOf(location.substring(location.length()-1, location.length()));
					model.setAgent(currentX, currentY);
				}
				System.out.println("End!");
			} catch (IOException e) {
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

    }
    
    public void updateGridDistance() {
    	for(int i=0; i<6; i++) {
    		for(int j=0; j<6; j++) {
    			//System.out.println("The distance is: "+d1);
    			GridDistance[i][j].setNorthDistance(calculateNorth(i, j));
    			GridDistance[i][j].setEastDistance(calculateEast(i, j));
    			GridDistance[i][j].setSouthDistance(calculateSouth(i, j));
    			GridDistance[i][j].setWestDistance(calculateWest(i, j));
    		}
    	}
    }
    
    public double calculateNorth(int x, int y) {
    	int num = 0;
    	x++;
    	for(int i=y; i>=0; i--) {
    		if(mapInform[x][i] != 1){
    			num++;
    		}else{
    			break;
    		}
    	}
    	return num*LENGTH;
    }
    
    public double calculateEast(int x, int y) {
    	int num = 0;
    	y++;
    	for(int i=x; i<=7; i++) {
    		if(mapInform[i][y] != 1){
    			num++;
    		}else{
    			break;
    		}
    	}
    	return num*WIDTH;
    }
    
    public double calculateSouth(int x, int y) {
    	int num = 0;
    	x++;
    	for(int i=y; i<=7; i++) {
    		if(mapInform[x][i] != 1){
    			num++;
    		}else{
    			break;
    		}
    	}
    	return num*LENGTH;
    }
    
    public double calculateWest(int x, int y) {
    	int num = 0;
    	y++;
    	for(int i=x; i>=0; i--) {
    		if(mapInform[i][x] != 1){
    			num++;
    		}else{
    			break;
    		}
    	}
    	return num*WIDTH;
    }
    
    public int[] getLocation() {
    	double distance[] = new double[4];
    	/* Scan for the distance to the obstacle or wall of the heading direction */
    	try {
    		for(int i=0; i<3; i++) {
    			DSComm.sendMessage("SCAN");
    			String str = DSComm.readMessage();
    			if(str.equals("NoMessage")){
    				i --;
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
	    		if(str.equals("NoMessage")){
	   				i --;
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
	 			if(str.equals("NoMessage")){
		   			i --;
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
		  		if(str.equals("NoMessage")){
		  			i --;
		   			continue;
		   		}
		  		distance[3] += Double.valueOf(str);
	    	}
			distance[3] = distance[3]/3;
			
			
		} catch (IOException e) {
			System.out.println("Fail in location part");
			e.printStackTrace();
		}
    	return null;
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
			
			//add(Victim, 3, 4);
			//add(2,4,0);
			add(Obstacle, 2, 3);
			add(Obstacle, 1, 6);
			add(Obstacle, 3, 2);
			add(Obstacle, 5, 2);
			add(Obstacle, 5, 5);
			add(Obstacle, 6, 5);
			add(unknown, 1, 1);
			add(unknown, 4, 1);
			add(unknown, 3, 3);
			add(unknown, 4, 4);
			add(unknown, 3, 5);
			
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

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DSView(DSModel model) {
            super(model, "DS World", 800);
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true);
            repaint();
        }

        /** draw application objects */
        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
                case DSEnv.unknown: drawUnknown(g, x, y);  break;
                case DSEnv.Obstacle: drawObstacle(g, x, y); break;
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
		
        public void drawUnknown(Graphics g, int x, int y) {
        	super.draw(g, x, y, 1);
        	g.setColor(Color.black);
        	drawString(g, x, y, defaultFont, "Unknown");
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
