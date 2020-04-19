package com.techelevator.invite.model;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Tournament
 */
public class Invite {
	
	@NotBlank(message = "User ID is required")
	private int userId;
	private String username;
	
	@NotBlank(message = "Tournament ID is required")
	private int tournamentId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date regDeadline;
    
    @NotBlank(message = "Response is required")
    private String response;
    
    private String tournamentName;
    
    private String email;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(int tournamentId) {
		this.tournamentId = tournamentId;
	}
	
	public Date getRegDeadline() {
		return regDeadline;
	}

	public void setRegDeadline(Date regDeadline) {
		this.regDeadline = regDeadline;
	}

	public String getResponse() {
		Date date = new Date();
		if (regDeadline != null) { 
			if (date.after(regDeadline) && response.equals("PENDING")) { 
				return "EXPIRED";
			
			}
		}
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
		
	}
	
    public String getTournamentName() {
		return tournamentName;
	}

	public void setTournamentName(String tournamentName) {
		this.tournamentName = tournamentName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}