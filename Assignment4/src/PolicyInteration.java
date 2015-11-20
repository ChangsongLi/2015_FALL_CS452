import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

public class PolicyInteration {

	private ArrayList<Integer> path = new ArrayList<Integer>();
	private final static int UP = 0;
	private final static int DOWN = 1;
	private final static int LEFT = 2;
	private final static int RIGHT = 3;
	private final static int STAY = 4;
	private static double minDelta = 0.1;
	private static double jumpDelta = 0.00001;
	private final static double gamma = 0.9;
	private Vector<MyPoint> agentVisitPoint;
	private MyPoint agentStartPoint;
	private int[][] mazeTable;
	private int carryCapacity;
	private MazeMDP mdp;
	private Random generator = new Random();

	public void setData(int[][] maze, MyPoint agentStartingPoint, Vector<MyPoint> agentTargetToVisit,
			int carryCapacity) {
		this.mazeTable = maze;
		this.agentStartPoint = agentStartingPoint;
		this.agentVisitPoint = agentTargetToVisit;
		this.carryCapacity = carryCapacity;
	}

	public void evaluate(MazeMDP mdp) {
		double delta = 0;
		this.mdp = mdp;
		HashMap<State, Double> utilityMAp = new HashMap<State, Double>();
		for (State s : mdp.getStates()) {
			utilityMAp.put(s, 0.0);
		}
		HashMap<State, Integer> policyMap = new HashMap<State, Integer>();
		for (State s : mdp.getStates()) {
			policyMap.put(s, generator.nextInt(4));
		}

		delta = evaluateUtility(mdp, utilityMAp, policyMap);
		while (delta > minDelta) {
			delta = evaluateUtility(mdp, utilityMAp, policyMap);
		}

		System.out.println("Doing policy iteration...");
		int count = 1;
		updatePolicy(mdp, utilityMAp, policyMap);
		delta = evaluateUtility(mdp, utilityMAp, policyMap);
		while (delta > jumpDelta) {
			count++;
			updatePolicy(mdp, utilityMAp, policyMap);
			delta = evaluateUtility(mdp, utilityMAp, policyMap);
		}
		System.out.println("   Evaluated and updated " + count + " policies.");
		System.out.println("Done.");
		System.out.println("");

		State currentState = null;
		for (State s : mdp.getStates()) {
			boolean check = s.x == agentStartPoint.x && s.y == agentStartPoint.y && s.c == 0;
			if (check) {
				currentState = s;
				break;
			}
		}
		while (true) {
			int p = policyMap.get(currentState);
			path.add(p);
			if (p == STAY)
				break;
			Next next = calculateU(currentState, p, utilityMAp);
			currentState = next.state;
		}

		// write the path
		System.out.print("Path of length " + (path.size() - 1) + ": [");
		for (int i = 0; i < path.size() - 1; i++) {
			if (path.get(i) == UP)
				System.out.print(" Up");
			else if (path.get(i) == DOWN)
				System.out.print(" Down");
			else if (path.get(i) == LEFT)
				System.out.print(" Left");
			else if (path.get(i) == RIGHT)
				System.out.print(" Right");

			if (i != (path.size() - 2))
				System.out.print(",");
		}
		System.out.print(" ] ");
	}

	public double evaluateUtility(MazeMDP mdp, HashMap<State, Double> U, HashMap<State, Integer> P) {

		double maxDelta = -999;
		for (State s : mdp.getStates()) {
			Double oldUtility = U.get(s);
			Integer statePolicy = P.get(s);
			Next next = calculateU(s, statePolicy, U);
			double newUtility = next.reward + this.gamma * next.utility;
			U.put(s, newUtility);

			if (Math.abs(newUtility - oldUtility) > maxDelta) {
				maxDelta = Math.abs(newUtility - oldUtility);
			}
		}
		return maxDelta;
	}

	public void updatePolicy(MazeMDP mdp, HashMap<State, Double> U, HashMap<State, Integer> P) {
		for (State s : mdp.getStates()) {
			// 4 direction
			if (s.c == this.carryCapacity && inVisit(new MyPoint(s.x, s.y, mazeTable[s.x][s.y]))) {
				P.put(s, STAY);
			} else {
				double maxUtility = -99;
				int policy = -1;
				for (int i = 0; i < 4; i++) {
					Next next = this.calculateU(s, i, U);
					double utility = next.reward + this.gamma * next.utility;
					;
					if (utility > maxUtility) {
						policy = i;
						maxUtility = utility;
					}
				}
				P.put(s, policy);
			}
		}
	}

