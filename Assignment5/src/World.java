import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class World {
	private char[][] world = new char[10][10];
	World(String file){
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			int row = 0;
			while(line != null){
				for(int i = 0; i < line.length(); i++){
					world[row][i] = line.charAt(i);
				}
				
				line = br.readLine();
				row++;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	char[][] getWorld(){
		return world;
	}
}
