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
	
	public static final int GWIDTH = 8;
	public static final int GLENGTH = 8;
	public static final int Victim = 16;
	public static final int Obstacle = 7;
	public static final int unknown = 8;
	
    private Logger logger = Logger.getLogger("comp329DS.mas2j."+DSEnv.class.getName());

    /** Called before the MAS execution with the args informed in .mas2j */

    @Override

    public void init(String[] args) {

        super.init(args);

        //addPercept(ASSyntax.parseLiteral("percept(demo)"));
		model = new DSModel();
		view = new DSView(model);
		model.setView(view);
		communicate = new DSComm();
		comThread = new Thread(communicate);
		comThread.start();
    }



    @Override

    public boolean executeAction(String agName, Structure action) {

        logger.info("executing: "+action+", but not implemented!");
        if (action.equals("location")) { // you may improve this condition
             try {
				communicate.sendMessage(action.toString());
				String location = communicate.readMessage(); //location format -- x,y
				
				while(location.equals("NoMessage"))
					location = communicate.readMessage();
				
				int currentX = Integer.valueOf(location.substring(0,1));
				int currentY = Integer.valueOf(location.substring(location.length()-1, location.length()));
				model.setAgent(currentX, currentY);
				
				while(!location.equals("end")) {
					location = communicate.readMessage();
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

	
	class DSModel extends GridWorldModel {
		
		protected boolean findVictim = false;
		
		protected DSModel() {
			super(GWIDTH, GLENGTH, 2);
			try{
				//setAgPos(0, 0, 0);
				
				Location location = new Location(GWIDTH/2, GLENGTH/2);
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
}


