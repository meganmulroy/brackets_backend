package com.techelevator.controller;

import java.util.List;

import javax.validation.Valid;

import com.techelevator.authentication.AuthProvider;
import com.techelevator.authentication.JwtTokenHandler;
import com.techelevator.authentication.RegistrationResult;
import com.techelevator.authentication.UnauthorizedException;


import com.techelevator.invite.model.Invite;

import com.techelevator.invite.model.InviteDao;
import com.techelevator.tournament.model.Tournament;
import com.techelevator.tournament.model.TournamentDao;

import com.techelevator.user.model.User;
import com.techelevator.user.model.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;


/**
 * AccountController
 */
@RestController
@CrossOrigin
public class AccountController {

    @Autowired
    private AuthProvider auth;
    
    private InviteDao inviteDAO;
    private UserDao userDAO;

    @Autowired
    private JwtTokenHandler tokenHandler;

	
	@Autowired
	public AccountController(InviteDao inviteDAO, UserDao userDAO) {
		this.inviteDAO = inviteDAO;
		this.userDAO = userDAO;
	}
    
    @GetMapping("/users") 
    public List<User> list(){
    	return userDAO.getAllUsers();
    }

    @GetMapping("/profile") 
    public User readProfileInfo(){
    	return userDAO.getUserByUsername(auth.getCurrentUser().getUsername());
    }
    
  
    
    @GetMapping("/profile/invites") 
    public List<Invite> readProfileInviteInfo(){
    	return inviteDAO.getAllInvitesByUser((int)auth.getCurrentUser().getId());
    }
    


    @PostMapping("/login")
    public String login(@RequestBody User user) throws UnauthorizedException {
        if(auth.signIn(user.getUsername(), user.getPassword())) {
            User currentUser = auth.getCurrentUser();
            return tokenHandler.createToken(user.getUsername(), currentUser.getRole());
        } else {
            throw new UnauthorizedException();
        }
    }
    
    @PostMapping("/register")
    public RegistrationResult register(@Valid @RequestBody User user, BindingResult result) {
    	RegistrationResult registrationResult = new RegistrationResult();
    	if(result.hasErrors()) {
            for(ObjectError error : result.getAllErrors()) {
                registrationResult.addError(error.getDefaultMessage());
            }
        }
    	else {
    		auth.register(user.getUsername(), user.getPassword(), user.getEmail());
    		registrationResult.setSuccess(true);
    	}
    	return registrationResult;
    }

}