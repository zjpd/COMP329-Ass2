

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ReadMap {

	public int[][] ReadMap(int numberOfRows,int numberOfColumns) throws FileNotFoundException
	{
		int[][] map = new int[numberOfRows][numberOfColumns];
		
		Scanner input = new Scanner(new File("resources/MapFile"));
		
		 while (input.hasNextLine())
		 {
			 for (int i = 0; i < numberOfRows; i++) {
				 for (int j = 0; j < numberOfColumns; j++)
	             {
	                   map[i][j] = input.nextInt();
	             }
	                
			 }
		 }
		 
		 return map;
	
	}
}