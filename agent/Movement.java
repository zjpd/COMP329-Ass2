import java.util.ArrayList;

public class Movement {
	
	private ArrayList<Cell> path;
	private Map map;
	private Cell targetCell;
	private Cell startCell;
	private String Heading;
	private PilotControl pilot;
	
	private int currentX;
	private int currentY;
	
	public static final float CELL_LENGTH = 33;
	public static final float CELL_WIDTH = 21;
	
	public Movement(Map map, Cell targetCell, Cell startCell, int X, int Y, String heading, PilotControl pilot) {
		this.map = map;
		this.targetCell = targetCell;
		this.startCell = startCell;
		this.Heading = heading;
		this.pilot = pilot;
		currentX = X;
		currentY = Y;
		path = new PathFinder().findPath(this.map.getProbabilityMap(), this.targetCell, this.startCell);
		
		moving();
	}
	
	public void setTargetCell(Cell targetCell) {
		this.targetCell = targetCell;
	}
	public void setStartCell(Cell startCell) {
		this.startCell = startCell;
	}
	public void setX(int X) {
		currentX = X;
	}
	public void setY(int Y) {
		currentY = Y;
	}
	public void setHeading(String heading) {
		Heading = heading;
	}
	
	
	private void moving() {
		if(path == null)
			return;
		for(int i=0; i<path.size(); i++) {
			int nextX = path.get(i).getCellXPos();
			int nextY = path.get(i).getCellYPos();
			if(nextX > currentX) {
				switch(Heading) {
					case "North":
						pilot.rotate(90);
						break;
					case "East":
						break;
					case "South":
						pilot.rotate(-90);
						break;
					case "West":
						pilot.rotate(180);
						break;
				}
				pilot.forward(CELL_WIDTH);
			} else if (nextX < currentX) {
				switch(Heading) {
					case "North":
						pilot.rotate(-90);
						break;
					case "East":
						pilot.rotate(180);
						break;
					case "South":
						pilot.rotate(90);
						break;
					case "West":
						break;
				}
				pilot.forward(CELL_WIDTH);
			} else if (nextY > currentY) {
				switch(Heading) {
					case "North":
						break;
					case "East":
						pilot.rotate(-90);
						break;
					case "South":
						pilot.rotate(180);
						break;
					case "West":
						pilot.rotate(90);
						break;
				}
				pilot.forward(CELL_LENGTH);
			} else if (nextY < currentY) {
				switch(Heading) {
					case "North":
						pilot.rotate(180);
						break;
					case "East":
						pilot.rotate(90);
						break;
					case "South":
						break;
					case "West":
						pilot.rotate(-90);
						break;
				}
				pilot.forward(CELL_LENGTH);
			}
		}
	}
	
	

}
