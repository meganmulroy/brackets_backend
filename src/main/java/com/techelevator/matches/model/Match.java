package com.techelevator.matches.model;

public class Match {
		
		private int matchId;
		
		private int tournamentId;
		
		private int userId;
		private String username;
		
		private boolean winner;
		
		private int gameId;
		private int round;
		
		private boolean topSlot;
		
		private boolean completed;
		
		
		public boolean isCompleted() {
			return completed;
		}
		public void setCompleted(boolean completed) {
			this.completed = completed;
		}
		public boolean isTopSlot() {
			return topSlot;
		}
		public void setTopSlot(boolean topSlot) {
			this.topSlot = topSlot;
		}
		public int getMatchId() {
			return matchId;
		}
		public void setMatchId(int matchId) {
			this.matchId = matchId;
		}
		public int getTournamentId() {
			return tournamentId;
		}
		public void setTournamentId(int tournamentId) {
			this.tournamentId = tournamentId;
		}
		public int getUserId() {
			return userId;
		}
		public void setUserId(int userId) {
			this.userId = userId;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public boolean isWinner() {
			return winner;
		}
		public void setWinner(boolean winner) {
			this.winner = winner;
		}
		public int getGameId() {
			return gameId;
		}
		public void setGameId(int gameId) {
			this.gameId = gameId;
		}
		public int getRound() {
			return round;
		}
		public void setRound(int round) {
			this.round = round;
		}
			
}