	public boolean inVisit(MyPoint point) {
		for (int i = 0; i < this.agentVisitPoint.size(); i++) {
			if (agentVisitPoint.get(i).x == point.x && agentVisitPoint.get(i).y == point.y) {
				return true;
			}
		}
		return false;
	}

	public Next calculateU(State s, Integer policy, HashMap<State, Double> U) {

		Next next = null;
		if (policy == UP)
			next = up(s, U);
		else if (policy == DOWN)
			next = down(s, U);
		else if (policy == LEFT)
			next = left(s, U);
		else if (policy == RIGHT)
			next = right(s, U);
		else if (policy == STAY)
			next = stay(s, U);

		return next;
	}

	public LinkedList<MyPoint> updateRemainList(int x, int y, State s) {
		LinkedList<MyPoint> remain = s.remain;
		LinkedList<MyPoint> newRemainList = new LinkedList<MyPoint>();
		for (int i = 0; i < remain.size(); i++) {
			if (remain.get(i).x == x && remain.get(i).y == y) {
				continue;
			}
			MyPoint point = new MyPoint(remain.get(i).x, remain.get(i).y, remain.get(i).points);
			newRemainList.add(point);
		}
		return newRemainList;
	}

	public boolean inRemainList(int x, int y, State s) {
		for (int i = 0; i < s.remain.size(); i++) {
			if (s.remain.get(i).x == x && s.remain.get(i).y == y)
				return true;
		}
		return false;
	}

	public State findState(int x, int y, int currentCapacity, LinkedList<MyPoint> remain) {
		for (State s : mdp.getStates()) {
			if (s.x == x && s.y == y && s.c == currentCapacity) {
				if (currentCapacity == this.carryCapacity)
					return s;
				if (equalRemain(s.remain, remain)) {
					return s;
				}
			}
		}
		return null;
	}

	public boolean equalRemain(LinkedList<MyPoint> one, LinkedList<MyPoint> two) {
		if (one.size() != two.size())
			return false;
		for (int i = 0; i < one.size(); i++) {
			if (!inTheList(two, one.get(i)))
				return false;
		}
		return true;
	}

	public boolean inTheList(LinkedList<MyPoint> two, MyPoint point) {
		for (int i = 0; i < two.size(); i++) {
			if (point.x == two.get(i).x && point.y == two.get(i).y) {
				return true;
			}
		}
		return false;
	}

	public boolean canReachTheNode(int row, int col) {
		if (row > -1 && row < this.mazeTable.length && col > -1 && col < this.mazeTable[0].length
				&& this.mazeTable[row][col] != -1)
			return true;
		return false;
	}

	public Next stay(State s, HashMap<State, Double> U) {
		double r = 0;
		double nextUtility = 0;
		r = 0;
		State nextState = s;
		nextUtility = U.get(nextState);

		Next nextRes = new Next(nextState, r, nextUtility);
		return nextRes;
	}

	public Next up(State s, HashMap<State, Double> U) {
		int x = s.x;
		int y = s.y;
		double r = 0;
		double nextUtility = 0;
		int currentCapacity = s.c;
		State nextState;

		if (canReachTheNode(x - 1, y)) {
			if (inRemainList(x - 1, y, s)) {
				r = this.mazeTable[x - 1][y];
				currentCapacity++;
				LinkedList<MyPoint> remainList = updateRemainList(x - 1, y, s);
				nextState = findState(x - 1, y, currentCapacity, remainList);

				nextUtility = U.get(nextState);

			} else {
				r = -1;
				LinkedList<MyPoint> remainList = s.remain;
				nextState = findState(x - 1, y, currentCapacity, remainList);

				nextUtility = U.get(nextState);
			}
		} else {
			if (inRemainList(x, y, s)) {
				r = this.mazeTable[x][y];
				currentCapacity++;
				LinkedList<MyPoint> remainList = updateRemainList(x, y, s);
				nextState = findState(x, y, currentCapacity, remainList);

				nextUtility = U.get(nextState);
			} else {
				if (currentCapacity == this.carryCapacity && inVisit(new MyPoint(s.x, s.y, mazeTable[s.x][s.y]))) {
					r = 0;
				} else {
					r = -1;
				}
				nextState = s;
				nextUtility = U.get(nextState);
			}
		}
		Next nextRes = new Next(nextState, r, nextUtility);
		return nextRes;
	}

