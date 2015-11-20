import java.util.LinkedHashSet;

public class MazeMDP {

	LinkedHashSet<State> set = new LinkedHashSet<State>();

	public LinkedHashSet<State> getStates() {
		return set;
	}

	public void addState(State s) {
		set.add(s);
	}
}
