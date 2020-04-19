package com.techelevator.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.authentication.AuthProvider;
import com.techelevator.invite.model.Invite;
import com.techelevator.invite.model.InviteDao;
import com.techelevator.tournament.model.Tournament;
import com.techelevator.tournament.model.TournamentDao;

/**
 * AccountController
 */

@CrossOrigin
@RestController
public class TournamentController {

	private TournamentDao tournamentDAO;
	private InviteDao inviteDAO;

	@Autowired
	private AuthProvider auth;

	@Autowired
	public TournamentController(TournamentDao tournamentDAO, InviteDao inviteDAO) {
		this.tournamentDAO = tournamentDAO;
		this.inviteDAO = inviteDAO;
	}

//	@GetMapping
//	public List<Tournament> getAllTournaments(@RequestParam(defaultValue="") String search,
//											  @RequestParam(defaultValue="name") String sortBy) {
//		return tournamentDAO.getMatchingTournamentsBySearch(search, sortBy);
//	}

	@GetMapping("/tournaments")
	public List<Tournament> getTournamentByOrganizer() {
		List<Tournament> organizerTourney = tournamentDAO.getTournamentByOrganizer((int) auth.getCurrentUser().getId());
		updateSpotsLeft(organizerTourney);

		return organizerTourney;
	}

	@GetMapping("/tournaments/current")
	public List<Tournament> getAllCurrentTournaments() {
		List<Tournament> currentTournaments = tournamentDAO.getAllCurrentTournaments();
		updateSpotsLeft(currentTournaments);

		return currentTournaments;

	}

	@GetMapping("/tournaments/past")
	public List<Tournament> getAllPastTournaments() {

		List<Tournament> pastTournaments = tournamentDAO.getAllPastTournaments();
		updateSpotsLeft(pastTournaments);

		return pastTournaments;

	}

	private void updateSpotsLeft(List<Tournament> tourneyList) {
		for (Tournament t : tourneyList) {

			int maxPlayers = t.getParticipantMax();
			List<Invite> acceptedInvites = new ArrayList<Invite>();
			acceptedInvites = inviteDAO.getAcceptedInvitesByTournament((int) t.getId());
			t.setSpotsLeft(maxPlayers - acceptedInvites.size());
		}

	}
	
	private void updateSpotsLeft(Tournament t) {
			int maxPlayers = t.getParticipantMax();
			List<Invite> acceptedInvites = new ArrayList<Invite>();
			acceptedInvites = inviteDAO.getAcceptedInvitesByTournament((int) t.getId());
			t.setSpotsLeft(maxPlayers - acceptedInvites.size());
	}

	@GetMapping("/tournaments/future")
	public List<Tournament> getAllFutureTournaments() {

		List<Tournament> futureTournaments = tournamentDAO.getAllFutureTournaments();
		updateSpotsLeft(futureTournaments);

		return futureTournaments;

	}

	@PostMapping("/tournaments/createTournament")
	@ResponseStatus(HttpStatus.CREATED)
	public Tournament createTournament(@RequestBody Tournament tournament) {

		System.out.println(tournament.getStartDate());
		Tournament createdTournament = tournamentDAO.saveTournament(tournament, (int) auth.getCurrentUser().getId());

		if (tournament.getParticipate().equalsIgnoreCase("Yes")) {
			Invite invite = new Invite();
			invite.setTournamentId((int) createdTournament.getId());
			invite.setEmail(auth.getCurrentUser().getEmail());
			;
			invite.setResponse("ACCEPTED");
			inviteDAO.saveInvite(invite);
		}

		return createdTournament;
	}
	
	@GetMapping("/tournaments/{id}")
	public Tournament read(@PathVariable int id) {
		Tournament t = tournamentDAO.getTournamentById(id);
		updateSpotsLeft(t);
		return t;
	}

	@GetMapping("/profile/pastTournaments")
	public List<Tournament> readProfilePastTournamentsInfo() {
		List<Tournament> profilePastTourney = tournamentDAO
				.getMatchingTournamentsByUserToPastDate((int) auth.getCurrentUser().getId());
		System.out.println("what is happening!" + profilePastTourney.size());
		updateSpotsLeft(profilePastTourney);
		System.out.println("what is happening now!" + profilePastTourney.size());
		System.out.println("what is happening now!" + profilePastTourney.get(0).getSpotsLeft());
		return profilePastTourney;
	}

	@GetMapping("/profile/tournaments")
	public List<Tournament> readProfileTournamentInfo() {
		List<Tournament> profileTourneyInfo = tournamentDAO
				.getMatchingTournamentsByUserToCurrentDate((int) auth.getCurrentUser().getId());
		updateSpotsLeft(profileTourneyInfo);

		return profileTourneyInfo;
	}

}
