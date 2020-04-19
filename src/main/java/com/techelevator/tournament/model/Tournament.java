package com.techelevator.tournament.model;

import java.time.LocalDate;
import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Tournament
 */
public class Tournament {
	private long id;

	@NotBlank(message = "Tournament name is required")
	private String name;

	@NotBlank(message = "Description is required")
	private String description;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Future
	private LocalDate startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Future
	private LocalDate endDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Future
	private LocalDate regDeadline;

	@NotBlank(message = "Sport ID is required")
	private int sportId;

	@NotBlank(message = "Type is required")
	private String type;

	@NotBlank(message = "Participant Max is required")
	@Range(min = 1, max = 64, message = "Maximum of 64 participants!")
	private int participantMax;

	private String status;

	private String sportName;

	private String results;

	private String response;

	private int organizer;

	private long spotsLeft;
	
	private String participate;

	private boolean bracketGenerated;

	public String getParticipate() {
		return participate;
	}

	public boolean isBracketGenerated() {
		return bracketGenerated;
	}

	public void setBracketGenerated(boolean bracketGenerated) {
		this.bracketGenerated = bracketGenerated;
	}

	public void setParticipate(String participate) {
		this.participate = participate;
	}

	public long getSpotsLeft() {
		return spotsLeft;
	}

	public void setSpotsLeft(long spotsLeft) {
		this.spotsLeft = spotsLeft;
	}

	public int getOrganizer() {
		return organizer;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public void setOrganizer(int organizer) {
		this.organizer = organizer;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public String getSportName() {
		return sportName;
	}

	public void setSportName(String sportName) {
		this.sportName = sportName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public LocalDate getRegDeadline() {
		return regDeadline;
	}

	public void setRegDeadline(LocalDate regDeadline) {
		this.regDeadline = regDeadline;
	}

	public int getSportId() {
		return sportId;
	}

	public void setSportId(int sportId) {
		this.sportId = sportId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getParticipantMax() {
		return participantMax;
	}

	public void setParticipantMax(int participantMax) {
		this.participantMax = participantMax;
	}

	public String getStatus() {
		LocalDate currentDate = LocalDate.now();
//		System.out.println(startDate);

		if (currentDate.isBefore(startDate)) {
			if (spotsLeft == 0) {
				this.status = "Full";
			} else {
				this.status = "Open";
			}
		} else if (((currentDate.isAfter(startDate) || currentDate.equals(startDate))
				&& (currentDate.isBefore(endDate) || currentDate.equals(endDate)))) {
			this.status = "In Progress";
		} else {
			this.status = "Closed";
		}

		return status;

	}

	public void setStatus(String status) {
		this.status = status;
	}

//	public void setStatusCount(Date startDate, Date endDate, int currentPlayers) { 
//		Date currentDate = new Date();
////		System.out.println(startDate);
//		
//		if (currentDate.before(startDate) && currentPlayers > 0) { 
//			this.status = "Open";
//		}
//		else if (((currentDate.after(startDate) || currentDate.equals(startDate)) && (currentDate.before(endDate) || currentDate.equals(endDate)))) { 
//			this.status = "In Progress";
//		} else { 
//			this.status = "Closed";
//		}
//	}
}