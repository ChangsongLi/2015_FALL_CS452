import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SARSA {
	private char[][] world;
	private Point start;
	private Point end;
	private final int NORMALCOST = -1;
	private final int HOLECOST = -100;
	private final int LEFT = 0;
	private final int RIGHT = 1;
	private final int UP = 2;
	private final int DOWN = 3;
	private final double GAMMA = 0.9;
	private final double ALPHA = 1;
	private double EPSILON = 0.9;
	private final int NOTSLIPER = -1;
	private GridBlock[][] grid;
	private FileWriter writer,writerReward;

	SARSA(String file) {
		File f = new File("SARSA.txt");
		File fReward = new File("SARSAReward.txt");
	    try {
			f.createNewFile();
			writer = new FileWriter(f);
			writerReward = new FileWriter(fReward);
			getWorld(file);
			setUpGrid();
			QValue();
			writer.flush();
			writer.close();
			writerReward.flush();
			writerReward.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUpGrid() {
		grid = new GridBlock[world.length][world[0].length];
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world.length; j++) {
				grid[i][j] = new GridBlock();
			}
		}
	}

	public void getWorld(String file) {
		World w = new World(file);
		world = w.getWorld();

		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world.length; j++) {
				if (world[i][j] == 'S') {
					start = new Point(i, j);
				}
				if (world[i][j] == 'G') {
					end = new Point(i, j);
				}
			}
		}
	}

	/*
	 * check whether is hole or end or ice
	 */
	public boolean isHole(int x, int y) {
		return world[x][y] == 'H';
	}

	public boolean isEnd(int x, int y) {
		return world[x][y] == 'G';
	}

	public boolean isIce(int x, int y) {
		return world[x][y] == 'I';
	}

	/*
	 * check whether can go 4 directions
	 */
	public boolean canGoThere(int x, int y) {
		return x >= 0 && x <= world.length - 1 && y <= world.length - 1 && y >= 0;
	}

	/*
	 * Get reward corresponding by the type of place.
	 */
	public int getReward(int x, int y) {
		if (world[x][y] == 'H')
			return HOLECOST;
		else
			return NORMALCOST;
	}

	/*
	 * Get random result for Ice surface
	 */
	public int getResultOnIce() {
		int ran = (int) (Math.random() * 10);
		if (ran == 0) {
			return LEFT;
		} else if (ran == 1) {
			return RIGHT;
		}
		return NOTSLIPER;
	}

	/*
	 * Choose random Action.
	 */
	public int chooseRandomAction() {
		return (int) (Math.random() * 4);
	}

	/*
	 * Choose best action by the position of grid.
	 */
	public int chooseBestAction(int x, int y) {
		return grid[x][y].getBestAction();
	}

	public double getCurrentQValue(int x, int y, int direction) {
		if (direction == DOWN)
			return grid[x][y].getDown();
		if (direction == UP)
			return grid[x][y].getUp();
		if (direction == LEFT)
			return grid[x][y].getLeft();

		return grid[x][y].getRight();
	}

	public void updateValue(int x, int y, int direction, double newValue) {
		if (direction == DOWN)
			grid[x][y].updateDown(newValue);
		else if (direction == UP)
			grid[x][y].updateUp(newValue);
		else if (direction == LEFT)
			grid[x][y].updateLeft(newValue);
		else if (direction == RIGHT)
			grid[x][y].updateRight(newValue);
	}

	public void printWorld(){
		for(int i = 0; i < 10; i++){
			for(int j = 0; j < 10; j++){
				if(isHole(i,j)){
					try {
						writer.write("H");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(isEnd(i,j)){
					try {
						writer.write("G");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					if(chooseBestAction(i, j) == UP)
						try {
							writer.write("U");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if(chooseBestAction(i, j) == DOWN)
						try {
							writer.write("D");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if(chooseBestAction(i, j) == LEFT)
						try {
							writer.write("L");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if(chooseBestAction(i, j) == RIGHT)
						try {
							writer.write("R");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
			try {
				writer.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/*
	 * 
	 */
	public void QValue() {

		for (int episodes = 1; episodes <= 2000; episodes++) {
			System.out.println("Episode " + episodes);
			Point currentPoint = start;
			int currentAction = -1;
			int nextAction = -1;
			boolean sTerminal = false;
			boolean first = true;
			int reward = 0;
			while (!sTerminal) {
				if(first){
					currentAction = chooseEpsilonGreedyAction(currentPoint.x, currentPoint.y, episodes);
				}
				Point newPosition;
				// Right now not on Ice
				if(!isIce(currentPoint.x, currentPoint.y)){
					newPosition = getNewPosition(currentPoint, currentAction);
				}else{
					newPosition = getNewPositionOnIce(currentPoint, currentAction);
				}
				// is still in grid
				if (canGoThere(newPosition.x, newPosition.y)) {
					// drop in hole
					if (isHole(newPosition.x, newPosition.y)) {
						reward = reward - 100;
						nextAction = chooseEpsilonGreedyAction(currentPoint.x, currentPoint.y, episodes);
						double currentQValue = getCurrentQValue(currentPoint.x, currentPoint.y, currentAction);
						double nextStateQValue = getCurrentQValue(currentPoint.x, currentPoint.y, nextAction);
						double newValue = currentQValue
								+ ALPHA * (HOLECOST + (GAMMA * nextStateQValue) - currentQValue);
						updateValue(currentPoint.x, currentPoint.y, currentAction, newValue);
					} else {
						int cost = -1;
						if (isGoal(newPosition)) {
							cost = 0;
							sTerminal = true;
						}else{
							reward = reward - 1;
						}
						nextAction = chooseEpsilonGreedyAction(newPosition.x, newPosition.y, episodes);
						double currentQValue = getCurrentQValue(currentPoint.x, currentPoint.y, currentAction);
						double nextStateQValue = getCurrentQValue(newPosition.x, newPosition.y, nextAction);
						double newValue = currentQValue + ALPHA * (cost + (GAMMA * nextStateQValue) - currentQValue);
						updateValue(currentPoint.x, currentPoint.y, currentAction, newValue);
						currentPoint = newPosition;
					}

				}
				// hit wall
				else {
					reward = reward - 1;
					nextAction = chooseEpsilonGreedyAction(currentPoint.x, currentPoint.y, episodes);
					double currentQValue = getCurrentQValue(currentPoint.x, currentPoint.y, currentAction);
					double nextStateQValue = getCurrentQValue(currentPoint.x, currentPoint.y, nextAction);
					double newValue = currentQValue + ALPHA * (-1 + (GAMMA * nextStateQValue) - currentQValue);
					updateValue(currentPoint.x, currentPoint.y, currentAction, newValue);
				}
				currentAction = nextAction;
			}
			
			try {
				writerReward.write(reward+"\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(episodes%100 == 0){
				try {
					writer.write("Episodes "+episodes+"\n");
					printWorld();
					writer.write("\n\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isGoal(Point p) {
		return isEnd(p.x, p.y);
	}

	private Point getNewPosition(Point current, int direction) {
		if (direction == UP)
			return new Point(current.x-1, current.y);
		if (direction == DOWN)
			return new Point(current.x+1, current.y);
		if (direction == LEFT)
			return new Point(current.x, current.y-1);

		return new Point(current.x, current.y+1);
	}

	private Point getNewPositionOnIce(Point current, int direction) {
		int result = getResultOnIce();
		if (direction == UP) {
			if (result == NOTSLIPER)
				return new Point(current.x - 1, current.y);
			else if (result == LEFT)
				return new Point(current.x - 1, current.y + 1);
			else
				return new Point(current.x - 1, current.y - 1);
		}
		if (direction == DOWN) {
			if (result == NOTSLIPER)
				return new Point(current.x+1, current.y);
			else if (result == LEFT)
				return new Point(current.x + 1, current.y - 1);
			else
				return new Point(current.x + 1, current.y + 1);
		}
		if (direction == LEFT) {
			if (result == NOTSLIPER)
				return new Point(current.x, current.y - 1);
			else if (result == LEFT)
				return new Point(current.x + 1, current.y - 1);
			else
				return new Point(current.x - 1, current.y - 1);
		}
		if (result == NOTSLIPER)
			return new Point(current.x, current.y+1);
		else if (result == LEFT)
			return new Point(current.x + 1, current.y + 1);
		else
			return new Point(current.x - 1, current.y + 1);
	}

	private int chooseEpsilonGreedyAction(int x, int y, int episodes) {
		double number = Math.random();
		if (episodes > 1000)
			return chooseBestAction(x, y);
		else {
			if (episodes % 10 == 0) {
				EPSILON = 0.9 / (episodes / 10);
			}
		}
		
		if (number > EPSILON){
			return chooseBestAction(x, y);
		}

		else{
			return chooseRandomAction();
		}
	}
}
