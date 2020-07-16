package de.getinit.msg;

/**
 * Entity that represents a node. It can calculate the direct distance to
 * another node based on their coordinates.
 *
 */
public class Node {

	protected int number;
	protected String name, street, housenumber, postalCode, city;
	protected double lat, lng;

	/**
	 * Constructs a node.
	 * 
	 * @param lineParts an array of Strings with pieces of information from the
	 *                  input file
	 */
	public Node(String[] lineParts) {
		this(Integer.parseInt(lineParts[0]), lineParts[1], lineParts[2], lineParts[3], lineParts[4], lineParts[5],
				Double.parseDouble(lineParts[6]), Double.parseDouble(lineParts[7]));
	}

	/**
	 * Constructs a node from the pieces of information from the input file
	 * 
	 * @param number
	 * @param name
	 * @param street
	 * @param housenumber
	 * @param postalCode
	 * @param city
	 * @param lat
	 * @param lng
	 */
	public Node(int number, String name, String street, String housenumber, String postalCode, String city, Double lat,
			Double lng) {
		this.number = number;
		this.name = name;
		this.street = street;
		this.housenumber = housenumber;
		this.postalCode = postalCode;
		this.city = city;
		this.lat = lat;
		this.lng = lng;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHousenumber() {
		return housenumber;
	}

	public void setHousenumber(String housenumber) {
		this.housenumber = housenumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	/**
	 * Uses the Halversine method to calculate the direct distance between to
	 * locations on this planet based on latitude and longitude between this node an
	 * another
	 * 
	 * @param otherLat the latitude of the other node
	 * @param otherLng the longitude of the other node
	 * @return the distance between the nodes in meters
	 */
	public double distanceTo(double otherLat, double otherLng) {
		final int R = 6371; // globe radius for planet 'earth'
		double latDistance = Math.toRadians(otherLat - lat);
		double lonDistance = Math.toRadians(otherLng - lng);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat))
				* Math.cos(Math.toRadians(otherLat)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // unit conversation (km to m)
		return Math.sqrt(Math.pow(distance, 2)); // pythagorean theorem
	}

	/**
	 * Uses the Halversine method to calculate the direct distance between to
	 * locations on this planet based on latitude and longitude between this node an
	 * another
	 * 
	 * @param otherNode The other node
	 * @return the distance between the nodes in meters
	 */
	public double distanceTo(Node otherNode) {
		return distanceTo(otherNode.getLat(), otherNode.getLng());
	}

	@Override
	public String toString() {
		return "[" + name + " @ " + lat + "|" + lng + "]";
	}

}
