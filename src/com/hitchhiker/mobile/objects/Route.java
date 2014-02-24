package com.hitchhiker.mobile.objects;

import java.util.List;

public class Route {
	
	private String id;
	private String authorId;
	private String authorName;
	private String routeFrom;
	private String routeTo;
	private Long distance;
	private Double price;
	private String departureTime;
	private String departureDate;
	private int availableSeats;
	private List<String> passengers;
	private double latFrom;
	private double lngFrom;
	private double latTo;
	private double lngTo;
	
	public Route() {
	}
	
	public Route(String id) {
		this.id = id;
	}
	
	public Route(String id, String authorId) {
		this.id = id;
		this.authorId = authorId;
	}
	
	public Route(String id, String routeFrom, String routeTo) {
		this.id = id;
		this.routeFrom = routeFrom;
		this.routeTo = routeTo;
	}
	
	public Route(String id, String routeFrom, String routeTo, String authorId) {
		this.id = id;
		this.routeFrom = routeFrom;
		this.routeTo = routeTo;
		this.authorId = authorId;
	}
	
	public Route(String id, String routeFrom, String routeTo, String authorId, String authorName) {
		this.id = id;
		this.routeFrom = routeFrom;
		this.routeTo = routeTo;
		this.authorId = authorId;
		this.authorName = authorName;
	}
	
	public synchronized Route setId(String id) {
		this.id = id;
		return this;
	}
	
	public synchronized Route setAuthorId(String authorId) {
		this.authorId = authorId;
		return this;
	}
	
	public synchronized Route setAuthorName(String authorName) {
		this.authorName = authorName;
		return this;
	}
	
	public synchronized Route setRouteFrom(String routeFrom) {
		this.routeFrom = routeFrom;
		return this;
	}
	
	public synchronized Route setRouteTo(String routeTo) {
		this.routeTo = routeTo;
		return this;
	}
	
	public synchronized Route setDistance(Long distance) {
		this.distance = distance;
		return this;
	}
	
	public synchronized Route setPrice(Double price) {
		this.price = price;
		return this;
	}
	
	public synchronized Route setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
		return this;
	}
	
	public synchronized Route setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
		return this;
	}
	
	public synchronized Route setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
		return this;
	}
	
	public synchronized Route setPassengers(List<String> passengers) {
		this.passengers = passengers;
		return this;
	}
	
	public synchronized Route setLatitudeFrom(double lat) {
		this.latFrom = lat;
		return this;
	}
	
	public synchronized Route setLatitudeTo(double lat) {
		this.latTo = lat;
		return this;
	}
	
	public synchronized Route setLongitudeFrom(double lng) {
		this.lngFrom = lng;
		return this;
	}
	
	public synchronized Route setLongitudeTo(double lng) {
		this.lngTo = lng;
		return this;
	}
	
	public synchronized String getId() {
		return this.id;
	}
	
	public synchronized String getAuthorId() {
		return this.authorId;
	}
	
	public synchronized String getAuthorName() {
		return this.authorName;
	}
	
	public synchronized String getRouteFrom() {
		return this.routeFrom;
	}
	
	public synchronized String getRouteTo() {
		return this.routeTo;
	}
	
	public synchronized Long getDistance() {
		return this.distance;
	}
	
	public synchronized Double getPrice() {
		return this.price;
	}
	
	public synchronized String getDepartureTime() {
		return this.departureTime;
	}
	
	public synchronized String getDepartureDate() {
		return this.departureDate;
	}
	
	public synchronized int getAvailableSeats() {
		return this.availableSeats;
	}
	
	public synchronized List<String> getPassengers() {
		return this.passengers;
	}
	
	public synchronized Double getLatitudeFrom() {
		return this.latFrom;
	}
	
	public synchronized Double getLatitudeTo() {
		return this.latTo;
	}
	
	public synchronized Double getLongitudeFrom() {
		return this.lngFrom;
	}
	
	public synchronized Double getLongitudeTo() {
		return this.lngTo;
	}
}