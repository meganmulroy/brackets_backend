package com.techelevator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class TournamentFullException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3213012184214936461L;

}
