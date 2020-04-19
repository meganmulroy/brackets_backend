package com.techelevator.sports.model;

public class Sport {
	
	private int sportId;
	private String sportName;
	
	public int getId() {
		return sportId;
	}
	public void setId(int sportId) {
		this.sportId = sportId;
	}
	public String getName() {
		return sportName;
	}
	public void setName(String sportName) {
		this.sportName = sportName;
	}
}