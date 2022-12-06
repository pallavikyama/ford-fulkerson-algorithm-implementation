package com.nasmodel;

public class Flight {

	private String sourceAirport, destinationAirport;
	private int departureTime, arrivalTime, flightCapacity;

	public Flight(String sourceAirport, String destinationAirport, int departureTime, int arrivalTime,
			int flightCapacity) {
		this.sourceAirport = sourceAirport;
		this.destinationAirport = destinationAirport;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.flightCapacity = flightCapacity;
	}

	public String getSourceAirport() {
		return sourceAirport;
	}

	public String getDestinationAirport() {
		return destinationAirport;
	}

	public int getDepartureTime() {
		return convertTime(departureTime);
	}

	public int getArrivalTime() {
		return convertTime(arrivalTime);
	}

	public int getFlightCapacity() {
		return flightCapacity;
	}

	// Considering 6AM on the present day as 0 and 5AM on the next day as 23
	private int convertTime(int time) {
		if (time == 24) {
			time = 0;
		}
		return ((time - 6) >= 0) ? (time - 6) : (time + 18);
	}
}
