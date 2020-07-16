Result
------
The shortest path is 3156 km short.
The path is [1 19 20 4 21 8 7 6 15 10 11 13 14 18 3 2 5 9 17 16 12 1].

About this algorithm
--------------------
Since there is no non-heuristic, optimal solution for the problem of the travelling salesman, my algorithm tries an alternative approach: There are 20! possibilities to arrange all locations except the main location. My optimistic-heuristic solution tries about 3.79 times 20! random chosen possibilities. So there is a good chance to find the optimal solution with every execution of the program. It just takes a while.

How to run
----------
In the bin directory you'll find class files precompiled for Java v1.8. The main class is de.getinit.msg.StartChallenge. The entry point excepts one argument: The path to the input csv-file. Run the following command from within the bin directory:
java de.getinit.msg.StartChallenge <PATH_TO_CSV_INPUT_FILE>
