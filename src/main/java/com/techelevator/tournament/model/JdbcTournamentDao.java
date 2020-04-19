package com.techelevator.tournament.model;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcTournamentDao implements TournamentDao {

    private JdbcTemplate jdbcTemplate;

    /**
     * Create a new tournament dao with the supplied data source.
     * @param dataSource an SQL data source
     */
    @Autowired
    public JdbcTournamentDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

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
    
    @Override
	public Tournament saveTournament(Tournament tournament, int id) {
    	
    	String sql = "INSERT INTO tournaments (name, description, organizer, start_date, end_date, reg_deadline, sport_id, type, participant_max, status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING tournament_id";

    	SqlRowSet result = jdbcTemplate.queryForRowSet(sql,
				tournament.getName(), tournament.getDescription(), id, tournament.getStartDate(), tournament.getEndDate(),
				tournament.getRegDeadline(), tournament.getSportId(), tournament.getType(), tournament.getParticipantMax(), tournament.getStatus());
    	
    	if(result.next()) {
    		tournament.setId(result.getLong("tournament_id"));
    	}

		return tournament;
	}
    
    /**
     * Get all of the tournaments from the database.
     * @return a List of tournament objects
     */
    @Override
    public List<Tournament> getAllTournaments() {
        List<Tournament> tournaments = new ArrayList<Tournament>();
        String sqlSelectAllTournaments = "SELECT tournament_id, name, description, start_date, end_date, "
        		+ " reg_deadline, sport_id, type, participant_max, status FROM tournaments";


        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectAllTournaments);
  
        while (results.next()) {
            Tournament tournament = mapResultToTournament(results);
            tournaments.add(tournament);
        }
        
        
        return tournaments;
    }
    
	/**
     * Get a tournament from the database by tournament id.
     * @param id the id of the wanted tournament
     * @return a tournament object
     */
    
    ///////////////////////////
    //    DO NOT CHANGE THIS METHOD!!!!!!
    ///////////////////////////
    @Override
    public Tournament getTournamentById(int id) {
        Tournament tournament = new Tournament();
                
        String sqlSelectTournament = "SELECT tournaments.tournament_id, tournaments.name, tournaments.description, tournaments.start_date, "
        								+ "tournaments.end_date, tournaments.reg_deadline, tournaments.sport_id, tournaments.type, "
        								+ "tournaments.participant_max "
	        							+ "FROM tournaments "
	        							+ "WHERE tournaments.tournament_id = ? ";
        
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectTournament, id);
        

        int invitiationCountInt = tournamentStatusChecker(id);

        while (results.next()) {
            tournament = mapResultToTournamentById(results, invitiationCountInt);
        }

        return tournament;
    }
    
    private Tournament mapResultToTournamentById(SqlRowSet results, int invitiationCountInt) {
        Tournament tournament = new Tournament();
        tournament.setId(results.getLong("tournament_id"));
        tournament.setName(results.getString("name"));
        tournament.setDescription(results.getString("description"));
        tournament.setStartDate(results.getDate("start_date").toLocalDate());
        tournament.setEndDate(results.getDate("end_date").toLocalDate());
        tournament.setRegDeadline(results.getDate("reg_deadline").toLocalDate());
        tournament.setSportId(results.getInt("sport_id"));
        tournament.setType(results.getString("type"));
        tournament.setParticipantMax(results.getInt("participant_max"));
//        tournament.setStatusCount(results.getDate("start_date"), results.getDate("end_date"), invitiationCountInt);
        
        return tournament;
    }
    
    @Override
    public int tournamentStatusChecker(int id) {
        String inviteSearchSql = "SELECT COUNT(user_id) "
        						+ "FROM users_tournaments "
        						+ "WHERE tournament_id = ? AND response ILIKE 'ACCEPTED'";
        
        SqlRowSet invitationCount = jdbcTemplate.queryForRowSet(inviteSearchSql, id);
        int invitiationCountInt = 0;
        while(invitationCount.next()) {
        	invitiationCountInt = invitationCount.getInt("count");
        }
        
        return invitiationCountInt;
    }
    
    /**
     * Get a tournament from the database by organizer id.
     * @param userId the id of the organizer who created the returned tournaments
     * @return a List of tournament objects
     */
    
    @Override
    public List<Tournament> getTournamentByOrganizer(int userId) {
        List<Tournament> tournaments = new ArrayList<Tournament>();
        
        String sqlSelectTournament = "SELECT tournament_id, name, description, start_date, end_date, "
        		+ " reg_deadline, sport_id, type, participant_max, status FROM tournaments "
        		+ " WHERE organizer = ?";


        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectTournament, userId);

        while (results.next()) {
            Tournament tournament = mapResultToTournament(results);
            tournaments.add(tournament);
        }        
        
        return tournaments;
    }
    
	/**
     * Get a tournament from the database by tournament id.
     * @param id the id of the wanted tournament
     * @return a tournament object
     */
    
    @Override
    public Tournament getTournamentByIdAndReturnsSpotsLeft(int id) {
        Tournament tournament = new Tournament();
        
        String sqlSelectTournament = "SELECT tournaments.tournament_id, tournaments.name, tournaments.description, tournaments.start_date, "
        								+ "tournaments.end_date, tournaments.reg_deadline, tournaments.sport_id, tournaments.type, "
        								+ "tournaments.participant_max, tournaments.status, tournaments.results, sports.sport_id, "
        								+ "sports.sport_name, (tournaments.participant_max - COUNT(users_tournaments.tournament_id)) as spots_left, tournaments.bracket_generated "
        							+ "FROM tournaments "
        							+ "JOIN sports ON tournaments.sport_id = sports.sport_id "
        							+ "JOIN users_tournaments on users_tournaments.tournament_id = tournaments.tournament_id "
        							+ "WHERE tournaments.tournament_id = ? "
        								+ "AND users_tournaments.response ILIKE 'ACCEPTED' "
        							+ "GROUP BY tournaments.participant_max, tournaments.tournament_id, sports.sport_name, sports.sport_id";
        	


        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectTournament, id);

        int invitiationCountInt = tournamentStatusChecker(id);
        
        while (results.next()) {
            tournament = mapResultToTournamentByIdAndCount(results, invitiationCountInt);
            System.out.println(tournament.getName());
        }
        
        return tournament;
    }
    
    private Tournament mapResultToTournamentByIdAndCount(SqlRowSet results, int invitiationCountInt) {
        Tournament tournament = new Tournament();
        tournament.setId(results.getLong("tournament_id"));
        tournament.setName(results.getString("name"));
        tournament.setDescription(results.getString("description"));
        tournament.setStartDate(results.getDate("start_date").toLocalDate());
        tournament.setEndDate(results.getDate("end_date").toLocalDate());
        tournament.setRegDeadline(results.getDate("reg_deadline").toLocalDate());
        tournament.setSportId(results.getInt("sport_id"));
        tournament.setType(results.getString("type"));
        tournament.setParticipantMax(results.getInt("participant_max"));
//        tournament.setStatusCount(results.getDate("start_date"), results.getDate("end_date"), invitiationCountInt);
        tournament.setSpotsLeft(results.getInt("spots_left"));
        tournament.setBracketGenerated(results.getBoolean("bracket_generated"));
        
        return tournament;
    }
    
    /**
     * Get all of the tournaments from the database that today is between the start and end dates.
     * @return a List of tournament objects
     */
    
    @Override
    public List<Tournament> getAllCurrentTournaments() {
        List<Tournament> tournaments = new ArrayList<Tournament>();
        String sqlSelectAllTournaments = "SELECT tournament_id, name, description, start_date, end_date,"
        		+ " reg_deadline, sport_id, type, participant_max, status FROM tournaments"
        		+ " WHERE CURRENT_DATE > start_date AND CURRENT_DATE < end_date"
        		+ " ORDER BY start_date";


        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectAllTournaments);

        while (results.next()) {
            Tournament tournament = mapResultToTournament(results);
            tournaments.add(tournament);
        }
        return tournaments;
    }
    
    /**
     * Get all of the tournaments from the database that today is past the end date.
     * @return a List of tournament objects
     */
    
    @Override
    public List<Tournament> getAllPastTournaments() {
        List<Tournament> tournaments = new ArrayList<Tournament>();
        String sqlSelectAllTournaments = "SELECT tournament_id, name, description, start_date, end_date,"
        		+ " reg_deadline, sport_id, type, participant_max, status FROM tournaments"
        		+ " WHERE end_date < CURRENT_DATE"
        		+ " ORDER BY end_date";


        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectAllTournaments);

        while (results.next()) {
            Tournament tournament = mapResultToTournament(results);
            tournaments.add(tournament);
        }
        return tournaments;
    }
    
    /**
     * Get all of the tournaments from the database that today is before the start date.
     * @return a List of tournament objects
     */
    
    @Override
    public List<Tournament> getAllFutureTournaments() {
        List<Tournament> tournaments = new ArrayList<Tournament>();
        String sqlSelectAllTournaments = "SELECT tournament_id, name, description, start_date, end_date,"
        		+ " reg_deadline, sport_id, type, participant_max, status FROM tournaments"
        		+ " WHERE CURRENT_DATE < start_date"
        		+ " ORDER BY start_date";


        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectAllTournaments);

        while (results.next()) {
            Tournament tournament = mapResultToTournament(results);
            tournaments.add(tournament);
        }
        return tournaments;
    }	
    
    /**
     * Get a tournaments that today is before or on the end date from the database by user id.
     * For the user's profile page
     * @param userId the id of the user who is involved in the returned tournaments
     * @return a List of tournament objects
     */ 
    
    @Override
   	public List<Tournament> getMatchingTournamentsByUserToCurrentDate(int userId) {
           List<Tournament> matchingUserTournaments = new ArrayList<>();
           
           String tournamentUserParticipanteSql = "SELECT tournaments.tournament_id, tournaments.name, tournaments.description, "
           		+ "sports.sport_name, tournaments.start_date, tournaments.end_date, tournaments.organizer, users_tournaments.response "
           		+ "FROM tournaments "
           		+ "JOIN users_tournaments ON tournaments.tournament_id = users_tournaments.tournament_id "
           		+ "JOIN users ON users_tournaments.user_id = users.user_id "
           		+ "JOIN sports ON tournaments.sport_id = sports.sport_id "
           		+ "WHERE tournaments.end_date > CURRENT_DATE AND users.user_id = ? "
           		+ "ORDER BY tournaments.name";
           
           SqlRowSet results = jdbcTemplate.queryForRowSet(tournamentUserParticipanteSql, userId);

           while(results.next()) {
               matchingUserTournaments.add(mapResultToTournamentMyTournaments(results));   
           }
           
           String tournamentUserOrganizerSql = "SELECT tournaments.tournament_id, tournaments.name, tournaments.description, "
           			+ "tournaments.start_date, tournaments.end_date, tournaments.organizer, sports.sport_name "
        			+ "FROM tournaments "
        			+ "JOIN sports ON tournaments.sport_id = sports.sport_id "
        			+ "WHERE tournaments.end_date > CURRENT_DATE AND organizer = ?";
           
           SqlRowSet resultsTwo = jdbcTemplate.queryForRowSet(tournamentUserOrganizerSql, userId);

           while(resultsTwo.next()) {
        	   Tournament tournament = new Tournament();
               tournament = mapResultToTournamentMyTournaments(resultsTwo);  
        	   tournament.setResponse("ORGANIZER");
        	   matchingUserTournaments.add(tournament);
           }
           
           return matchingUserTournaments;
   	}
    
    /**
     * Get a tournaments that today is after the end date from the database by user id.
     * For the user's profile page
     * @param userId the id of the user who is involved in the returned tournaments
     * @return a List of tournament objects
     */ 
    
    @Override
   	public List<Tournament> getMatchingTournamentsByUserToPastDate(int userId) {
           List<Tournament> matchingUserTournaments = new ArrayList<>();
                      
           String tournamentSearchSql = "SELECT tournaments.name, tournaments.description, sports.sport_name, tournaments.results "
           		+ "FROM tournaments JOIN users_tournaments ON "
           		+ "tournaments.tournament_id = users_tournaments.tournament_id "
    		    + "JOIN users ON users_tournaments.user_id = users.user_id "
    		    + "JOIN sports ON tournaments.sport_id = sports.sport_id "
    		    + "WHERE users.user_id = " + userId + " AND end_date < CURRENT_DATE "
    		    + "ORDER BY tournaments.name";
           
           SqlRowSet results = jdbcTemplate.queryForRowSet(tournamentSearchSql);

           while(results.next()) {
               matchingUserTournaments.add(mapResultToTournamentMyTournamentsPast(results));
           }
           return matchingUserTournaments;
   	}
    
	public void updateTournamentBracketGenerated(int id) {         
        String sqlSelectTournament = "UPDATE tournaments " 
        						+ "SET bracket_generated = true " 
        						+ "WHERE tournament_id = ?";     
        
        jdbcTemplate.update(sqlSelectTournament, id);
	}

    
    private Tournament mapResultToTournament(SqlRowSet results) {
        Tournament tournament = new Tournament();
        tournament.setId(results.getLong("tournament_id"));
        tournament.setName(results.getString("name"));
        tournament.setDescription(results.getString("description"));
        tournament.setStartDate(results.getDate("start_date").toLocalDate());
        tournament.setEndDate(results.getDate("end_date").toLocalDate());
        tournament.setRegDeadline(results.getDate("reg_deadline").toLocalDate());
        tournament.setSportId(results.getInt("sport_id"));
        tournament.setType(results.getString("type"));
        tournament.setParticipantMax(results.getInt("participant_max"));
//        tournament.setStatusCount(results.getDate("start_date"), results.getDate("end_date"), invitiationCountInt);
        
        return tournament;
    }
    
    private Tournament mapResultToTournamentMyTournaments(SqlRowSet results) {
        Tournament tournament = new Tournament();
        tournament.setSportName(results.getString("sport_name"));
        tournament.setName(results.getString("name"));
        tournament.setDescription(results.getString("description"));
        tournament.setStartDate(results.getDate("start_date").toLocalDate());
        tournament.setEndDate(results.getDate("end_date").toLocalDate());
        tournament.setId(results.getLong("tournament_id"));
        tournament.setOrganizer(results.getInt("organizer"));

        return tournament;
    }
    
    private Tournament mapResultToTournamentMyTournamentsPast(SqlRowSet results) {
        Tournament tournament = new Tournament();
        tournament.setSportName(results.getString("sport_name"));
        tournament.setName(results.getString("name"));
        tournament.setDescription(results.getString("description"));
        tournament.setResults(results.getString("results"));
        
        return tournament;
    }

}
