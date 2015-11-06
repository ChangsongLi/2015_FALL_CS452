/**
 * Driver class for Bay Net class.
 * 
 * @author Changsong Li
 */

import java.util.Scanner;

public class Driver {

	public static void main(String[] args) {
		Driver d = new Driver(args[0]);

	}

	Driver(String fileName) {
		BayNet bn = new BayNet(fileName);
		Scanner scan = new Scanner(System.in);

		System.out.println("Loading file " + fileName + ".\n");
		;

		String query = scan.nextLine();

		while (!query.equals("quit")) {
			bn.printPercentage(query);
			query = scan.nextLine();
		}
		scan.close();
	}

}
