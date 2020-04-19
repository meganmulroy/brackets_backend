package com.techelevator.tournament.model;

import java.util.List;


public interface TournamentDao {

    /**
     * Save a new tournament to the database.
     *
     * @param name the name to give the new tournament
     * @param description the tournament's description
     * @param startDate the tournament's start date
     * @param endDate the tournament's end date
     * @param regDeadline the tournament's registration deadline
     * @param sportId the tournament's sport id
     * @param type the tournament's type (round robin, single elimination, etc.)
     * @param participantMax the tournament's maximum amount of participants
     * @return the new tournament
     */
	
	public Tournament saveTournament(Tournament tournament, int id);
	
    /**
     * Get all of the tournaments from the database.
     * @return a List of tournament objects
     */
	
    public List<Tournament> getAllTournaments();

	/**
     * Get a tournament from the database by tournament id.
     * @param id the id of the wanted tournament
     * @return a tournament
     */
	
    public Tournament getTournamentById(int id);
    
    /**
     * Get a tournament from the database by organizer id.
     * @param userId the id of the organizer who created the returned tournaments
     * @return a list of tournaments
     */
    
    public List<Tournament> getTournamentByOrganizer(int userId);
    
    public Tournament getTournamentByIdAndReturnsSpotsLeft(int id);

    
    /**
     * Get all of the tournaments from the database that today is between the start and end dates.
     * @return a List of tournament objects
     */
    
    public List<Tournament> getAllCurrentTournaments();
    
    /**
     * Get all of the tournaments from the database that today is past the end date.
     * @return a List of tournament objects
     */
    
    public List<Tournament> getAllPastTournaments();
    
    /**
     * Get all of the tournaments from the database that today is before the start date.
     * @return a List of tournament objects
     */
    
    public List<Tournament> getAllFutureTournaments();
    
    /**
     * Get a tournaments that today is before or on the end date from the database by user id.
     * For the user's profile page
     * @param userId the id of the user who is involved in the returned tournaments
     * @return a list of tournaments
     */ 
    
    public List<Tournament> getMatchingTournamentsByUserToCurrentDate(int id);
    
    /**
     * Get a tournaments that today is after the end date from the database by user id.
     * For the user's profile page
     * @param userId the id of the user who is involved in the returned tournaments
     * @return a list of tournaments
     */ 
    
    public List<Tournament> getMatchingTournamentsByUserToPastDate(int id);

	int tournamentStatusChecker(int id);

	public void updateTournamentBracketGenerated(int id);


//	public Tournament getTournamentDetailsById(int tournamentId, int id);

//  public List<Tournament> getMatchingTournamentsBySearch(String search, String sortBy);




}
