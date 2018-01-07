import java.util.ArrayList;

/**
 * Cell class is responsible for representing one unit of the main map.
 */
public class Cell {

	private int f; // f(n) cost estimate
	private int g; // g(n) is the cost of the path from the start node to n
	private int h; // h(n) is a heuristic that estimates the cost of the cheapest path from n to the goal

	/**
	 * Cell position in the grid
	 */
	private int x;
	private int y;

	/**
	 * store the neighbours
	 * 
	 */
	private ArrayList<Cell> neighbours = new ArrayList<Cell>();

	/**
	 * For each node, which node it can most efficiently be reached from. If a node
	 * can be reached from many nodes, cameFrom will eventually contain the most
	 * efficient previous step.
	 */
	public Cell previous = null;
	/**
	 * is this node a wall/obstacle
	 */
	private float cellProbability;

	/**
	 * ----- Constructor ------
	 * 
	 * @param x
	 * @param y
	 */
	public Cell(int x, int y) {
		this.setF(0);
		this.setG(0);
		this.setH(0);

		this.x = x;
		this.y = y;
	}

	/**
	 * Calculate each cell's neighbour Diagonal neighbours are to be excluded since
	 * the project requirements do not ask for diagonal movement
	 */
	public void addNeighbours(Cell[][] grid, int i, int j, int rows, int cols) {

		if (i < cols - 1) {
			neighbours.add(grid[i + 1][j]);
		}

		if (i > 1) {
			neighbours.add(grid[i - 1][j]);
		}

		if (j < rows - 1) {
			neighbours.add(grid[i][j + 1]);
		}

		if (j > 1) {
			neighbours.add(grid[i][j - 1]);
		}
	}

	/**
	 * Returns the cell's x position in the grid
	 */
	public int getCellXPos() {
		return x;
	}

	/**
	 * Returns the cell's y position in the grid
	 */
	public int getCellYPos() {
		return y;
	}

	public int getF() {
		return f;
	}

	/**
	 * @return f(n) cost estimate
	 */
	public void setF(int f) {
		this.f = f;
	}

	/**
	 * Get the g(n)
	 * 
	 * @return g g(n) is the cost of the path from the start cell to n
	 */
	public int getG() {
		return g;
	}

	/**
	 * Set the g(n)
	 * 
	 * @param g
	 *            g(n) is the cost of the path from the start cell to n
	 */
	public void setG(int g) {
		this.g = g;
	}

	/**
	 * Get the h(n)
	 * 
	 * @return h(n)- a heuristic that estimates the cost of the cheapest path from n
	 *         to the goal
	 */
	public int getH() {
		return h;
	}

	/**
	 * Set the h(n)
	 * 
	 * @param h
	 *            - a heuristic that estimates the cost of the cheapest path from n
	 *            to the goal
	 */
	public void setH(int h) {
		this.h = h;
	}

	/**
	 * Get the previous
	 * 
	 * @return Cell - which cell it can most efficiently be reached from
	 */
	public Cell getPrevious() {
		return previous;
	}

	/**
	 * Set the previous
	 * 
	 * @param Cell
	 *            previous - which cell it can most efficiently be reached from
	 */
	public void setPrevious(Cell previous) {
		this.previous = previous;
	}

	/**
	 * Get neighbouring cells
	 * 
	 * @return ArrayList<Cell> - array of neighbouring cells (diagonals excluded)
	 */
	public ArrayList<Cell> getNeighbours() {
		return this.neighbours;
	}

	/**
	 * Get Cell probability
	 * 
	 * @return probability - the probability of having an obstacle on this cell
	 */
	public float getCellProbability() {
		return cellProbability;
	}

	/**
	 * Set Cell probability
	 * 
	 * @param probability
	 *            - he probability of having an obstacle on this cell
	 */
	public void setCellProbability(float cellProbability) {
		this.cellProbability = cellProbability;
	}

}