//import lejos.hardware.Button;

/**
 * This class generates an occupancy grid based on the probability that each cell has
 * an obstacle in it dependent on how many times it has been seen and how many times 
 * it has been seen to contain an obstacle
 *
 */
public class Map {
	//the number of rows and columns repent the cells and walls
	public static final int NUMBER_OF_ROWS = 8;
	public static final int NUMBER_OF_COLUMNS = 7;

	//data that been measured of arena
	public static final float MAP_LENGTH = 195;
	public static final float MAP_WIDTH = 155;
	public static final float BOX_LENGTH = MAP_LENGTH / (NUMBER_OF_ROWS - 2);
	public static final float BOX_WIDTH = MAP_WIDTH / (NUMBER_OF_COLUMNS - 2);
	public static final int HEADING_NORTH = 0;
	public static final int HEADING_WEST = -90;
	public static final int HEADING_EAST = 90;
	public static final int HEADING_SOUTH = -180;
	public static final int MAX_VIEWING_DISTANCE = 200;

	//these array record the probability and if it has been to and if it is occupied
	private Cell[][] mapProbability;
	private int[][] mapSpaceBeenScanned;
	private int[][] mapSpaceOccupied;
	private int[][] mapSpaceBeenTo;

	//record the current location 
	private int currentXCoordinate;
	private int currentYCoordinate;

	//record the end location (blue card)
	private int endLocationX;
	private int endLocationY;

	//record if the map has been generated
	private boolean wholeMapResult;

	/**
	 * Initialise the map
	 * 
	 */
	public Map() {
		currentXCoordinate = 1;
		currentYCoordinate = 1;
		endLocationX = -1;
		endLocationY = -1;
		wholeMapResult = false;

		mapProbability = new Cell[NUMBER_OF_COLUMNS][NUMBER_OF_ROWS];
		mapSpaceBeenScanned = new int[NUMBER_OF_COLUMNS][NUMBER_OF_ROWS];
		mapSpaceOccupied = new int[NUMBER_OF_COLUMNS][NUMBER_OF_ROWS];
		mapSpaceBeenTo = new int[NUMBER_OF_COLUMNS][NUMBER_OF_ROWS];
		// Initialise the 2D arrays
		for (int a = 0; a < NUMBER_OF_COLUMNS; a++) {
			for (int b = 0; b < NUMBER_OF_ROWS; b++) {
				mapProbability[a][b] = new Cell(a, b);
				mapProbability[a][b].setCellProbability(0);

				mapSpaceBeenScanned[a][b] = 0;
				mapSpaceOccupied[a][b] = 0;
				mapSpaceBeenTo[a][b] = 0;
				//start point
				mapSpaceBeenTo[1][1] = 1;
				// update the wall into 1 which means all the walls are
				// occupied.
				if (a == 0 || a == NUMBER_OF_COLUMNS - 1) {
					mapSpaceOccupied[a][b] = 1;
					mapSpaceBeenScanned[a][b] = 1;
					mapProbability[a][b].setCellProbability(1);
					mapSpaceBeenTo[a][b] = 1;
				}
				if (b == 0 || b == NUMBER_OF_ROWS - 1) {
					mapSpaceOccupied[a][b] = 1;
					mapSpaceBeenScanned[a][b] = 1;
					mapProbability[a][b].setCellProbability(1);
					mapSpaceBeenTo[a][b] = 1;
				}

			}
		}

		// update all the neighbours
		for (int a = 0; a < NUMBER_OF_COLUMNS; a++) {
			for (int b = 0; b < NUMBER_OF_ROWS; b++) {
				mapProbability[a][b].addNeighbours(mapProbability, a, b, NUMBER_OF_ROWS - 1, NUMBER_OF_COLUMNS - 1);

			}
		}
	}

	/**
	 * @return The number of times each map space has been seen to be occupied
	 */
	public int[][] getMapSpaceOccupied() {
		return this.mapSpaceOccupied;
	}

	/**
	 * @return A 2D array stating how many times each space in the map grid has been
	 *         scanned
	 */
	public int[][] getMapSpaceBeenScanned() {
		return this.mapSpaceBeenScanned;
	}

	/**
	 * @param i The current x position of the robot
	 */
	public void setCurrentPositionX(int i) {
		currentXCoordinate = i;
	}

	/**
	 * @param j The current y position of the robot on the map
	 */
	public void setCurrentPositionY(int j) {
		currentYCoordinate = j;
	}

	/**
	 * @return The current x coordinate of the robot on the map
	 */
	public int getCurrentPositionX() {
		return this.currentXCoordinate;
	}

	/**
	 * @return The current y coordinate of the robot on the map
	 */
	public int getCurrentPositionY() {
		return this.currentYCoordinate;
	}

	/**
	 * Store the coordinates of the end point on the map
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 */
	public void setEndLocation(int x, int y) {
		this.endLocationX = x;
		this.endLocationY = y;
	}

	/**
	 * @return The int for the x coordinate of the end location on the grid
	 */
	public int getEndLocationX() {
		return this.endLocationX;
	}

	/**
	 * @return The int for the y coordinate of the end location on the grid X and Y
	 */
	public int getEndLocationY() {
		return this.endLocationY;
	}
	
	/**
	 * @return Whether or not the whole map has been seen
	 */
	public boolean isWholeMapResult() {
		return this.wholeMapResult;
	}

	public void setWholeMapResult(boolean wholeMapResult) {
		this.wholeMapResult = wholeMapResult;
	}
	
	public int[][] getMapSpaceBeenTo() {
		return mapSpaceBeenTo;
	}

	public void setMapSpaceBeenTo(int[][] mapSpaceBeenTo) {
		this.mapSpaceBeenTo = mapSpaceBeenTo;
	}
	

	/**
	 * Update the probability that a space on the grid is occupied by an obstacle
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 */
	private void updateCellProbability(int x, int y) {
		if (mapSpaceBeenScanned[x][y] != 0) {
			mapProbability[x][y].setCellProbability(((float) mapSpaceBeenScanned[x][y] + (float) mapSpaceOccupied[x][y])
					/ (2 * (float) mapSpaceBeenScanned[x][y]));
		} else {
			mapProbability[x][y].setCellProbability(0);
		}

	}

	/**
	 * 
	 * update the whole Probability array.
	 * 
	 * 
	 */
	private void updateWholeProbability() {
		boolean result = true;
		// i means COLUMNS j means rows;
		for (int i = 1; i <= NUMBER_OF_COLUMNS - 2; i++) {
			for (int j = 1; j <= NUMBER_OF_ROWS - 2; j++) {
				this.updateCellProbability(i, j);
				//check if there is any space that hasn't been to
				if(this.mapSpaceBeenScanned[i][j] == 0) {
					result = false;
				}
			}
		}
		this.setWholeMapResult(result);
	}

	/**
	 * @return The map probability of occupation for each space in the grid in the
	 *         form of a 2D array
	 */
	public Cell[][] getProbabilityMap() {
		return mapProbability;
	}

	

}