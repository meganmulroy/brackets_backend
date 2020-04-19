package com.techelevator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.sports.model.Sport;
import com.techelevator.sports.model.SportDao;

@CrossOrigin
@RestController
@RequestMapping("/sports")
public class SportsController {
	
	private SportDao sportsDAO;
    
    @Autowired
	public SportsController(SportDao sportsDAO) {
		this.sportsDAO = sportsDAO;
	}
	
	@GetMapping
	public List<Sport> getAllSports() {
		return sportsDAO.list();
	}

}
