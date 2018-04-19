package server;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine {
	public static final int MIN_DIGITS = 3;
	public static final int MAX_DIGITS = 8;
	
	
	public GameEngine() {
		
	}
	
	public int generateCode(int digitCount) {
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
	    
	    return Integer.parseInt(randomNumString);
	}
	
		
}
