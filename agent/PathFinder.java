import java.util.ArrayList;

/**
 * PathFinder Class is responsible for carrying out the calculation of the
 * shortest path using the A* search between a given start cell and target cell
 * using a map constructed by an array of cells
 */
public class PathFinder {

	/**
	 * The set of currently discovered cell that are not evaluated yet. Initially,
	 * only the start cell is known.
	 */
	public ArrayList<Cell> openSet = new ArrayList<Cell>();

	/**
	 * The set of cells already evaluated
	 */
	public ArrayList<Cell> closedSet = new ArrayList<Cell>();

	/**
	 * The path from start to target cell - represented by an ArrayList of Cells
	 */
	public ArrayList<Cell> path = new ArrayList<Cell>();

	/**
	 * Implementation of the A* algorithm. Given a map represented by an array of
	 * Cells, a start Cell and a target Cell, the algorithm will return the shortest
	 * path represented by an ArrayList of Cells from start cell to target cell.
	 * This implementation of the algorithm excludes diagonal traversal of the map.
	 */
	public ArrayList<Cell> findPath(Cell[][] map, Cell targetCell, Cell startCell) {
		boolean pathWasFound = false;

		while (!pathWasFound) {
			if (!openSet.isEmpty()) {
				// index of the cell having the lowest cost estimate f(n)
				int winner = 0;

				// Find the cell with the lowest cost estimate
				for (int i = 0; i < openSet.size(); i++) {
					if (openSet.get(i).getF() < openSet.get(winner).getF()) {
						winner = i;
					}
				}

				// The cell from the openset which has the lowest cost estimate f(n)
				Cell current = openSet.get(winner);
				if (current == targetCell) {
					Cell temp = current;
					path.add(temp);

					while (temp.getPrevious() != null) {
						if (temp.getPrevious() != null) {
							path.add(temp.getPrevious());
							temp = temp.getPrevious();
						}
					}
					
					pathWasFound = true;
					return path;
				}
				openSet.remove(current);
				closedSet.add(current);

				ArrayList<Cell> neighbours = current.getNeighbours();

				for (int i = 0; i < neighbours.size(); i++) {
					Cell neighbour = neighbours.get(i);

					// Explore the next possibility only if the probability of having an obstacle on
					// that cell
					// is less than 8. Ignore the neighbour if it was already evaluated
					if (closedSet.contains(neighbour) == false
							&& Float.compare(neighbour.getCellProbability(), Consts.probBound) < 0) {
						int tempG = current.getG() + 1;
						// Increase the cost of the path from the start cell
						if (openSet.contains(neighbour)) {
							if (tempG < neighbour.getG()) {
								neighbour.setG(tempG);
							}

						} else {
							
							neighbour.setG(tempG);
							openSet.add(neighbour);
						}

						neighbour.setH(heuristic(neighbour, targetCell));
						neighbour.setF(neighbour.getG() + neighbour.getH());
						neighbour.setPrevious(current);
					}
				}

			} else {
				pathWasFound = true;
				return null;
			}

		}

		return null;
	}

	/**
	 * Heuristic Function - Using the Manhattan Distance
	 */
	private int heuristic(Cell a, Cell b) {
		return Math.abs(a.getCellXPos() - b.getCellXPos()) + Math.abs(a.getCellYPos() - b.getCellYPos());
	}
}