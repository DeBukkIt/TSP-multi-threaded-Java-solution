package de.getinit.msg;

import java.io.FileNotFoundException;
import java.util.Arrays;
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
		final List<Node> nodes = proc.readInputFile();

		System.out.println(nodes.size() + " locations loaded. Please wait while shortest path is being searched for...\n");
		
		// Init workers for multithreading
		// every worker should work one thread
		// finding shortest paths on 1/NUM_WORKERS of all permutations
		if(PathFinder.NUM_WORKERS > nodes.size()/2 + 1) {
			PathFinder.NUM_WORKERS = nodes.size()/2 + 1;
			System.out.println("Too many cores for too few nodes, reduced number of workers to " + PathFinder.NUM_WORKERS);
		}
		PathFinder[] workers = new PathFinder[PathFinder.NUM_WORKERS];
		// for every worker...
		for(int i = 0; i < workers.length; i++) {
			// ... init the default order array ...
			int[] initialOrder = new int[nodes.size() - 1];
			for(int k = 1; k < initialOrder.length; k++) {
				initialOrder[k] = k;
			}
			// ... but choose another element for index=0 based on number of workers available.
			int quot = initialOrder.length / PathFinder.NUM_WORKERS;
			int[] finalOrder = new int[initialOrder.length];
			finalOrder[0] = initialOrder[i*quot];
			for(int m = 1; m < initialOrder.length; m++) {
				if(m <= i*quot) {
					finalOrder[m] = initialOrder[m-1];
				} else {
					finalOrder[m] = initialOrder[m];
				}
			}
			
			System.out.println("Init worker #" + i + " with order " + Arrays.toString(finalOrder));
			workers[i] = new PathFinder(finalOrder);
		}
		
		// Find shortest paths using one thread per worker
		Thread[] threads = new Thread[workers.length];
		for(int i = 0; i < workers.length; i++) {
			final int j = i;
			threads[i] = new Thread(() -> workers[j].findShortestPath(nodes, 0));
			threads[i].start();
		}
		
		// Wait for all threads to finish their jobs
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		List<Node> globalShortestPath = null;
		double globalShortestPathLength = Double.MAX_VALUE;
		
		for(PathFinder w : workers) {
			if(w.getShortestPathLength() < globalShortestPathLength) {
				globalShortestPathLength = w.getShortestPathLength();
				globalShortestPath = w.getShortestPath();
			}
		}
		
		// Print result
		System.out.println("\nShortest of all paths found. Length=" + globalShortestPathLength + ", order of waypoints is: " + listToString(globalShortestPath));	
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
