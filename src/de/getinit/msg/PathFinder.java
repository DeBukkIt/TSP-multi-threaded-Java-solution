package de.getinit.msg;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PathFinder {

	/**
	 * Number of workers (= of threads) to use for solving the problem
	 */
	public static int NUM_WORKERS = (Runtime.getRuntime().availableProcessors() + 1) * 2;

	/**
	 * Randomly generated ID of this instance's thread
	 */
	private String ID;
	/**
	 * current order of local nodes to be used
	 */
	private int[] nodeOrder;

	/**
	 * current shortest path found
	 */
	private List<Node> shortestPath;
	/**
	 * length of current shortest path
	 */
	private double shortestPathLength;

	private PathFinder() {
		ID = UUID.randomUUID().toString().substring(0, 4);
		shortestPath = new ArrayList<Node>();
		shortestPathLength = Integer.MAX_VALUE;
	}

	/**
	 * Constructs a PathFinder to do the whole job alone, this is equal to using one
	 * single worker
	 * 
	 * @param nodeCount number of nodes to find the shortest path for
	 */
	public PathFinder(int nodeCount) {
		this();
		// set initial order of nodes
		nodeOrder = new int[nodeCount - 1];
		for (int i = 0; i < nodeOrder.length; i++) {
			nodeOrder[i] = i;
		}
	}

	/**
	 * Constructs a PathFinder with an initial node order (part of the whole
	 * problem) based on the number of workers this PathFinder is one of
	 * 
	 * @param initialNodeOrder the initial lexicographic permutation to start
	 *                         working with
	 */
	public PathFinder(int[] initialNodeOrder) {
		this();
		// load given initial order of nodes
		if (initialNodeOrder == null) {
			throw new IllegalArgumentException("initial order must not be null");
		}
		nodeOrder = initialNodeOrder;
	}

	/**
	 * Finds the shortest path possible between the nodes of the given list where
	 * unsortedNodes.get(startEndNodeIndex) is the first and the last node and all
	 * other nodes are in between. Dependent on the number of workers used, this
	 * method only checks some of the lexicographic permutations possible. The first
	 * permutation to check is the one given to the constructor PathFinder(int[]).
	 * 
	 * @param unsortedNodes The list of nodes to order so the distance of a path
	 *                      between them all is the shortest possible
	 * @param baseNodeIndex The index of the start and end node
	 * @return the ordered list of nodes so the distance of a path between them all
	 *         is the shortest possible
	 * @throws RuntimeException if the size of the given list is differnt from the
	 *                          nodeOrder given in the constructor
	 */
	public void findShortestPath(List<Node> unsortedNodes, int baseNodeIndex) {
		if (unsortedNodes == null || unsortedNodes.size() - 1 != nodeOrder.length) {
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
			for (int i = 0; i < nodeOrder.length; i++) {
				candidatePath.add(i, unsortedNodesWithoutBase.get(nodeOrder[i]));
			}
			candidatePath.add(0, unsortedNodes.get(baseNodeIndex));
			// check if a new shorter path was found
			double candidatePathLength = pathLengthCircular(candidatePath);
			if (candidatePathLength < shortestPathLength) {
				shortestPath = candidatePath;
				shortestPathLength = candidatePathLength;
				System.out.println("[" + ID + "] New thread-local shortest path found. Length=" + (int) (shortestPathLength / 1000)
								+ "km. Path= [" + listToString(shortestPath) + " and back to Base]");
			}

			// keep track of progress
			if ((orderCounter++ + 1) % (1 * 1000 * 1000) == 0) {
				double progress = ((double) orderCounter * (double) 100) / (double) orderCountPerThread;
				System.out.println("[" + ID + "] Thread-local progress: "
						+ new DecimalFormat("0.0000000000000000").format(progress) + "% done.");
			}

		} while (generateNextOrder() && orderCounter < orderCountPerThread + 2);
		// Terminate the loop when all lexicographic permutations of this worker's
		// partition of the problem have benn checked or when there is no next
		// lexicographic permutation

		// just add the base node as endNode before terminating
		shortestPath.add(unsortedNodes.get(baseNodeIndex));
	}

	/**
	 * Generates the next lexicographic permutation based on the current one
	 * represented by the nodeOrder array
	 * 
	 * @return the next permutation in lexicographically order
	 */
	private boolean generateNextOrder() {
		int i = nodeOrder.length - 1;
		while (i > 0 && nodeOrder[i - 1] >= nodeOrder[i]) {
			i--;
		}

		if (i <= 0) {
			return false;
		}

		int j = nodeOrder.length - 1;
		while (nodeOrder[j] <= nodeOrder[i - 1]) {
			j--;
		}

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

	/**
	 * Swaps to elements of an array
	 * 
	 * @param array the array
	 * @param i     the index of the one element
	 * @param j     the index of the other element
	 */
	private void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 * Calculates the faculty of the given number
	 * 
	 * @param i the given number
	 * @return the faculty of the given number
	 */
	private long fac(long i) {
		if (i == 1) {
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
	 * 
	 * @param list the list to be converted
	 * @return the string representing the list
	 */
	private String listToString(List<Node> list) {
		StringBuilder b = new StringBuilder("[");
		for (Node n : list) {
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
