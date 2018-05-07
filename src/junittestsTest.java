import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import server.GameEngine;

public class junittestsTest {

	GameEngine g = new GameEngine();

	@Test
	public void test() {
		g.currentCode = "4567";
		
		String guessAttempt = "4890";
		System.out.println(g.playerGuess(guessAttempt));
		assert(g.playerGuess(guessAttempt).equals("Correct Places: 1, Incorrect Places: 0"));
		
		guessAttempt = "4865";
		System.out.println(g.playerGuess(guessAttempt));
		assert(g.playerGuess(guessAttempt).equals("Correct Places: 2, Incorrect Places: 1"));
		
		guessAttempt = "0128";
		System.out.println(g.playerGuess(guessAttempt));
		assert(g.playerGuess(guessAttempt).equals("Correct Places: 0, Incorrect Places: 0"));
		
		guessAttempt = "4567";
		System.out.println(g.playerGuess(guessAttempt));
		assert(g.playerGuess(guessAttempt).equals("correct!"));
	}

}
