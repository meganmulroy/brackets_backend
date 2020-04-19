package com.techelevator.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.authentication.AuthProvider;
import com.techelevator.invite.model.Invite;
import com.techelevator.invite.model.InviteDao;
import com.techelevator.matches.model.Match;
import com.techelevator.matches.model.MatchDao;
import com.techelevator.tournament.model.Tournament;
import com.techelevator.tournament.model.TournamentDao;

/**
 * BracketController
 */

@CrossOrigin
@RequestMapping("/bracket")
@RestController
public class BracketController {
	
	private MatchDao matchDAO;
	private InviteDao inviteDAO;
	private TournamentDao tournamentDAO;

    @Autowired
	public BracketController(MatchDao matchDAO, InviteDao inviteDAO, TournamentDao tournamentDAO) {
		this.matchDAO = matchDAO;
		this.inviteDAO = inviteDAO;
		this.tournamentDAO = tournamentDAO;
	}
    
    @GetMapping("/{id}")
    public List<Match> getEmptyMatches(@PathVariable int id) {
    	
    	tournamentDAO.updateTournamentBracketGenerated(id);

    	return matchDAO.getTournamentMatches(id);
    }
    
    @PostMapping("/{id}")
    public List<Match> saveMatches(@PathVariable int id){    	
    	Tournament tournament = new Tournament();
    	tournament = tournamentDAO.getTournamentById(id);
    	return matchDAO.generateBracketMatches(tournament);
    }
    
    @PostMapping("matchups/{id}")
    public List<Match> assignFirstRoundMatches(@PathVariable int id){
    	
    	saveMatches(id);
    	
    	List<Invite> acceptedInvites = inviteDAO.getAcceptedInvitesByTournament(id);
    	Tournament thisTournament = tournamentDAO.getTournamentById(id);
    	int maxParticipants = thisTournament.getParticipantMax();

    	if(acceptedInvites.size() < maxParticipants) {

    		int byesNeeded = (maxParticipants - acceptedInvites.size());

    		for(int i=1; i <= byesNeeded; i++) {
    			Invite byeInvite = new Invite();
    			byeInvite.setUserId(1);
    			byeInvite.setEmail("bye@bye.com");
    			byeInvite.setTournamentId(id);
    			byeInvite.setResponse("ACCEPTED");
    			acceptedInvites.add(inviteDAO.saveInvite(byeInvite));
    		}
    	}
    	
    	matchDAO.assignFirstRound(id, maxParticipants, acceptedInvites);    
    	
    	return matchDAO.getMatchesWithDetails(id);

    }
    
    @GetMapping("matches/{id}")
    public List<Match> getMatchesWithDetails(@PathVariable int id) {
    	return matchDAO.getMatchesWithDetails(id);
    }
    
    
    @PutMapping
    public List<Match> updateMatchResults(@RequestBody Match match){
    	
    	matchDAO.updateMatch(match);
    	
    	return matchDAO.getMatchesWithDetails(match.getTournamentId());
    }
    
}
