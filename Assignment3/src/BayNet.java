import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;



public class BayNet {
	private LinkedHashMap<String,Node> allNode = new LinkedHashMap<String,Node>();
	private LinkedHashMap<String,Node> allNode2 = new LinkedHashMap<String,Node>();
	
	private HashMap<String,ArrayList<String>> nodeParameter = new HashMap<String,ArrayList<String>>();
	private HashMap<String,String> premiseMap = new HashMap<String,String>();
	private BufferedReader buffer;
	
	BayNet(String fileName){
		readFile(fileName);
	}
	
	
	public void readFile(String fileName){
		try {
			buffer = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			initialNodes();
			setUpParents();
			setUpCPT();
			buffer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initialNodes(){
		String line = null;
		try {
			line = buffer.readLine();
			while(!line.equals("# Parents")){
				String[] value = line.split(" ");
				String nodeName = value[0];
				
				allNode.put(nodeName, new Node(nodeName));
				allNode2.put(nodeName, new Node(nodeName));
				ArrayList<String> valueList = new ArrayList<String>();
				
				// add all value of this node
				for(int i = 1; i < value.length;i++){
					valueList.add(value[i]);
				}
				nodeParameter.put(nodeName, valueList);
				line = buffer.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setUpParents(){
		String line = null;
		
		try {
			line = buffer.readLine();
			
			while(!line.equals("# Tables")){
				String[] nodes = line.split(" ");
				String childName = nodes[0];
				
				Node childNode = allNode.get(childName);
				Node childNode2 = allNode2.get(childName);
				
				childNode.hasParent = true;
				childNode2.hasParent = true;
				
				for(int i = 1; i < nodes.length; i++){
					String parentName = nodes[i];
					Node parentNode = allNode.get(parentName);
					Node parentNode2 = allNode2.get(parentName);
					childNode.parentNodes.add(parentNode);
					parentNode.childNodes.add(childNode);
					
					childNode2.parentNodes.add(parentNode2);
					parentNode2.childNodes.add(childNode2);
				}
				line = buffer.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setUpCPT(){
		String line = null;
		try {
			line = buffer.readLine();
			
			while(line != null){
				List<String> currentNodeValues = nodeParameter.get(line);
				Node currentNode = allNode.get(line);
				//check if has parent.
				if (currentNode.hasParent) {
					List<Node> allParent = currentNode.parentNodes;
					int numOfParents = allParent.size();
					int numOfLines = 1;
					
					// numOfLines = number of all combination of value of parents.
					for(int i = 0; i < numOfParents; i ++){
						numOfLines *= (nodeParameter.get(allParent.get(i).name)).size();
					}
					
					// read following lines, and saving the table entries.
					for (int i = 0; i < numOfLines; i++) {
						line = buffer.readLine();
						String[] str = line.split(" ");
						String valuesOfParents = "";
						for(int j = 0; j < numOfParents; j++){
							valuesOfParents += str[j];
						}
						int index = 0;
						HashMap<String, String> hashMap = new HashMap<String, String>();
						double percentage = 1;
						
						for(int j = numOfParents; j < str.length; j++){
							percentage -= Double.valueOf(str[j]);
							hashMap.put(currentNodeValues.get(index), str[j]);
							index++;
						}
						hashMap.put(currentNodeValues.get(index), ""+percentage);
						currentNode.parentTable.put(valuesOfParents, hashMap);
					}
				}else{
					line = buffer.readLine();
					String[] percentage = line.split(" ");
					double totalPercentage = 1;
					for(int i = 0; i < percentage.length;i++){
						currentNode.noParentTable.put(currentNodeValues.get(i), percentage[i]);
						totalPercentage -= Double.valueOf(percentage[i]);
					}
					currentNode.noParentTable.put(currentNodeValues.get(percentage.length), ""+totalPercentage);
				}
				line = buffer.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	
