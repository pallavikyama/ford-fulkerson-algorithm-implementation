/**
 * 
 */
package com.nasmodel;

import java.nio.file.*;
import java.util.*;

/**
 * @author pkyama
 *
 */
public class MaxFlowNAS {

	/**
	 * 
	 */

	public static void main(String[] args) {
		try {

			// 10 valid Airports of NAS, with the first and last as source and destination
			ArrayList<String> validAirports = new ArrayList<String>(
					Arrays.asList("LAX", "SFO", "PHX", "SEA", "DEN", "ATL", "ORD", "BOS", "IAD", "JFK"));

			// Scanning the entire data from flights.txt
			List<String> fileData = new ArrayList<String>();
			ArrayList<Flight> schedule = new ArrayList<Flight>();

			String filePath = "C:/Users/pkyama/eclipse-workspace/ford-fulkerson-algorithm-implementation/"
					+ "NationalAirspaceSystem/src/com/dataset/flights.txt";
			fileData = Files.readAllLines(Paths.get(filePath));

			fileData.forEach(eachFlight -> {
				String[] flightData = eachFlight.split("\\s+");

				// Converted all flight timings in Flight.java to 0-23 hour syntax
				// Considering 6AM on the present day as 0 and 5AM on the next day as 23
				schedule.add(new Flight(flightData[0], flightData[1], Integer.parseInt(flightData[2]),
						Integer.parseInt(flightData[3]), Integer.parseInt(flightData[4])));
			});

			// Removing itineraries of stops not a part of valid list of NAS airports
			// Also removing the itineraries which don't return within the 24-hour period
			schedule.removeIf(flightData -> (!validAirports.contains(flightData.getSourceAirport())
					|| !validAirports.contains(flightData.getDestinationAirport())
					|| flightData.getDepartureTime() > flightData.getArrivalTime()));

			/*for (Flight flightData : schedule) {
				// Printing the valid schedule before applying the algorithm
				System.out.println(flightData.getSourceAirport() + "\t" + flightData.getDestinationAirport() + "\t"
						+ flightData.getDepartureTime() + "\t" + flightData.getArrivalTime() + "\t"
						+ flightData.getFlightCapacity());
			}*/

			// Creating a graph of all the valid air-routes
			int graphSize = ((validAirports.size() - 2) * 24) + 2;
			Graph g = new Graph(graphSize);

			int singleHopFlow = 0;
			for (Flight flightData : schedule) {
				int s = getAirportCode(flightData.getSourceAirport(), flightData.getDepartureTime(), validAirports);
				int t = getAirportCode(flightData.getDestinationAirport(), flightData.getArrivalTime(), validAirports);
				if (s == 0 && t == graphSize - 1) {
					// Appending single-hop capacities
					singleHopFlow += flightData.getFlightCapacity();
				} else {
					// Appending multi-hop capacities
					g.getEdgeCapacity()[s][t] += flightData.getFlightCapacity();
				}
			}

			// Since there is no possibility of travel within the same airport
			for (int i = 1; i < graphSize - 1; i++) {
				for (int j = 1; j < graphSize - 1; j++) {
					if (i < j) {
						for (int k = 0; k < validAirports.size() - 2; k++) {
							if ((i > (24 * k) && j > (24 * k)) && (i <= (24 * (k + 1)) && j <= (24 * (k + 1))))
								g.removeNoTrip(i, j);
						}
					}
				}
			}

			String source = validAirports.get(0), destination = validAirports.get(validAirports.size() - 1);
			System.out.println("The largest possible capacity of passengers that can travel between " + source + " to "
					+ destination + " are: " + (singleHopFlow + fordFulkerson(g, 0, graphSize - 1)));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int getAirportCode(String airport, int time, ArrayList<String> validAirports) {

		int id = validAirports.indexOf(airport);

		if (id == 0) {
			return 0;
		} else if (id == validAirports.size() - 1) {
			return ((validAirports.size() - 2) * 24) + 1;
		} else {
			return ((id - 1) * 24) + time + 1;
		}
	}

	private static int fordFulkerson(Graph g, int src, int dstn) {
		if (src == dstn)
			return 0;

		// Generating Residual Graph
		Graph rg = new Graph(g.getVertexCount());
		int i, j;
		for (i = 0; i < g.getVertexCount(); i++) {
			for (j = 0; j < g.getVertexCount(); j++) {
				rg.addTrip(i, j, g.getEdgeCapacity()[i][j]);
			}
		}

		// Path found by Breadth First Search (BFS) traversal of the graph
		int[] origin = new int[g.getVertexCount()];

		int maxFlow = 0;

		while (bfs(rg, src, dstn, origin)) {
			int pathFlow = Integer.MAX_VALUE;

			// Finding maximum flow for the path found by BFS
			for (j = dstn; j != src; j = origin[j]) {
				i = origin[j];
				pathFlow = Math.min(pathFlow, rg.getEdgeCapacity()[i][j]);
			}
			// Updating capacities in the residual graph
			for (j = dstn; j != src; j = origin[j]) {
				i = origin[j];
				rg.getEdgeCapacity()[i][j] -= pathFlow; // forward path
				rg.getEdgeCapacity()[j][i] += pathFlow; // backward path
			}

			maxFlow += pathFlow;
		}

		return maxFlow;
	}

	private static boolean bfs(Graph rg, int src, int dstn, int[] origin) {

		boolean[] labelled = new boolean[rg.getVertexCount()];
		for (int i = 0; i < rg.getVertexCount(); i++) {
			labelled[i] = false;
		}

		LinkedList<Integer> path = new LinkedList<Integer>();

		// Starting journey from the source
		path.add(src);
		labelled[src] = true;
		origin[src] = -1;

		// Traversing through all the vertices
		while (!path.isEmpty()) {
			int head = path.poll();

			// Finding augmenting paths
			for (Integer tail : rg.findConnections(head)) {
				// if vertex is not labeled and has residual capacity
				if (labelled[tail] == false && rg.getEdgeCapacity()[head][tail] > 0) {
					path.add(tail);
					labelled[tail] = true;
					origin[tail] = head;
				}
			}
		}

		// Finished if we have arrived at the destination
		return labelled[dstn];
	}
}
