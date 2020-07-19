# TSP Multi-threaded Solution

## Result
The program is still running.
The shortest path found yet is 3156 km short.
That path is [1 19 20 4 21 8 7 6 15 10 11 13 14 18 3 2 5 9 17 16 12 1].

## About the algorithm used
Since there is no non-heuristic  _and_  optimal solution for the problem of the travelling salesman, my algorithm tries an alternative approach: All 20! possibilities to arrange all locations except the main location are tested. So it  _will_  reliably find the optimal solution. It just takes a while. To improve the speed a bit, the algorithm was designed multi-threaded, so many processors mean a faster result.

## How to run
In the bin directory you'll find class files pre-compiled for Java 8 (1.8). The main class is de.getinit.msg.StartChallenge. The entry point expects one argument: The path to the input csv-file. Run the following command from  _within_  the bin directory:
`java de.getinit.msg.StartChallenge <PATH_TO_CSV_INPUT_FILE>`
or run the program like you run every other java program.
