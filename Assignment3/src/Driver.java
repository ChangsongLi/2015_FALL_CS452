import java.util.Scanner;

public class Driver {

	public static void main(String[] args) {
		Driver d = new Driver(args[0]);

	}
	
	Driver(String fileName){
		BayNet bn = new BayNet(fileName);
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Loading file "+fileName+".\n");;
		
		String query = scan.next();
		
		while(!query.equals("quit")){
			
			query = scan.next();
		}
		scan.close();
	}

}
