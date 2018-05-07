package server;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine {
	public static final int MIN_DIGITS = 3;
	public static final int MAX_DIGITS = 8;
	
	public String currentCode;
	
	
	public GameEngine() {
		
	}
	
	public String generateCode(int digitCount) {
		String randomNumString = "";
		ArrayList<String> digitsAlreadyUsed = new ArrayList<String>();

	    Random r = new Random();

	    //Generate the first digit from 1-9
	    
	    while((randomNumString.length() != digitCount)) {

		    String newInt = Integer.toString(r.nextInt(9));
		    
		    if (!digitsAlreadyUsed.contains(newInt)) {
		    	digitsAlreadyUsed.add(newInt);
			    randomNumString += newInt;
		    }
		    else {
		    	continue;
		    }
	    }
	    
	    currentCode = randomNumString;
	    return currentCode;
	}
	
	public String playerGuess(String guessAttempt) {
		
		if (guessAttempt.equals(currentCode)) {
			return "correct!";
		}
		int correctPlaces = 0;
		int incorrectPlaces = 0;
		for(int i = 0; i < currentCode.length(); i++) {
			char currentDigit = guessAttempt.charAt(i);
			if (currentCode.charAt(i) == currentDigit) {
				correctPlaces++;
			} else {
				for(int j = 0; j < currentCode.length(); j++) {
					if (currentCode.charAt(j) == currentDigit) {
						incorrectPlaces++;
					}
				}
			}
		}
		
		return "Correct Places: " + correctPlaces + ", Incorrect Places: " + incorrectPlaces;
	}
	
	void updateScoreboard(String string, ServerConnectionThread player) {
		if (string.equals("win")) {
			player.getScore().incrementGuesses();
			player.getScore().setWinner(true);
		}
		if (string.equals("guess")) {
			player.getScore().incrementGuesses();
		}
		if (string.equals("forfeit")) {
			player.getScore().forfeit();
		}

	public String constructScoreBoard() {
		return new String();
	}
}
