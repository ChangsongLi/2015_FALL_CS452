import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node {
	String name; //node's name
	String value; //node's state Ex: T || F 
	List<Node> parentNodes = new ArrayList<Node>(); // all parent nodes
	List<Node> childNodes = new ArrayList<Node>(); // all children nodes
	Boolean hasParent = false; //T - has parent F - no parent
	
	// 2 HashMap for #tables, use only one depending on has parent or not. 
	HashMap<String,HashMap<String,String>> parentTable = new HashMap<String,HashMap<String,String>>();
	HashMap<String,String>noParentTable = new HashMap<String,String>();
	
	Node(String name){
		this.name = name;
	}
	
	
}
