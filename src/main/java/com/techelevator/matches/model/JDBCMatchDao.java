package com.techelevator.matches.model;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.invite.model.Invite;
import com.techelevator.tournament.model.Tournament;

@Component
public class JDBCMatchDao implements MatchDao {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public JDBCMatchDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Match> getTournamentMatches(int tournamentId){
		List<Match> tournamentMatches = new ArrayList<Match>();
				
		String sqlMatches = "SELECT match_id, tournament_id, game_id, round "
				+ "FROM matches "
				+ "WHERE tournament_id = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlMatches, tournamentId);

		while(results.next()) {
			tournamentMatches.add(mapResultToMatch(results));
		}
		
		return tournamentMatches;	
	}

	
//	private Match mapResultToMatchDetails(SqlRowSet results) {
//		Match match = new Match();
//		match.setMatchId(results.getInt("match_id"));
//		match.setTournamentId(results.getInt("tournament_id"));
//		match.setGameId(results.getInt("game_id"));
//		match.setRound(results.getInt("round"));
//		match.setUserId(results.getInt("user_id"));
//		match.setUsername(results.getString("username"));
//		match.setWinner(results.getBoolean("winner"));
//		match.setTopSlot(results.getBoolean("top_slot"));
//		return match;
//	}
	
	@Override
	public List<Match> assignFirstRound(int tournamentId, int maxParticipants, List<Invite> acceptedInvites){
		List<Match> firstRoundMatches = new ArrayList<Match>();
		List<Match> tournamentMatchups = new ArrayList<Match>();
		
		String sqlFirstRoundMatches = "SELECT match_id, tournament_id, game_id, round "
				+ "FROM matches "
				+ "WHERE tournament_id = ? AND round = 1";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFirstRoundMatches, tournamentId);
		
		while(results.next()) {
			firstRoundMatches.add(mapResultToMatch(results));
		}
		
		int matchId = firstRoundMatches.get(0).getMatchId();
		
		boolean topSlotCounter = true;
		for(int i = 0; i < (firstRoundMatches.size()*2); i++) {
			
			if(i > 1 && (i%2 == 0)) {
				matchId++;
			}
			
			String sqlSaveFirstRoundMatchUps = "INSERT INTO users_matches (match_id, user_id, top_slot) "
					+ "VALUES (?, ?, ?)";
			
			jdbcTemplate.update(sqlSaveFirstRoundMatchUps, matchId, acceptedInvites.get(i).getUserId(), topSlotCounter);

			Match match = new Match();
			match.setMatchId(matchId);
			match.setUserId(acceptedInvites.get(i).getUserId());
			match.setTournamentId(tournamentId);
			match.setRound(1);
			match.setWinner(false);
			match.setTopSlot(topSlotCounter);
			tournamentMatchups.add(match);

			topSlotCounter = !topSlotCounter;
		}
		System.out.println("firstroundmatches size check 2: " + firstRoundMatches.size());
		int firstRoundMatchesSizeToCheck= firstRoundMatches.size()*2;
		int emptyUserMatchesNeeded = 0;
		if(firstRoundMatchesSizeToCheck == 4) {
			emptyUserMatchesNeeded = 2;
		} else if(firstRoundMatchesSizeToCheck == 8) {
			emptyUserMatchesNeeded = 6;
		} else if(firstRoundMatchesSizeToCheck == 16) {
			emptyUserMatchesNeeded = 14;
		} else if(firstRoundMatchesSizeToCheck == 32) {
			emptyUserMatchesNeeded = 30;
		}
		System.out.println("matches needed: " + emptyUserMatchesNeeded);
		matchId++;
		for(int i = 0; i < emptyUserMatchesNeeded; i++) {
			if(i > 1 && (i%2 == 0)) {
			matchId++;
			}
			System.out.println("matchID Being created: " + matchId);

	        String sqlSaveEmptyMatchUps = "INSERT INTO users_matches (match_id, top_slot) "
	                + "VALUES (?, ?)";
	        
	        jdbcTemplate.update(sqlSaveEmptyMatchUps, matchId, topSlotCounter);
			System.out.println("matchID Being created: " + topSlotCounter);

			topSlotCounter = !topSlotCounter;
		}
		
