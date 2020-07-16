package de.getinit.msg;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PathFinder {
	
	public static int NUM_WORKERS = (Runtime.getRuntime().availableProcessors() + 1) * 2;
	
	private String ID;
	private int[] nodeOrder;
	
	private List<Node> shortestPath;
	private double shortestPathLength;
	
	private PathFinder() {
		ID = UUID.randomUUID().toString().substring(0, 4);
		shortestPath = new ArrayList<Node>();
		shortestPathLength = Integer.MAX_VALUE;
	}
	
	public PathFinder(int nodeCount) {
		this();
		// set initial order of nodes
		nodeOrder = new int[nodeCount - 1];
		for(int i = 0; i < nodeOrder.length; i++) {
			nodeOrder[i] = i;
		}
	}
	
	public PathFinder(int[] initialNodeOrder) {
		this();
		// load given initial order of nodes
		if(initialNodeOrder == null) {
			throw new IllegalArgumentException("initial order must not be null");
		}
		nodeOrder = initialNodeOrder;
	}

	/**
	 * Finds the shortest path possible between the nodes of the given list where
	 * unsortedNodes.get(startEndNodeIndex) is the first and the last node and all
	 * other nodes are in between.
	 * 
	 * @param unsortedNodes The list of nodes to order so the distance of a path between them all is the shortest possible
	 * @param baseNodeIndex The index of the start and end node
	 * @return the ordered list of nodes so the distance of a path between them all is the shortest possible
	 * @throws Exception 
	 */
	public void findShortestPath(List<Node> unsortedNodes, int baseNodeIndex) {
		if(unsortedNodes == null || unsortedNodes.size() -1 != nodeOrder.length) {
			throw new RuntimeException("nodeOrder and nodes must be of same length");
		}
		
		// set max num of counts to make
		long orderCountPerThread = (fac(unsortedNodes.size() - 1) / NUM_WORKERS) + 1;
		
		// remove start/end node from source list
		List<Node> unsortedNodesWithoutBase = new ArrayList<Node>(unsortedNodes);
		unsortedNodesWithoutBase.remove(baseNodeIndex);

		// find shortest path in between
		// iterate over all possible orders
		long orderCounter = 0;
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
				System.out.println("[" + ID + "] New thread-local shortest path found. Length=" + (int) (shortestPathLength/1000) + "km. Path= [" + listToString(shortestPath) + " and back to Base]");
			}
			
			// keep track of progress
			if((orderCounter++ + 1) % (1 * 1000 * 1000) == 0) {
				// TODO Fix double/long overflow error
				double progress = (double) orderCounter / (double) orderCountPerThread * (double) 100;
				System.out.println("[" + ID + "] Thread-local progress: " + new DecimalFormat("0.0000000000000000").format(progress) + "% done.");				
			}
			
		} while(generateNextOrder() && orderCounter < orderCountPerThread + 2);
		
		// just add the base node as endNode before returning
		shortestPath.add(unsortedNodes.get(baseNodeIndex));
	}
	
	private boolean generateNextOrder() {		
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
	
	private void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	private long fac(long i) {
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
	private double pathLengthCircular(List<Node> path) {
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
	private String listToString(List<Node> list) {
		StringBuilder b = new StringBuilder("[");
		for(Node n : list) {
			b.append(n.getNumber() + " ");
		}
		return b.toString().trim() + "]";
	}
	
	public double getShortestPathLength() {
		return shortestPathLength;
	}
	
	public List<Node> getShortestPath() {
		return shortestPath;
	}

}
