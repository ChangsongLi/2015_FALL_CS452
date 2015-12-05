import java.util.Arrays;
import java.util.HashMap;

public class GridBlock {
	private final int LEFT = 0;
	private final int RIGHT = 1;
	private final int UP = 2;
	private final int DOWN = 3;
	
	private HashMap<Integer,Double> states;
	
	GridBlock(){
		states = new HashMap<Integer,Double>();
		states.put(LEFT, 0.0);
		states.put(RIGHT, 0.0);
		states.put(UP, 0.0);
		states.put(DOWN, 0.0);
	}
	
	public void updateLeft(double d){
		states.put(LEFT, d);
	}
	
	public void updateRight(double d){
		states.put(RIGHT, d);
	}
	
	public void updateUp(double d){
		states.put(UP, d);
	}
	
	public void updateDown(double d){
		states.put(DOWN, d);
	}
	
	public double getUp()
	{
		return states.get(UP);
	}
	
	public double getDown()
	{
		return states.get(DOWN);
	}
	
	public double getLeft()
	{
		return states.get(LEFT);
	}
	
	public double getRight()
	{
		return states.get(RIGHT);
	}
	
	public double getMax(){
		double[] directions = new double[4];
		directions[0] = getUp();
		directions[1] = getDown();
		directions[2] = getLeft();
		directions[3] = getRight();
		for(int i = 0; i < 4; i++){
			//System.out.print(directions[i] + " ");
		}
		Arrays.sort(directions);
		return directions[3];
	}
	
	public int getBestAction(){
		double max = getMax();
		if(states.get(LEFT) == max){
			return LEFT;
		}
		if(states.get(RIGHT) == max){
			return RIGHT;
		}
		if(states.get(UP) == max){
			return UP;
		}
		return DOWN;
	}
	
}