		return tournamentMatchups;
	}
	
	@Override
	public List<Match> getMatchesWithDetails(int tournamentId) {
		List<Match> tournamentMatches = new ArrayList<Match>();
		
		String sqlBracketMatches = "SELECT matches.match_id, matches.tournament_id, game_id, round, users_matches.user_id, "
				+ "users.username, users_matches.winner, users_matches.top_slot, matches.completed "
				+ "FROM matches "
				+ "JOIN users_matches ON users_matches.match_id = matches.match_id "
				+ "LEFT JOIN users ON users.user_id = users_matches.user_id "
				+ "WHERE matches.tournament_id = ? "
				+ "ORDER BY matches.game_id DESC, users_matches.top_slot DESC";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlBracketMatches, tournamentId);

	    while(results.next()) {
	        tournamentMatches.add(mapResultToSavedMatch(results));
	    }
	    
	    return tournamentMatches;
	}
	
	private Match mapResultToSavedMatch(SqlRowSet results) {
		Match match = new Match();
		match.setMatchId(results.getInt("match_id"));
		match.setTournamentId(results.getInt("tournament_id"));
		match.setGameId(results.getInt("game_id"));
		match.setUserId(results.getInt("user_id"));
		match.setRound(results.getInt("round"));
		match.setUsername(results.getString("username"));
		match.setWinner(results.getBoolean("winner"));
		match.setTopSlot(results.getBoolean("top_slot"));
		match.setCompleted(results.getBoolean("completed"));
		return match;
	}
	

	
	private Match mapResultToMatch(SqlRowSet results) {
		Match match = new Match();
		match.setMatchId(results.getInt("match_id"));
		match.setTournamentId(results.getInt("tournament_id"));
		match.setGameId(results.getInt("game_id"));
		match.setRound(results.getInt("round"));
		return match;
	}
	
	@Override
	public List<Match> generateBracketMatches(Tournament tournament){
		int numParticipants = tournament.getParticipantMax();
		int bracketMatchCount = 0;
		int gameId = 1;

		if (numParticipants <= 4) {
			bracketMatchCount = 3;
			gameId = 3;
		} else if (numParticipants > 4 && numParticipants <= 8) {
			bracketMatchCount = 7;
			gameId = 7;
		} else if (numParticipants > 8 && numParticipants <= 16) {
			bracketMatchCount = 15;
			gameId = 15;
		} else if (numParticipants > 16 && numParticipants <= 32) {
			bracketMatchCount = 31;
			gameId = 31;
		}
		
		List<Match> bracketMatches = new ArrayList<Match>();
		
		String sqlInsertMatches = "INSERT INTO matches (tournament_id, game_id, round) "
				+ "VALUES (?, ?, ?) RETURNING match_id";
		
		for(int i = 1; i <= (bracketMatchCount); i++){
			int round = 1;
			
			if(numParticipants == 32 && gameId <= 1) {
				round = 5;
			} else if((numParticipants == 16 && gameId <= 1) || (numParticipants == 32 && gameId <= 3)) {
				round = 4;
			} else if((numParticipants == 8 && gameId <= 1) 
					|| (numParticipants == 16 && gameId <= 3) || (numParticipants == 32 && gameId <= 7)) {
				round = 3;
			} else if((numParticipants == 4 && gameId <= 1) || (numParticipants == 8 && gameId <= 3) 
					|| (numParticipants == 16 && gameId <= 7) || (numParticipants == 32 && gameId <= 15)) {
				round = 2;
			} else if((numParticipants == 4 && gameId <= 3) || (numParticipants == 8 && gameId <= 7) 
					|| (numParticipants == 16 && gameId <= 15) || (numParticipants == 32 && gameId <= 31)) {
				round = 1;
			} 
			
			SqlRowSet results = jdbcTemplate.queryForRowSet(sqlInsertMatches, tournament.getId(), gameId, round);
						
			while(results.next()) {
				Match m = mapMatchIDToMatch(results);
				m.setTournamentId((int)tournament.getId());
				m.setGameId(gameId);
				m.setRound(round);
				bracketMatches.add(m);
			}
			
			gameId--;
		}

		return bracketMatches;
	}

	public void updateMatch(Match match) {
		
		String updateMatch = "UPDATE matches "
				+ "SET completed = true "
				+ "WHERE match_id = ?";

		jdbcTemplate.update(updateMatch, match.getMatchId());
		
		String updatingCurrentMatchWinner = "UPDATE users_matches "
							+ "SET winner = true "
							+ "WHERE user_id = ? AND match_id = ?";
		
		System.out.println("find this in sql USERID: " + match.getUserId());
		System.out.println("find this in sql MATCHID: " + match.getMatchId());
		
		jdbcTemplate.update(updatingCurrentMatchWinner, match.getUserId(), match.getMatchId());
		
		String updatingCurrentMatchLoserSelect = "SELECT users_matches.user_id "
				+ "FROM matches "
				+ "JOIN users_matches ON matches.match_id = users_matches.match_id "
				+ "WHERE game_id = ? AND winner is NULL AND matches.match_id = ?";
		
		System.out.println("find this in sql GAMEID: " + match.getGameId());
		System.out.println("find this in sql MATCHID: " + match.getMatchId());

		int loserId = jdbcTemplate.queryForObject(updatingCurrentMatchLoserSelect, Integer.class, match.getGameId(), match.getMatchId());
				
		String updatingCurrentMatchLoser = "UPDATE users_matches "
				+ "SET winner = false "
				+ "WHERE user_id = ? AND match_id = ?";
		
		jdbcTemplate.update(updatingCurrentMatchLoser, loserId, match.getMatchId());

		int newGameNumber = 1;
		if (match.getGameId() % 2 == 1 && match.getGameId() > 3) { 
			newGameNumber = (match.getGameId() - 1) / 2;
		} else if (match.getGameId() % 2 == 0 && match.getGameId() > 3) {
			newGameNumber = match.getGameId() / 2;
		}
		
		String findingNextMatchId = "SELECT match_id "
				+ "FROM matches "
				+ "WHERE tournament_id = ? AND game_id = ?";
		
		int matchId = jdbcTemplate.queryForObject(findingNextMatchId, Integer.class, match.getTournamentId(), newGameNumber);
		
		boolean calculatedNextMatchSpot = true;
		if (match.getGameId() % 2 == 1) { 
				calculatedNextMatchSpot = true;
		} else { 
				calculatedNextMatchSpot = false;
		}
		
		if(match.getGameId() != 1) {
		String updatePlayerToNextMatch = "UPDATE users_matches "
							+ "SET user_id = ? "
							+ "WHERE match_id = ? AND top_slot = ?";
		
		jdbcTemplate.update(updatePlayerToNextMatch, match.getUserId(), matchId, calculatedNextMatchSpot);
		}

	}
	
	private Match mapMatchIDToMatch(SqlRowSet results) {
		Match match = new Match();
		match.setMatchId(results.getInt("match_id"));
		return match;
	}

}