import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

public class BayNet {
	private LinkedHashMap<String, Node> allNode = new LinkedHashMap<String, Node>();
	private LinkedHashMap<String, Node> allNode2 = new LinkedHashMap<String, Node>();
	private Vector<String> names = new Vector<String>();
	private HashMap<String, ArrayList<String>> nodeParameter = new HashMap<String, ArrayList<String>>();
	private BufferedReader buffer;
	private String[] topo;
	private HashMap<String, String> evidenceMap = new HashMap<String, String>();
	private LinkedHashSet<String> problemSet = new LinkedHashSet<String>();

	BayNet(String fileName) {
		readFile(fileName);
	}

	private void readFile(String fileName) {
		try {
			buffer = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			initialNodes();
			setUpParents();
			setUpCPT();
			buffer.close();
			// test
			// testSetUp();
			//
			topo = getTopologicalOrder();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printPercentage(String query) {
		modifyQuery(query);
		System.out.println(getPercentage(query));
		cleanAllNodeValue();
	}

	private String getPercentage(String query) {
		String ret = "";
		double denominator = denominator(topo, 0);

		cleanAllNodeValue();

		String[] problems = problemSet.toArray(new String[0]);

		for (int i = 0; i < problems.length; i++) {
			ArrayList<String> parameters = nodeParameter.get(problems[i]);
			for (int j = 0; j < parameters.size(); j++) {
				evidenceMap.put(problems[i], parameters.get(j));
				double numerator = denominator(topo, 0);
				ret += ("P(" + parameters.get(j) + ") = " + (numerator / denominator));
				if (j != parameters.size() - 1)
					ret += ", ";

				cleanAllNodeValue();
			}
			ret += "\n";
		}

		return ret;
	}

	private void cleanAllNodeValue() {

		for (String key : allNode.keySet())
			allNode.get(key).value = "";
	}

	private double denominator(String[] arr, int index) {
		if (index == arr.length)
			return 1;

		String nodeName = arr[index];

		if (evidenceMap.containsKey(nodeName)) {
			double percentage = 1;
			String value = evidenceMap.get(nodeName);
			Node node = allNode.get(nodeName);
			node.value = value;
			if (node.hasParent) {
				String key = "";
				for (int i = 0; i < node.parentNodes.size(); i++)
					key += node.parentNodes.get(i).value;

				percentage = Double.valueOf(node.parentTable.get(key).get(value));
			} else {
				String possibility = node.noParentTable.get(value);
				percentage = Double.valueOf(possibility);
			}
			return percentage * denominator(arr, index + 1);
		} else {
			double p = 1;
			Node node = this.allNode.get(nodeName);
			List<String> var = nodeParameter.get(nodeName);
			if (node.hasParent) {
				double total = 0;
				String key = "";
				for (int i = 0; i < node.parentNodes.size(); i++) {
					key += node.parentNodes.get(i).value;
				}

				for (int i = 0; i < var.size(); i++) {
					node.value = var.get(i);
					String possibility = node.parentTable.get(key).get(var.get(i));
					p = Double.valueOf(possibility);
					total += p * denominator(arr, index + 1);
				}
				return total;

			} else {
				double total = 0;
				for (int i = 0; i < var.size(); i++) {
					node.value = var.get(i);
					String possibility = node.noParentTable.get(var.get(i));
					p = Double.valueOf(possibility);
					total += p * denominator(arr, index + 1);
				}
				return total;
			}
		}
	}

	private void modifyQuery(String query) {
		evidenceMap = new HashMap<String, String>();
		problemSet = new LinkedHashSet<String>();

		// has the evidence.
		if (query.contains("|")) {
			query = query.replaceAll(" ", "");
			String[] arr = query.split("\\|");

			String problem = arr[0];
			String evidence = arr[1];

			// modify the problem, and set to problem set.

			// has more than 1 problem.
			if (problem.contains(",")) {
				String[] arr2 = problem.split(",");
				for (int i = 0; i < arr2.length; i++) {
					problemSet.add(arr2[i]);
				}
			}
			// only 1 problem.
			else
				problemSet.add(problem);

			// more than 1 evidence
			if (evidence.contains(",")) {
				String[] evidencesArray = evidence.split(",");
				for (int i = 0; i < evidencesArray.length; i++) {
					String[] equationEvidence = evidencesArray[i].split("=");
					evidenceMap.put(equationEvidence[0], equationEvidence[1]);
				}

			} else {
				String[] equationEvidence = evidence.split("=");
				evidenceMap.put(equationEvidence[0], equationEvidence[1]);
			}

		}
		// has no evidence
		else
			// not conditional probably problem.
			problemSet.add(query.replace(" ", ""));

	}

	private String[] getTopologicalOrder() {
		@SuppressWarnings("unchecked")
		Vector<String> name = (Vector<String>) names.clone();

		ArrayList<String> result = new ArrayList<String>();
		getTopologicalOrder(name, result);

		String[] ret = new String[result.size()];
		ret = result.toArray(ret);

		return ret;
	}

	private void getTopologicalOrder(Vector<String> names, ArrayList<String> result) {
		if (names.size() == 0) {
			return;
		}

		for (int i = 0; i < names.size(); i++) {
			String nodeName = names.get(i);
			Node node = allNode2.get(nodeName);

			if (node.parentNodes.size() == 0) {
				result.add(nodeName);
				for (int j = 0; j < node.childNodes.size(); j++) {
					Node childNode = node.childNodes.get(j);
					for (int index = 0; index < childNode.parentNodes.size(); index++) {
						if (childNode.parentNodes.get(i).name.equals(nodeName))
							childNode.parentNodes.remove(i);
					}
				}
				names.remove(i);
				getTopologicalOrder(names, result);
				break;
			}
		}
	}

	private void initialNodes() {
		String line = null;
		try {
			line = buffer.readLine();
			while (!line.equals("# Parents")) {
				String[] value = line.split(" ");
				String nodeName = value[0];
				names.add(nodeName);
				allNode.put(nodeName, new Node(nodeName));
				allNode2.put(nodeName, new Node(nodeName));
				ArrayList<String> valueList = new ArrayList<String>();

				// add all value of this node
				for (int i = 1; i < value.length; i++) {
					valueList.add(value[i]);
				}
				nodeParameter.put(nodeName, valueList);
				line = buffer.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setUpParents() {
		String line = null;

		try {
			line = buffer.readLine();

			while (!line.equals("# Tables")) {
				String[] nodes = line.split(" ");
				String childName = nodes[0];

				Node childNode = allNode.get(childName);
				Node childNode2 = allNode2.get(childName);

				childNode.hasParent = true;
				childNode2.hasParent = true;

				for (int i = 1; i < nodes.length; i++) {
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
			e.printStackTrace();
		}
	}

	private void setUpCPT() {
		String line = null;
		try {
			line = buffer.readLine();

			while (line != null) {
				List<String> currentNodeValues = nodeParameter.get(line);
				Node currentNode = allNode.get(line);
				// check if has parent.
				if (currentNode.hasParent) {
					List<Node> allParent = currentNode.parentNodes;
					int numOfParents = allParent.size();
					int numOfLines = 1;

					// numOfLines = number of all combination of value of
					// parents.
					for (int i = 0; i < numOfParents; i++) {
						numOfLines *= (nodeParameter.get(allParent.get(i).name)).size();
					}

					// test
					// System.out.println(currentNode.name + " : following
					// "+numOfLines+" lines.");
					//

					// read following lines, and saving the table entries.
					for (int i = 0; i < numOfLines; i++) {
						line = buffer.readLine();
						String[] str = line.split(" ");
						String valuesOfParents = "";
						for (int j = 0; j < numOfParents; j++) {
							valuesOfParents += str[j];
						}
						int index = 0;
						HashMap<String, String> hashMap = new HashMap<String, String>();
						double percentage = 1;

						for (int j = numOfParents; j < str.length; j++) {
							percentage -= Double.valueOf(str[j]);
							hashMap.put(currentNodeValues.get(index), str[j]);
							// test
							// System.out.println(currentNodeValues.get(index)+"
							// "+str[j]);
							//
							index++;
						}
						hashMap.put(currentNodeValues.get(index), "" + percentage);

						// test
						// System.out.println(currentNodeValues.get(index)+"
						// "+percentage);
						//

						currentNode.parentTable.put(valuesOfParents, hashMap);

						// test
						// System.out.println(valuesOfParents);
						//
					}
				} else {
					line = buffer.readLine();
					String[] percentage = line.split(" ");
					double totalPercentage = 1;
					for (int i = 0; i < percentage.length; i++) {
						currentNode.noParentTable.put(currentNodeValues.get(i), percentage[i]);
						totalPercentage -= Double.valueOf(percentage[i]);
					}
					currentNode.noParentTable.put(currentNodeValues.get(percentage.length), "" + totalPercentage);
				}
				line = buffer.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void testSetUp() {
		// test parameter
		System.out.println("--------------------------");
		System.out.println("Print parameter of each node.");
		System.out.println("--------------------------");
		for (int i = 0; i < names.size(); i++) {
			System.out.println(names.get(i) + "'s parameter: ");
			ArrayList<String> parameter = nodeParameter.get(names.get(i));
			for (int j = 0; j < parameter.size(); j++) {
				System.out.print(" " + parameter.get(j));
			}
			System.out.println("\n");
		}

		// test parents

		System.out.println("--------------------------");
		System.out.println("Print relation of each node.");
		System.out.println("--------------------------");
		for (int i = 0; i < names.size(); i++) {
			// print parents.
			System.out.println(names.get(i) + "'s parents: ");
			Node currentNode = allNode.get(names.get(i));
			List<Node> parents = currentNode.parentNodes;
			if (currentNode.hasParent) {
				for (int j = 0; j < parents.size(); j++) {
					System.out.print(" " + parents.get(j).name);
				}
			}
			System.out.println("\n");

			// print children.
			System.out.println(names.get(i) + "'s children: ");
			List<Node> children = currentNode.childNodes;
			if (parents != null) {
				for (int j = 0; j < children.size(); j++) {
					System.out.print(" " + children.get(j).name);
				}
			}
			System.out.println("\n");

			// print each percentage of tables.
			System.out.println("--------------------------");
			System.out.println("Print percentage of each node.");
			System.out.println("--------------------------");
		}

	}
}
