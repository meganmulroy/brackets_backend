package com.techelevator.invite.model;

import java.util.List;



public interface InviteDao {

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
	
	public Invite saveInvite(Invite invite);

    /**
     * Get all of the invites for a specific id from the database.
     * @param id the user id to get invitations for
     * @return a List of Invite objects
     */
	    
    public List<Invite> getAllInvitesByUser(int id);
    
    /**
     * Get all of the invites issued for a specific tournament from the database.
     * @param tournamentId the tournament id to get invitations for
     * @return a List of Invite objects
     */
    
    public List<Invite> getInvitesByTournament(int tournamentId);
    
    public List<Invite> getAcceptedInvitesByTournament(int tournamentId);
    
    public Invite updateInvite(int tournamentId, int userId, Invite invite);
    

	public List<Invite> getIssuedInvitesbyTournamentId(int tournamentId);


}
