package com.techelevator.matches.model;

import java.util.List;

import com.techelevator.invite.model.Invite;
import com.techelevator.tournament.model.Tournament;

public interface MatchDao {
	
	public List<Match> getTournamentMatches(int tournamentId);
	
	public List<Match> assignFirstRound(int tournamentId, int maxParticipants, List<Invite> acceptedInvites);

	public List<Match> getMatchesWithDetails(int tournamentId);
	
	public List<Match> generateBracketMatches(Tournament tournament);

	public void updateMatch(Match match);
	
}
