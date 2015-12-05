/**
 * AI assignment 5 ReinforceLearning
 * @author changsongli
 *
 */
public class IceWorld {

	public static void main(String[] args) {
		QLearning q = new QLearning(args[0]);
		SARSA s = new SARSA(args[0]);
	}

}
