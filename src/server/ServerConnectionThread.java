package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.Port;

import shared.*;

public class ServerConnectionThread extends Thread {
	

	private Logger logger = Logger.getLogger("server-info");

	private ServerController c; // reference to controller for accessing controller/gameengine methods
	
	public Socket socket;
	private ObjectInputStream objectInput;
	private ObjectOutputStream objectOutput;
	

	public String clientName;
	public int uniqueId;
	
	private Score score;
	
	private NetMessage netmsg;
	
	private boolean quit = false;
	
	public boolean readyForPostGame = false;

	public boolean ingame = false;
	
	 // without this the player could hack the program and force it to start with the digits it wanted
	public boolean hasPermissionToSelectDigits = false;

	public ServerConnectionThread(ServerController controller, Socket socket, int uniqueId) {
		this.c = controller;
		this.socket = socket;
		this.uniqueId = uniqueId;
		this.score = new Score();
		try
		{
			objectOutput = new ObjectOutputStream(socket.getOutputStream());
			objectInput  = new ObjectInputStream(socket.getInputStream());
			// get the client's name
			clientName = (String) objectInput.readObject();
		}
		catch(IOException e) {
			log("Could not create input/output streams");
		}
		catch (ClassNotFoundException e) {
		}
	}

	public void run() {
		log("Thread created on port " + socket.getPort() + " for client");
		while(!quit) {
			try {
				netmsg = (NetMessage) objectInput.readObject();
			}
			catch (IOException e) {
				log(clientName + " Exception reading Streams: " + e);
				break;				
			}
			catch(ClassNotFoundException e2) {
				break;
			}
			
			String message = netmsg.getMessage();
			log("Server received NetMessage from client on port " + socket.getLocalPort());
			switch(netmsg.getMessageType()) {
				
			case GUESS:
				log("Received guess message from client.");

				if (getScore().getGuesses() >= 10) {
					// user has either won or lost already, and can no longer guess!
					ingame = false;
					NetMessage responseMessage = new NetMessage(MessageType.OUTOFGUESSES, "You can no longer guess, as you are out of guesses.");
					sendMsg(responseMessage);
					break;
				}
				if (getScore().isWinner()) {
					// user has either won or lost already, and can no longer guess!
					ingame = false;
					NetMessage responseMessage = new NetMessage(MessageType.WINNER, "You have won this round!");
					sendMsg(responseMessage);
					break;
				}
				if (!ingame) { // check if the client is even allowed to send guess messages
					break;
				}
				String guessResponse = this.c.getGameEngine().playerGuess(message);
				updateScore(guessResponse);
				if (guessResponse.equals("correct!")) {
					ingame = false;
				}
				NetMessage responseMessage = new NetMessage(MessageType.GUESS, guessResponse);
				sendMsg(responseMessage);
				break;
			case FIRSTPLAYERCHOOSESDIGITSIZE:
				log("Received digit size message from client.");
				if (hasPermissionToSelectDigits) {
					try {
						int digits = Integer.parseInt(message); 
						c.digitSize = digits;
						synchronized(c.getGameEngine()) {
							c.getGameEngine().generateCode(digits);
							log("Code is " + c.getGameEngine().currentCode);
							c.getGameEngine().notify();
						};

						hasPermissionToSelectDigits = false;
												
					}
					catch (NumberFormatException e) {
						NetMessage responseMessage1 = new NetMessage(MessageType.MESSAGE, "Not valid digits.");
						sendMsg(responseMessage1);
					}
				}
				break;
			case FORFEIT:
				updateScore("forfeit");
				ingame = false;
				break;
			default:
				break;
			}
		}
 		log("Thread connection with " + clientName + " closed.");
	}
	
	public boolean sendMsg(NetMessage msg) {
		// if Client is still connected send the message to it
		try {
			if(!socket.isConnected()) {
				closeStreams();
				return false;
			}
			
		}
		catch(Exception e) {
			log("Could not write message and failed to abort. Could not close streams.");
			return false;
		}
		
		
		try {
			objectOutput.writeObject(msg);
		}
		catch(IOException e) {
			log("Error sending message to client, username: " + clientName);
		}
		log("Sent message from server to port " + socket.getPort());
		return true;
	}

	public void closeStreams() throws Exception{
		socket.close();
		objectInput.close();
		objectOutput.close();
		
	}
	
	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}
	
	void updateScore(String string) {
		if (string.equals("win")) {
			getScore().incrementGuesses();
			getScore().setWinner(true);
			return;
		}
		if (string.equals("forfeit")) {
			getScore().forfeit();
			return;
		}

		getScore().incrementGuesses();
	}

	
	private void log(String s) {
		logger.log(Level.INFO, String.format("ClientID: %s, %s", uniqueId, s));
	}

}
