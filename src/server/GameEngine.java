package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameEngine {
	public static final int MIN_DIGITS = 3;
	public static final int MAX_DIGITS = 8;
	
	String currentCode;
	HashMap<ClientConnectionThread, String> scoreBoard = new HashMap<ClientConnectionThread, String>();
	
	
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
	
	public String playerGuess(String guessAttempt, ClientConnectionThread player) {
		
		if (guessAttempt.equals(currentCode)) {
			updateScoreboard("win", player);
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
	
	private void updateScoreboard(String string, ClientConnectionThread player) {
		// TODO Auto-generated method stub
		
	}

	public String constructScoreBoard() {
		return new String();
	}
}
