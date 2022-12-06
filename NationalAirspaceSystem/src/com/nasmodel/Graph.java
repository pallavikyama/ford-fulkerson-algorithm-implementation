package com.nasmodel;

import java.util.ArrayList;

public class Graph {

	private int vertexCount;
	private int[][] edgeCapacity;

	public int getVertexCount() {
		return vertexCount;
	}

	public int[][] getEdgeCapacity() {
		return edgeCapacity;
	}

	public Graph(int vertexCount) {
		this.vertexCount = vertexCount;
		edgeCapacity = new int[vertexCount][vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			for (int j = 0; j < vertexCount; j++) {
				edgeCapacity[i][j] = 0;
			}
		}
	}

	public void addTrip(int src, int dstn, int capacity) {
		edgeCapacity[src][dstn] = capacity;
	}

	public void removeNoTrip(int src, int dstn) {
		edgeCapacity[src][dstn] = Integer.MAX_VALUE;
	}

	public boolean hasTrip(int src, int dstn) {
		return (edgeCapacity[src][dstn] != 0) ? true : false;
	}

	public ArrayList<Integer> findConnections(int vertex) {
		ArrayList<Integer> connections = new ArrayList<Integer>();
		for (int i = 0; i < vertexCount; i++) {
			if (hasTrip(vertex, i))
				connections.add(i);
		}
		return connections;
	}
}
