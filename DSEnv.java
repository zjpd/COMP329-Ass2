// Environment code for project comp329DS.mas2j


import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.*;



public class DSEnv extends Environment {

	private DataOutputStream output;
	private DSModel model;
	private DSView view;
	private DSComm communicate;
	
	public static final int GWIDTH = 6;
	public static final int GLENGTH = 7;
	public static final int Victim = 16;
	
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
    }



    @Override

    public boolean executeAction(String agName, Structure action) {

        logger.info("executing: "+action+", but not implemented!");
        if (action.equals("location")) { // you may improve this condition
             try {
				communicate.sendMessage(action.toString());
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
				setAgPos(0, 0, 0);
				
				Location location = new Location(GWIDTH/2, GLENGTH/2);
				setAgPos(1, location);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			add(Victim, 3, 0);
		}
	}
    
	class DSView extends GridWorldView {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DSView(DSModel model) {
            super(model, "Mars World", 600);
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true);
            repaint();
        }

        /** draw application objects */
        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
                case DSEnv.Victim: drawGarb(g, x, y);  break;
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

        public void drawGarb(Graphics g, int x, int y) {
            super.drawObstacle(g, x, y);
            g.setColor(Color.white);
            drawString(g, x, y, defaultFont, "G");
        }
		
	}
}


