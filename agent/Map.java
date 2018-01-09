public class Map {
	
	//The number of rows and columns repent the cells and walls
	public static final int NUMBER_OF_ROWS = 8;
	public static final int NUMBER_OF_COLUMNS = 8;

	//Arena size 
	public static final float MAP_LENGTH = 152.4f;
	public static final float MAP_WIDTH = 198.12f;
	
	//The size of 
	public static final float BOX_LENGTH = MAP_LENGTH / (NUMBER_OF_ROWS - 2);
	public static final float BOX_WIDTH = MAP_WIDTH / (NUMBER_OF_COLUMNS - 2);
	
	// The map, a two dimensional Cell array
	// Cell class being the representation of one square of the arena
	public Cell[][] map;

	public Map(int[][] mapInput)
	{
		map= new Cell[NUMBER_OF_COLUMNS][NUMBER_OF_ROWS];
	
		// Initialise the 2D arrays
		for (int i = 0; i < NUMBER_OF_COLUMNS; i++)
		{
			for (int j = 0; j < NUMBER_OF_ROWS; j++)
			{
				map[i][j] = new Cell(i, j);
				
				if(mapInput[i][j] == 1)
				{
					map[i][j].setCellProbability(1);	
				}
			}
		}
		
		// Update all neighbours - (Required for the A* search)
		for (int i = 0; i < NUMBER_OF_COLUMNS; i++)
		{
			for (int j = 0; j < NUMBER_OF_ROWS; j++)
			{
				map[i][j].addNeighbours(map, i, j, NUMBER_OF_ROWS , NUMBER_OF_COLUMNS);
			}
		}
		
		
		for (int i = 0; i < NUMBER_OF_COLUMNS; i++)
		{
			for (int j = 0; j < NUMBER_OF_ROWS; j++)
			{
				System.out.print(mapInput[i][j] +" ");
			}
			System.out.print("\n");
		}
		
		
	}
	

}