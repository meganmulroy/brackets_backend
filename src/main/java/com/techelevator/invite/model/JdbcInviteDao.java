package com.techelevator.invite.model;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcInviteDao implements InviteDao {

    private JdbcTemplate jdbcTemplate;

    /**
     * Create a new invite dao with the supplied data source.
     *
     * @param dataSource an SQL data source
     */
    @Autowired
    public JdbcInviteDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Save a new invite to a tournament to the database.
     *
     * @param Invite invite to be saved
     * @return the new invite
     */
    
    @Override
	public Invite saveInvite(Invite invite) {
    	
    	
    	String sqlSaveByEmail = "INSERT INTO users_tournaments (user_id, tournament_id, response) "
    			+ "VALUES ((SELECT user_id FROM users "
    			+ "WHERE email = ?), ?, ?)";
		
    	try {
    		int count = jdbcTemplate.update(sqlSaveByEmail,invite.getEmail(), invite.getTournamentId(), invite.getResponse());
    
    		return invite;
    	}catch(Exception e){
    		return null;
    	}
		
    	
	}
    
    @Override
    public Invite updateInvite(int tournamentId, int userId, Invite invite) {

    	String sqlUpdateStatus = "UPDATE users_tournaments "
    			+ "SET response = ? "
    			+ "WHERE user_id = ? AND tournament_id = ?";
    	
    	jdbcTemplate.update(sqlUpdateStatus, invite.getResponse(), userId, tournamentId);
    	    	
    	invite.setUserId(userId);
    	invite.setTournamentId(tournamentId);
    	
    	return invite;
    }

    /**
     * Get all of the invites for a specific user from the database.
     * @param id user id to get invites for
     * @return a List of invite objects
     */

    public List<Invite> getAllInvitesByUser(int id) {
        List<Invite> filteredInvites = new ArrayList<>();
        
        String inviteSearchSql = "SELECT tournaments.tournament_id, tournaments.name, tournaments.reg_deadline, response "
        		+ "FROM users_tournaments "
        		+ "JOIN tournaments ON users_tournaments.tournament_id = tournaments.tournament_id "
        		+ "WHERE user_id = ?"
				+ "ORDER BY tournaments.reg_deadline";
        
        SqlRowSet results = jdbcTemplate.queryForRowSet(inviteSearchSql, id);

        while(results.next()) {
        	filteredInvites.add(mapResultToInviteByUser(results));
        }

        return filteredInvites;
	}
    
    /**
     * Get all of the invites for a specific tournament from the database.
     * @param tournamentId tournament id to get invites for
     * @return a List of invite objects
     */
    
    @Override
    public List<Invite> getInvitesByTournament(int tournamentId) {
        List<Invite> filteredInvites = new ArrayList<>();
        
        String inviteSearchSql = "SELECT users.user_id, users.email, response, tournaments.reg_deadline "
        		+ "FROM users_tournaments "
        		+ "JOIN users ON users_tournaments.user_id = users.user_id "
        		+ "JOIN tournaments on users_tournaments.tournament_id = tournaments.tournament_id "
        		+ "WHERE users_tournaments.tournament_id = ?";
        
        SqlRowSet results = jdbcTemplate.queryForRowSet(inviteSearchSql, tournamentId);

        while(results.next()) {
        	Invite invite = mapResultToInviteByTournament(results);
        	filteredInvites.add(invite);
        }
        
        return filteredInvites;
	}
    
    @Override
    public List<Invite> getAcceptedInvitesByTournament(int tournamentId) {
        List<Invite> filteredInvites = new ArrayList<>();
        
        String inviteSearchSql = "SELECT users.user_id, users.email, response, tournaments.reg_deadline "
        		+ "FROM users_tournaments "
        		+ "JOIN users ON users_tournaments.user_id = users.user_id "
        		+ "JOIN tournaments on users_tournaments.tournament_id = tournaments.tournament_id "
        		+ "WHERE users_tournaments.tournament_id = ? AND users_tournaments.response = 'ACCEPTED'";
        
        SqlRowSet results = jdbcTemplate.queryForRowSet(inviteSearchSql, tournamentId);

        while(results.next()) {
        	Invite invite = mapResultToInviteByTournament(results);
        	filteredInvites.add(invite);
        }
        
        return filteredInvites;
	}
    @Override
    public List<Invite> getIssuedInvitesbyTournamentId(int tournamentId) {
        List<Invite> filteredInvites = new ArrayList<>();
        
        String inviteSearchSql = "SELECT users.user_id, users.username, users.email, response, tournaments.reg_deadline "
        		+ "FROM users_tournaments "
        		+ "JOIN users ON users_tournaments.user_id = users.user_id "
        		+ "JOIN tournaments on users_tournaments.tournament_id = tournaments.tournament_id "
        		+ "WHERE users_tournaments.tournament_id = ? ";
        
        SqlRowSet results = jdbcTemplate.queryForRowSet(inviteSearchSql, tournamentId);

        while(results.next()) {
        	Invite invite = mapResultToIssuedInviteByTournament(results);
        	filteredInvites.add(invite);
        }
        
        return filteredInvites;
	}
    
    private Invite mapResultToInviteByUser(SqlRowSet results) {
    	Invite invite = new Invite();
    	invite.setTournamentId(results.getInt("tournament_id"));
    	invite.setTournamentName(results.getString("name"));
    	invite.setRegDeadline(results.getDate("reg_deadline"));
        invite.setResponse(results.getString("response"));
        return invite;
    }
    
    private Invite mapResultToInviteByTournament(SqlRowSet results) {
    	Invite invite = new Invite();
    	invite.setEmail(results.getString("email"));
        invite.setResponse(results.getString("response"));
        invite.setRegDeadline(results.getDate("reg_deadline"));
        invite.setUserId(results.getInt("user_id"));
        return invite;
    }
    
    private Invite mapResultToIssuedInviteByTournament(SqlRowSet results) {
    	Invite invite = new Invite();
    	invite.setUserId(results.getInt("user_id"));
    	invite.setUsername(results.getString("username"));
    	invite.setEmail(results.getString("email"));
        invite.setResponse(results.getString("response"));
        invite.setRegDeadline(results.getDate("reg_deadline"));
        return invite;
    }
    
    
}
