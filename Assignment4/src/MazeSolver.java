
/**
 * AI assignment 4.
 * @author changsongli
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class MazeSolver {

	private int numOfRow;
	private int numOfCol;
	private int[][] maze;
	private int carryCapacity;
	private HashMap<String, MyPoint> map;
	private MyPoint agentStartingPoint;
	private int[] re;
	private Vector<MyPoint> agentTargetToVisit;

	public static void main(String[] args) {
		MazeSolver m = new MazeSolver(args);
	}

	MazeSolver(String[] args) {
		MazeMDP mdp = new MazeMDP();

		// get input, and set up MDP
		getInputsFromFile(args);
		getMDP(mdp);
		System.out.println("Testing Deterministic MDP.");
		System.out.println();
		System.out.print("Target values to obtain: ");
		System.out.print("[");
		for (int i = 0; i < agentTargetToVisit.size(); i++) {
			if (i == 0)
				System.out.print((int) (agentTargetToVisit.get(i).getPoints()));
			else {
				System.out.print(", " + (int) (agentTargetToVisit.get(i).getPoints()));
			}
		}
		System.out.print("]");
		System.out.println();
		System.out.println();
		getPolicyInteration(mdp);
	}

	private void getPolicyInteration(MazeMDP mdp) {
		PolicyInteration p = new PolicyInteration();
		p.setData(maze, agentStartingPoint, agentTargetToVisit, carryCapacity);
		p.evaluate(mdp);
	}

	private void getInputsFromFile(String[] args) {
		carryCapacity = Integer.valueOf(args[1]);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			String oneLine = reader.readLine();
			String[] arr = oneLine.split(" ");
			numOfRow = Integer.parseInt(arr[0]);
			numOfCol = Integer.parseInt(arr[1]);
			maze = new int[numOfRow][numOfCol];

			for (int i = 0; i < numOfRow; i++) {
				oneLine = reader.readLine();
				arr = oneLine.split(" ");
				for (int j = 0; j < numOfCol; j++) {
					maze[i][j] = Integer.parseInt(arr[j]);
				}
			}
			reader.close();
		} catch (Exception e) {

		}
	}

	public void findAllCombination(int targetSize, int targetCap, int num, int num2, List<LinkedList<MyPoint>> list) {
		int i;
		if (num2 == targetCap) {
			LinkedList<MyPoint> listOne = new LinkedList<MyPoint>();
			for (i = 0; i < targetCap; i++) {
				listOne.add(agentTargetToVisit.get(re[i]));
			}
			list.add(listOne);
			return;
		}

		for (i = num; i < targetSize; i++) {
			re[num2] = i;
			findAllCombination(targetSize, targetCap, i + 1, num2 + 1, list);
		}
	}

	public void getMDP(MazeMDP mdp) {
		map = new HashMap<String, MyPoint>();

		// get all target and starting point of agent
		Vector<Integer> target = new Vector<Integer>();
		for (int row = 0; row < maze.length; row++) {
			for (int col = 0; col < maze[row].length; col++) {
				if (maze[row][col] > 1) {
					target.add(maze[row][col]);
					map.put("" + maze[row][col], new MyPoint(row, col, maze[row][col]));
				} else if (maze[row][col] == 1) {
					agentStartingPoint = new MyPoint(row, col, 1);
				}
			}
		}

		// int[] queue = target.toArray(new int[target.size()]);
		int count = 0, allTarget[] = new int[target.size()];
		for (int i : target)
			allTarget[count++] = i;
		Arrays.sort(allTarget);
		agentTargetToVisit = new Vector<MyPoint>();
		int first = allTarget.length - 1;
		for (int i = first; i > first - carryCapacity; i--)
			agentTargetToVisit.add(map.get(allTarget[i] + ""));

		for (int row = 0; row < maze.length; row++) {
			for (int col = 0; col < maze[row].length; col++) {
				for (int capacity = 0; capacity <= carryCapacity; capacity++) {

					ArrayList<LinkedList<MyPoint>> list = new ArrayList<LinkedList<MyPoint>>();
					re = new int[100];
					findAllCombination(agentTargetToVisit.size(), agentTargetToVisit.size() - capacity, 0, 0, list);
					for (int i = 0; i < list.size(); i++) {
						State state = new State();
						// System.out.println("State");
						// System.out.println("x = "+row);
						// System.out.println("y = "+col);
						// System.out.println("c = "+capacity);
						state.remain = list.get(i);
						// for(int c = 0; c < state.remain.size();c++ ){
						// System.out.println(state.remain.get(c).getX()+"
						// "+state.remain.get(c).getY());
						// }
						state.x = row;
						state.y = col;
						state.c = capacity;
						state.remain = list.get(i);
						mdp.addState(state);
					}
				}
			}
		}
	}
}
