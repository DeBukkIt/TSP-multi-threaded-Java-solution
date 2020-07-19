package de.getinit.msg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Processes the input file to use its data for the challenge
 *
 */
public class InputProcessor {

	/**
	 * The input <code>File</code>
	 */
	protected File inputFile;

	/**
	 * Constructs an instance of the InputProcessor class, checking whether the
	 * given input file path leads to an existing file (and not a directory)
	 * 
	 * @param inputFilePath the path to the input file
	 */
	public InputProcessor(String inputFilePath) {
		inputFile = new File(inputFilePath);
		// check if file is OK
		if (!inputFile.exists() || inputFile.isDirectory()) {
			throw new IllegalArgumentException("inputFilePath must exist and not be a directory");
		}
	}

	/**
	 * Read file line by line and construct a <code>Node</code> for every line.
	 * 
	 * @return a list of Nodes, each representing of line of the input file
	 * @throws FileNotFoundException if input file is missing
	 */
	public List<Node> readInputFile() throws FileNotFoundException {
		List<Node> result = new ArrayList<Node>();

		// read input file
		Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), Charset.forName("UTF-8"))));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineParts = line.split(",");

			// skip first line
			if (lineParts[0].equalsIgnoreCase("Nummer")) {
				continue;
			}

			// read other lines and store data in a new Node object,
			// skip line if an error occurs
			try {
				result.add(new Node(lineParts));
			} catch (Exception e) {
				System.err.println("Error reading line from input file: " + line);
			}
		}
		scanner.close();

		return result;
	}

}
