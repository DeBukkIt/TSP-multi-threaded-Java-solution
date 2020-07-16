package de.getinit.msg;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
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
		System.out.println("\nShortest of all paths found. Order of waypoints is:");
		for (Node n : shortestPath) {
			System.out.println(n);
		}
	}

	/**
	 * Finds the shortest path possible between the nodes of the given list where
	 * unsortedNodes.get(startEndNodeIndex) is the first and the last node and all
	 * other nodes are in between.
	 * 
	 * @param unsortedNodes The list of nodes to order so the distance of a path between them all is the shortest possible
	 * @param startEndNodeIndex The index of the start and end node
	 * @return the ordered list of nodes so the distance of a path between them all is the shortest possible
	 */
	private static List<Node> findShortestPath(List<Node> unsortedNodes, int startEndNodeIndex) {
		// remove start/end node from source list
		List<Node> unsortedNodesWithoutStartEnd = new ArrayList<Node>(unsortedNodes);
		unsortedNodesWithoutStartEnd.remove(startEndNodeIndex);

		// try random solutions, find shortest of them
		List<Node> shortestPath = null;
		double shortestCircleLength = Double.MAX_VALUE;

		for (long l = 0; l < Long.MAX_VALUE; l++) {
			List<Node> candidatePath = new ArrayList<Node>(unsortedNodesWithoutStartEnd);
			// randomize
			Collections.shuffle(candidatePath);
			candidatePath.add(0, unsortedNodes.get(startEndNodeIndex));
			// measure
			if (pathLengthCircular(candidatePath) < shortestCircleLength) {
				// if new shortest found:
				shortestPath = candidatePath;
				shortestCircleLength = pathLengthCircular(candidatePath);
				System.out.println("New shortest path is " + (int) (shortestCircleLength / 1000) + " km short: " + listToString(shortestPath));
			}
		}

		// just add the end node before returning
		shortestPath.add(unsortedNodes.get(startEndNodeIndex));

		return shortestPath;
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
