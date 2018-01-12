import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class Simulator {
	
	public static final int GWIDTH = 8;
	public static final int GLENGTH = 8;
	public static final int WIDTH = 30;	//10 inches 25.4 which is 26
	public static final int LENGTH = 38;	//12 -- 30.48 cm in length rectangle-34  square-26
	public static final int CENTER_TO_EDGE_WIDTH = 14;
	public static final int CENTER_TO_EDGE_LENGTH = 17;		//rectangle-17 square-14
	public static final int ZERO_DEVIATION = 13;			//rectangle-13 square-8
	
	
	public static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	public static mapGridDis[][] GridDistance = new mapGridDis[6][6];
	public static int[][] mapInform = new int[8][8];
	public static ArrayList<VictimInform> victims = new ArrayList<VictimInform>();
	
	public static final int[][] rightRotate = new int[][]{{0, -1}, {1, 0}};
	public static final int[][] leftRotate = new int[][]{{0, 1}, {-1, 0}};
	public static final int[] northHeading = new int[]{0, 1};
	public static final int[] southHeading = new int[]{0, -1};
	public static final int[] eastHeading = new int[]{1, 0};
	public static final int[] westHeading = new int[]{-1, 0};
	
	
	private SimulatorComm communication;
	private Location current;
	private String HEADING;
	
	public Simulator(int x, int y, String heading) {
		current = new Location(x, y);
		HEADING = heading;
		
		updateVictims();
		updateMapInform();
		updateGridDistance();
		communication = new SimulatorComm();
		startSimulate();
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
	
	private void startSimulate() {
		System.out.println(GridDistance[5][4].getNorthDistance()+" "
				+GridDistance[5][4].getWestDistance()+" "
				+GridDistance[5][4].getSouthDistance()+" "
				+GridDistance[5][4].getEastDistance());
		String input = " ";
		boolean isEnd = false;
		
		input = communication.getMessage();
		System.out.println("The sent message is: "+input);
		
		while(!isEnd) {
			System.out.println("The current position: "+current.x+" "+current.y);
			System.out.println("Heading: "+HEADING);
			if(input.equals("NoMessage")) {
				input = communication.getMessage();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
				
			}
			
			switch(input) {
				
				case "DISTANCE" :
					
					if(HEADING.equals("NORTH")) {
						communication.send(String.valueOf(GridDistance[current.x][current.y].getNorthDistance()/100));
					}else if(HEADING.equals("WEST")) {
						communication.send(String.valueOf(GridDistance[current.x][current.y].getWestDistance()/100));
					}	else if(HEADING.equals("SOUTH")){
						communication.send(String.valueOf(GridDistance[current.x][current.y].getSouthDistance()/100));
					}	else{
						communication.send(String.valueOf(GridDistance[current.x][current.y].getEastDistance()/100));
					}
					break;
				
				case "COLOR" :
					
					boolean hasColor = false;
					for(int i=0; i<victims.size(); i++) {
						if(victims.get(i).getLocation().equal(current)) {
							communication.send(victims.get(i).getType());
							hasColor = true;
						}
					}
					
					if(!hasColor)
						communication.send("NoColor");
					
					break;
					
				case "FORWARD":
					
					input = communication.getMessage();
					double moveDistance = Double.valueOf(input);
					
					System.out.println("Message is: "+input);
					System.out.println("Move distance is: "+moveDistance);
					System.out.println();
					
					if(moveDistance >= 26) {
			//			System.out.println("fuck!!!!");
						if(HEADING.equals("NORTH"))
							current.x --;
						else if(HEADING.equals("WEST"))
							current.y --;
						else if(HEADING.equals("SOUTH"))
							current.x ++;
						else
							current.y ++;
					} else if(moveDistance == 14) {
						if(HEADING.equals("NORTH"))
							current.x --;
						else if(HEADING.equals("WEST"))
							current.y --;
						else if(HEADING.equals("SOUTH"))
							current.x ++;
						else
							current.y ++;
					}
						
					break;
					
				case "left":
					
					matrixLeftRotate(1);
					
					break;
					
				case "RIGHT":
					
					matrixRightRotate(1);
					
					break;
					
				case "BACK":
					break;
			}
			input = communication.getMessage();
			System.out.println("The received message is: "+input);
		}
		
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
     * Record the around distance of each grid
     */
    public void updateGridDistance() {
    	
    	for(int i=0; i<6; i++) {
    		for(int j=0; j<6; j++) {
    			GridDistance[i][j] = new mapGridDis();
    		}
    	}
    	
    	for(int i=0; i<6; i++) {
    		for(int j=0; j<6; j++) {
 //   			double dis = calculateNorth(i, j);
 //   			System.out.println(GridDistance.length+" "+i+" "+j);
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
    //	System.out.println(x+" "+y);
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
 //   	y++;
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
    
    public static void main(String args[]) {
    	new Simulator(3, 3,"EAST");
    	//simulator.startSimulate();
    }
    
	
	class SimulatorComm {
		ServerSocket server;
		Socket socket;
		ThreadReader reader;
		PrintWriter writer;
		
		public SimulatorComm() {
			
			try {
				
				server = new ServerSocket(9999);
				socket = server.accept();
				writer = new PrintWriter(socket.getOutputStream());
				reader = new ThreadReader();
				new Thread(reader).start();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public String getMessage() {
			if(queue.size()==0)
				return "NoMessage";
			else
				return queue.poll();
		}
		
		public void stop() {
			writer.flush();
			writer.close();
			System.exit(0);
		}
		
		/**
		 * Send the message to the server
		 * @param message The input parameter.
		 */
		public void send(String message){
			writer.println(message);
			System.out.println("The sent message is: "+message);
			writer.flush();
		}
		
		class ThreadReader implements Runnable{
			
			BufferedReader reader;
			String message;
			
			public ThreadReader() throws IOException {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			
			public void stop() {
				Thread.interrupted();
			}
			
			public void run() {
				
				while(true) {
					try {
						
						message = reader.readLine();
						if(message.equals("NoMessage"))
							continue;
						
						queue.put(message);
						
					} catch(Exception e) {
						System.out.println("Client shut down!!!");
						System.exit(0);
						e.printStackTrace();
					}
				}
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

	class Location {
		
		int x;
		int y;
		
		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {return x;}
		public int getY() {return y;}
		
		public void setX(int x) {this.x = x;}
		public void setY(int y) {this.y = y;}
		
		public String toString() {
			return x+" "+y;
		}
		
		public boolean equal(Location lo) {
			if(x == lo.x && y == lo.y)
				return true;
			else
				return false;
		}
	}
	
}
