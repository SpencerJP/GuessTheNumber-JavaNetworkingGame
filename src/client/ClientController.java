package client;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import shared.GameState;
import shared.MessageType;
import shared.NetMessage;

public class ClientController {
	
	private Logger logger = Logger.getLogger("client-info");

	String server;
	int port;
	String username;;
	ClientConnectionThread cct;
	GameState state = GameState.WAITINGFORPLAYERS;
	int digitSize = -1;
	int remainingGuesses = 10;
	NetMessage netmsg;
	String currentBroadcast = null;
	boolean chooseDigitSize;
	String nextGuess = "initial";
	
	private Console console;



	public ClientController(String server, int port) {
		this.port = port;
		this.server = server;
		this.console = new Console();
	}
	
	public void start() {
		System.out.println("What would you like your username to be?");
		try {

			// get username
			username = console.getString();
		} catch (IOException e) {
			log("Issue with IO");
		}
		log("starting connection thread");
		cct = new ClientConnectionThread(this);
		cct.start();
		while(true) {
			if (currentBroadcast != null) {
				System.out.println(currentBroadcast);
				currentBroadcast = null;
			}
			if (chooseDigitSize) {
				chooseDigitSizeMethod();
			}
				switch(state) {
					case WAITINGFORPLAYERS:
						break;
					case INGAME:
						if(!nextGuess.equals("initial") && !nextGuess.equals("lastFailed") ) { // need to wait for response from server;
							synchronized(nextGuess) {
								try {
									nextGuess.wait();
								} catch (InterruptedException e) {
									log("interruptedexception: " + e.getMessage());
								}
								if (currentBroadcast.toLowerCase().equals("correct!")) {
									System.out.println(currentBroadcast);
									remainingGuesses = 0;
									currentBroadcast = null;
								}
								if (currentBroadcast != null) {
									System.out.println(currentBroadcast);
									currentBroadcast = null;
								}
							}
						}
						if (remainingGuesses != 0) {
							System.out.println("Please make a guess, the digit size is " + digitSize + ". " + remainingGuesses + " guesses remaining.");
							boolean success = getGuess();
							if (success) {
								nextGuess = "notInitial";
							}
							else {
								nextGuess = "lastFailed";
							}
							break;
						}
					case FINISHEDGAME:
						reset();
						break;
					default:
						break;
				
				}
			}
		}
	
	private void chooseDigitSizeMethod() {
		
		try {
			System.out.println("Because you are the first player, the server wants you to pick the digit size for this round.");
			int input;
			input = console.getInt();
			while(input < 3 || input > 8) {
				System.out.println("Digit size must be between 3 and 8 inclusive.");
				input = console.getInt();
			}
			String digitSize = Integer.toString(input);
			netmsg = new NetMessage(MessageType.FIRSTPLAYERCHOOSESDIGITSIZE, digitSize);
			cct.send(netmsg);	
			chooseDigitSize = false;
		} catch (IOException e) {
			log("Issue with IO.");
		}
		
	}

	private boolean getGuess() {
		String input;
		try {
			input = console.getString();
			if (input.equals("q")) {
					netmsg = new NetMessage(MessageType.FORFEIT, "");
					cct.send(netmsg);
					return true;
			} else {
				boolean valid = validateGuess(input);
				if (valid) {
						netmsg = new NetMessage(MessageType.GUESS, input);
						cct.send(netmsg);
						return true;
				}
			}
		} catch (IOException e) {
			log("Issue with IO.");
			return false;
		}
		return false;
		
	}
	
	private boolean validateGuess(String s) {
		// check that it is the right size
		if (s.length() != digitSize) {
			System.out.println("This guess is not the correct size!");
			return false;
		}
		
		// check that it is a number
		try {
			@SuppressWarnings("unused") // we only use this to test if it is numerical
			int guess = Integer.parseInt(s);
		}
		catch(NumberFormatException e) {
			System.out.println("This guess is not a valid number!");
			return false;
		}
		
		return true;
	}

	private void reset() {
		digitSize = -1;
		remainingGuesses = 10;
		state = GameState.WAITINGFORPLAYERS;
	}
	void log(String s) {
		//logger.log(Level.INFO, s);
	}
}