	public Next down(State s, HashMap<State, Double> U) {
		int x = s.x;
		int y = s.y;
		double reward = 0;
		double nextUtility = 0;
		int currentCapacity = s.c;
		State nextS;

		if (canReachTheNode(x + 1, y)) {
			if (inRemainList(x + 1, y, s)) {
				reward = this.mazeTable[x + 1][y];
				currentCapacity++;
				LinkedList<MyPoint> remainList = updateRemainList(x + 1, y, s);
				nextS = findState(x + 1, y, currentCapacity, remainList);

				nextUtility = U.get(nextS);

			} else {
				reward = -1;
				LinkedList<MyPoint> remainList = s.remain;
				nextS = findState(x + 1, y, currentCapacity, remainList);

				nextUtility = U.get(nextS);
			}
		} else {
			if (inRemainList(x, y, s)) {
				reward = this.mazeTable[x][y];
				currentCapacity++;
				LinkedList<MyPoint> remainList = updateRemainList(x, y, s);
				nextS = findState(x, y, currentCapacity, remainList);
				nextUtility = U.get(nextS);
			} else {
				if (currentCapacity == this.carryCapacity && inVisit(new MyPoint(s.x, s.y, mazeTable[s.x][s.y]))) {
					reward = 0;
				} else {
					reward = -1;
				}
				nextS = s;
				nextUtility = U.get(nextS);
			}
		}
		Next nextRes = new Next(nextS, reward, nextUtility);
		return nextRes;
	}

	public Next left(State s, HashMap<State, Double> U) {
		int x = s.x;
		int y = s.y;
		double reward = 0;
		double nextUtility = 0;
		int currentCapacity = s.c;
		State nextS;

		if (canReachTheNode(x, y - 1)) {
			if (inRemainList(x, y - 1, s)) {
				reward = this.mazeTable[x][y - 1];
				currentCapacity++;
				LinkedList<MyPoint> remainList = updateRemainList(x, y - 1, s);
				nextS = findState(x, y - 1, currentCapacity, remainList);
				nextUtility = U.get(nextS);

			} else {
				reward = -1;
				LinkedList<MyPoint> remainList = s.remain;
				nextS = findState(x, y - 1, currentCapacity, remainList);

				nextUtility = U.get(nextS);
			}
		} else {
			if (inRemainList(x, y, s)) {
				reward = this.mazeTable[x][y];
				currentCapacity++;
				LinkedList<MyPoint> remainList = updateRemainList(x, y, s);
				nextS = findState(x, y, currentCapacity, remainList);
				nextUtility = U.get(nextS);
			} else {
				if (currentCapacity == this.carryCapacity && inVisit(new MyPoint(s.x, s.y, mazeTable[s.x][s.y]))) {
					reward = 0;
				} else {
					reward = -1;
				}
				nextS = s;
				nextUtility = U.get(nextS);
			}
		}
		Next nextRes = new Next(nextS, reward, nextUtility);
		return nextRes;
	}

	public Next right(State s, HashMap<State, Double> U) {
		int x = s.x;
		int y = s.y;
		double reward = 0;
		double nextUtility = 0;
		int currentCapacity = s.c;
		State nextS;

		if (canReachTheNode(x, y + 1)) {
			if (inRemainList(x, y + 1, s)) {
				reward = this.mazeTable[x][y + 1];
				currentCapacity++;
				LinkedList<MyPoint> remainList = updateRemainList(x, y + 1, s);
				nextS = findState(x, y + 1, currentCapacity, remainList);
				nextUtility = U.get(nextS);
			} else {
				reward = -1;
				LinkedList<MyPoint> remainList = s.remain;
				nextS = findState(x, y + 1, currentCapacity, remainList);
				nextUtility = U.get(nextS);
			}
		} else {
			if (inRemainList(x, y, s)) {
				reward = this.mazeTable[x][y];
				currentCapacity++;
				LinkedList<MyPoint> remainList = updateRemainList(x, y, s);
				nextS = findState(x, y, currentCapacity, remainList);
				nextUtility = U.get(nextS);
			} else {
				if (currentCapacity == this.carryCapacity && inVisit(new MyPoint(s.x, s.y, mazeTable[s.x][s.y]))) {
					reward = 0;
				} else {
					reward = -1;
				}
				nextS = s;
				nextUtility = U.get(nextS);
			}
		}
		Next nextRes = new Next(nextS, reward, nextUtility);
		return nextRes;
	}

	class Next {
		State state;
		Double reward;
		Double utility;

		public Next(State s, Double r, Double u) {
			this.state = s;
			this.reward = r;
			this.utility = u;
		}
	}

}
