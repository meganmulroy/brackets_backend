package com.techelevator.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.authentication.AuthProvider;
import com.techelevator.invite.model.Invite;
import com.techelevator.invite.model.InviteDao;
import com.techelevator.tournament.model.Tournament;
import com.techelevator.tournament.model.TournamentDao;
import com.techelevator.controller.UserNotFoundException;

@CrossOrigin
@RequestMapping("/invite")
@RestController
public class InviteController {
	
	private TournamentDao tournamentDAO;

	private InviteDao inviteDAO;
	
	@Autowired
	private AuthProvider auth;
    
    @Autowired
	public InviteController(InviteDao inviteDAO, TournamentDao tournamentDAO) {
		this.inviteDAO = inviteDAO;
		this.tournamentDAO = tournamentDAO;
	}
	
//	@GetMapping
//	public List<Invite> getAllInvitesForUser(@RequestParam int id) {
//		return inviteDAO.getAllInvitesByUser(id);
//	}
    
    @PutMapping("/{tournamentId}")
    public Invite updateInvite(@RequestBody Invite invite, @PathVariable int tournamentId) throws TournamentFullException {
    	Tournament thisTourney = tournamentDAO.getTournamentById(invite.getTournamentId());
    	int maxPlayers = thisTourney.getParticipantMax();
    	List<Invite> acceptedInvites = new ArrayList<Invite>();
    	acceptedInvites = inviteDAO.getAcceptedInvitesByTournament((int)thisTourney.getId());
    	int currentAcceptedInvites = acceptedInvites.size();
    	if(currentAcceptedInvites >= maxPlayers) {
    		throw new TournamentFullException();
    	}
//    	System.out.println((int)auth.getCurrentUser().getId());
    	return inviteDAO.updateInvite(tournamentId, (int)auth.getCurrentUser().getId(), invite);
    }
    	
    
	
	@GetMapping("/{id}")
	public List<Invite> getAllInvitesForTournament(@PathVariable int id) {
		return inviteDAO.getIssuedInvitesbyTournamentId(id);
	}
	
    @PostMapping("/createInvite")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Invite> createInvite(@RequestBody Invite invite) throws UserNotFoundException, TournamentFullException{
    	Tournament thisTourney = tournamentDAO.getTournamentById(invite.getTournamentId());
    	int maxPlayers = thisTourney.getParticipantMax();
    	List<Invite> acceptedInvites = new ArrayList<Invite>();
    	acceptedInvites = inviteDAO.getAcceptedInvitesByTournament((int)thisTourney.getId());
    	int currentAcceptedInvites = acceptedInvites.size();
    	if(currentAcceptedInvites >= maxPlayers) {
    		throw new TournamentFullException();
    	}else if (inviteDAO.saveInvite(invite) == null) {
    		throw new UserNotFoundException();
    	}
    	return inviteDAO.getInvitesByTournament(invite.getTournamentId());
    }
}