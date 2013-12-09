package com.hitchhiker.mobile.objects;

public class Route {
	
	private String id;
	private String creator;
	private String routeFrom;
	private String routeTo;
	private Long distance;
	private Double price;
	private String departureTime;
	private String departureDate;
	private int availableSeats;
	
	public Route() {
	}
	
	public Route(String id) {
		this.id = id;
	}
	
	public Route(String id, String creator) {
		this.id = id;
		this.creator = creator;
	}
	
	public Route(String id, String routeFrom, String routeTo) {
		this.id = id;
		this.routeFrom = routeFrom;
		this.routeTo = routeTo;
	}
	
	public synchronized Route setId(String id) {
		this.id = id;
		return this;
	}
	
	public synchronized Route setUser(String creator) {
		this.creator = creator;
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
	
	public synchronized String getId() {
		return this.id;
	}
	
	public synchronized String getUser() {
		return this.creator;
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
}