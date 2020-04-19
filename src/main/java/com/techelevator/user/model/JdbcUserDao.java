package com.techelevator.user.model;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.techelevator.authentication.PasswordHasher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcUserDao implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private PasswordHasher passwordHasher;
	private Map<Integer, User> users = new HashMap<>();


    /**
     * Create a new user dao with the supplied data source and the password hasher
     * that will salt and hash all the passwords for users.
     *
     * @param dataSource an SQL data source
     * @param passwordHasher an object to salt and hash passwords
     */
    @Autowired
    public JdbcUserDao(DataSource dataSource, PasswordHasher passwordHasher) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordHasher = passwordHasher;
    }
	
    @Override
	public List<User> list(){
		List<User> allUsers = new ArrayList<>();
		users.forEach((k, v) -> allUsers.add(v));
		return allUsers;
	}

    @Override
	public User read(int id) {
		return users.get(id);
	}
    
    /**
     * Save a new user to the database. The password that is passed in will be
     * salted and hashed before being saved. The original password is never
     * stored in the system. We will never have any idea what it is!
     *
     * @param userName the user name to give the new user
     * @param password the user's password
     * @param role the user's role
     * @return the new user
     */
    @Override
    public User saveUser(String userName, String password, String email) {
        byte[] salt = passwordHasher.generateRandomSalt();
        String hashedPassword = passwordHasher.computeHash(password, salt);
        String saltString = new String(Base64.getEncoder().encode(salt));
        long newId = jdbcTemplate.queryForObject(
                "INSERT INTO users (username, password, salt, email) VALUES (?, ?, ?, ?) RETURNING user_id", Long.class,
                userName, hashedPassword, saltString, email);
        
        User newUser = new User();
        newUser.setId(newId);
        newUser.setUsername(userName);
        newUser.setEmail(email);
        return newUser;
    }

    @Override
    public void changePassword(User user, String newPassword) {
        byte[] salt = passwordHasher.generateRandomSalt();
        String hashedPassword = passwordHasher.computeHash(newPassword, salt);
        String saltString = new String(Base64.getEncoder().encode(salt));

        jdbcTemplate.update("UPDATE users SET password=?, salt=? WHERE user_id=?", hashedPassword, saltString, user.getId());
    }

    /**
     * Look for a user with the given username and password. Since we don't
     * know the password, we will have to get the user's salt from the database,
     * hash the password, and compare that against the hash in the database.
     *
     * @param userName the user name of the user we are checking
     * @param password the password of the user we are checking
     * @return true if the user is found and their password matches
     */
    @Override
    public User getValidUserWithPassword(String userName, String password) {
        String sqlSearchForUser = "SELECT user_id, username, password, salt, email FROM users WHERE UPPER(username) = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchForUser, userName.toUpperCase());
        if (results.next()) {
            String storedSalt = results.getString("salt");
            String storedPassword = results.getString("password");
            String hashedPassword = passwordHasher.computeHash(password, Base64.getDecoder().decode(storedSalt));
            if (storedPassword.equals(hashedPassword)) {
                return mapResultToUser(results);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get all of the users from the database.
     * @return a List of user objects
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();
        String sqlSelectAllUsers = "SELECT user_id, username, email FROM users";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectAllUsers);

        while (results.next()) {
            User user = mapResultToUser(results);
            users.add(user);
        }

        return users;
    }

    private User mapResultToUser(SqlRowSet results) {
        User user = new User();
        user.setId(results.getInt("user_id"));
        user.setUsername(results.getString("username"));
        user.setEmail(results.getString("email"));
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        String sqlSelectUserByUsername = "SELECT user_id, username, email FROM users WHERE username = ?";
        
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectUserByUsername, username);
        
        if (results.next()) {
            return mapResultToUser(results);
        } else {
            return null;
        }
    }

    @Override
    public List<User> getUsersByTournament(int tournamentId) {
	    List<User> tournamentUsers = new ArrayList<User>();
	    String sqlSelectTournamentUsers = "SELECT users.username FROM users JOIN users_tournaments ON "
			    + "users.user_id = users_tournaments.user_id JOIN tournaments ON users_tournaments.tournament_id"
			    + "= tournaments.tournament_id WHERE tournaments.tournament_id = " + tournamentId + " ORDER BY tournaments.name";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectTournamentUsers);

        while (results.next()) {
            User user = mapResultToUser(results);
            tournamentUsers.add(user);
        }

        return tournamentUsers;
    }

}
