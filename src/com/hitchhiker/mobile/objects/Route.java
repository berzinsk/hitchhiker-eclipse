package com.hitchhiker.mobile.objects;

public class Route {
	
	private Long id;
	private String user;
	private String routeFrom;
	private String routeTo;
	private Long distance;
	private Double price;
	private String departureTime;
	
	public Route() {
	}
	
	public Route(Long id) {
		this.id = id;
	}
	
	public Route(Long id, String user) {
		this.id = id;
		this.user = user;
	}
	
	public synchronized Route setId(Long id) {
		this.id = id;
		return this;
	}
	
	public synchronized Route setUser(String user) {
		this.user = user;
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
	
	public synchronized Long getId() {
		return this.id;
	}
	
	public synchronized String getUser() {
		return this.user;
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
}