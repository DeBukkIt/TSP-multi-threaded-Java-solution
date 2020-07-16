package de.getinit.msg;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StartChallenge {

	/**
	 * Starts the program. args[0] must be the absolute path to input file.
	 * 
	 * @param args the program arguments
	 * @throws FileNotFoundException if something is wrong with the input file 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// Load input file
		InputProcessor proc = new InputProcessor(args[0]);
		List<Node> nodes = proc.readInputFile();

		System.out.println(nodes.size() + " locations loaded. Please wait while shortest path is being searched for...\n");
		
		// Find shortest path
		List<Node> shortestPath = findShortestPath(nodes, 0);

		// Print result
		System.out.println("\nShortest of all paths found. Order of waypoints is: " + listToString(shortestPath));	
	}
	
	private static int[] nodeOrder;

	/**
	 * Finds the shortest path possible between the nodes of the given list where
	 * unsortedNodes.get(startEndNodeIndex) is the first and the last node and all
	 * other nodes are in between.
	 * 
	 * @param unsortedNodes The list of nodes to order so the distance of a path between them all is the shortest possible
	 * @param baseNodeIndex The index of the start and end node
	 * @return the ordered list of nodes so the distance of a path between them all is the shortest possible
	 */
	private static List<Node> findShortestPath(List<Node> unsortedNodes, int baseNodeIndex) {
		// remove start/end node from source list
		List<Node> unsortedNodesWithoutBase = new ArrayList<Node>(unsortedNodes);
		unsortedNodesWithoutBase.remove(baseNodeIndex);

		// find shortest path in between
		// set initial order of nodes
		nodeOrder = new int[unsortedNodesWithoutBase.size()];
		for(int i = 0; i < nodeOrder.length; i++) {
			nodeOrder[i] = i;
		}
		// iterate over all possible orders
		List<Node> shortestPath = new ArrayList<Node>();
		double shortestPathLength = Integer.MAX_VALUE;
		int orderCounter = 0;
		do {
			// delcare path candidate
			List<Node> candidatePath = new ArrayList<Node>(unsortedNodesWithoutBase.size() + 1);
			// interate order rule set, build new candiadate path
			for(int i = 0; i < nodeOrder.length; i++) {
				candidatePath.add(i, unsortedNodesWithoutBase.get(nodeOrder[i]));
			}
			candidatePath.add(0, unsortedNodes.get(baseNodeIndex));
			// check if a new shorter path was found
			double candidatePathLength = pathLengthCircular(candidatePath);
			if(candidatePathLength < shortestPathLength) {
				shortestPath = candidatePath;
				shortestPathLength = candidatePathLength;
				System.out.println("New shortest path found. Length=" + (int) (shortestPathLength/1000) + "km. Path= [via " + listToString(shortestPath) + " back to Base]");
			}
			
			// keep track of progress
			if((orderCounter++ + 1) % (1 * 1000 * 1000) == 0) {
				double progress = (double) orderCounter / (double) fac(nodeOrder.length) * (double) 100;
				System.out.println(new DecimalFormat("0.0000000000000000").format(progress) + "% done.");				
			}
			
		} while(generateNextOrder());
		
		// just add the base node as endNode before returning
		shortestPath.add(unsortedNodes.get(baseNodeIndex));

		return shortestPath;
	}
	
	static int debug = 0;
	
	private static boolean generateNextOrder() {		
		int i = nodeOrder.length - 1;
	    while (i > 0 && nodeOrder[i - 1] >= nodeOrder[i])
	        i--;

	    if (i <= 0)
	        return false;
	    
	    int j = nodeOrder.length - 1;
	    while (nodeOrder[j] <= nodeOrder[i - 1])
	        j--;
		
		swap(nodeOrder, i - 1, j);

		j = nodeOrder.length - 1;
	    while (i < j) {
	        int temp = nodeOrder[i];
	        nodeOrder[i] = nodeOrder[j];
	        nodeOrder[j] = temp;
	        i++;
	        j--;
	    }
		
		return true;
	}
	
	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	private static long fac(long i) {
		if(i == 1) {
			return 1;
		}
		return i * fac(i - 1);
	}

	/**
	 * Calculates the length of the path between the given nodes by summing up the
	 * distances calculated by the nodes themselves. The distance from the last node
	 * to the first node of the given list is also added.
	 * 
	 * @param path List of Nodes
	 * @return the length of the path between the given nodes
	 */
	private static double pathLengthCircular(List<Node> path) {
		double sum = 0.0;
		// sum-up distances between all nodes...
		for (int i = 0; i < path.size(); i++) {
			// ...and from the last one to the first
			sum += path.get(i).distanceTo(path.get((i + 1) % path.size()));
		}
		return sum;
	}
	
	/**
	 * Makes a readable <code>String</code> out of a <code>List</code>
	 * @param list the list to be converted
	 * @return the string representing the list
	 */
	private static String listToString(List<Node> list) {
		StringBuilder b = new StringBuilder("[");
		for(Node n : list) {
			b.append(n.getNumber() + " ");
		}
		return b.toString().trim() + "]";
	}

}
