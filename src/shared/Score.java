package shared;

public class Score {
	private boolean winner = false;
	private int guesses = 0;
	private boolean forfeit = false;
	
	public boolean isForfeit() {
		return forfeit;
	}
	public boolean isWinner() {
		return winner;
	}
	public void setWinner(boolean winner) {
		this.winner = winner;
	}
	public int getGuesses() {
		return guesses;
	}
	public void incrementGuesses() {
		this.guesses++;
	}
	
	public void reset() {
		guesses = 0;
		winner = false;
	}
	
	public String toString() {
		return "Guesses: " + this.guesses + ". " + (this.isWinner() ? "WINNER!" : "");
		
	}
	public void forfeit() {
		guesses = 11;
		forfeit = true;
		
	}
	
	
	
}
