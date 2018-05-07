package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import shared.GameState;
import shared.NetMessage;

public class ClientConnectionThread extends Thread {
	
	ClientController c;
	public Socket socket;
	private ObjectInputStream objectInput;
	private ObjectOutputStream objectOutput;		// socket object
	
	public ClientConnectionThread(ClientController clientController) {
		this.c = clientController;
	}
	
	public void run() {
		try {
			c.log("attempting server connection.");
			socket = new Socket(c.server, c.port);
		} 
		catch(Exception ec) {
			c.log("Error connecting to server:" + ec);
		}
		c.log("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
		
		try
		{
			objectInput  = new ObjectInputStream(socket.getInputStream());
			objectOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			c.log("Exception creating new Input/Output Streams: " + eIO);
		}
		
		try
		{
			objectOutput.writeObject(c.username);
		}
		catch (IOException eIO) {
			c.log("Exception doing login : " + eIO);
		}
		
		while(true) {
			try {
				// get the message from datastream
				NetMessage msg = (NetMessage) objectInput.readObject();
				processNetMessage(msg);
			}
			catch(IOException e) {
				c.log("Server closed connection.");
				c.currentBroadcast = "Server closed connection.";
				break;
			}
			catch(ClassNotFoundException e2) {
				c.log("Server sent corrupt message!" + " " + e2);
			}
		}
	}
	
	
	private void processNetMessage(NetMessage msg) {
		c.log("Received netmessage from server on port " + socket.getLocalPort());
		switch(msg.getMessageType()) {
			case GAMESTATEUPDATE:
				c.log("Received GAMESTATEUPDATE");
				if (msg.getMessage().equals("waitingforplayers")) {
					c.state = GameState.WAITINGFORPLAYERS;
				} else if(msg.getMessage().equals("ingame")) {
					c.state = GameState.INGAME;
				} else if(msg.getMessage().equals("finishedgame")) {
					c.state = GameState.FINISHEDGAME;
				}
				break;
			case MESSAGE:
				c.log("Received MESSAGE");
				c.currentBroadcast = msg.getMessage();
				break;
			case WINNER:
				c.log("Received WINNER");
				c.currentBroadcast = msg.getMessage();
				c.remainingGuesses = 0;
				break;
			case OUTOFGUESSES:
				c.log("Received OUTOFGUESSES");
				c.currentBroadcast = msg.getMessage();
				c.remainingGuesses = 0;
				break;
			case DIGITSIZE:
				c.log("Received DIGITSIZE");
				c.digitSize = Integer.parseInt(msg.getMessage());
				break;
			case FIRSTPLAYERCHOOSESDIGITSIZE:
				c.log("Received FIRSTPLAYERCHOOSESDIGITSIZE");
				c.chooseDigitSize = true;
				break;
			case GUESS:
				c.log("Received GUESS");
				c.currentBroadcast = msg.getMessage();
				c.remainingGuesses = c.remainingGuesses - 1;
				synchronized(c.nextGuess) {
					c.nextGuess.notify();
					c.nextGuess = "notInitial";
				}
			default:
				break;
		}
	}

	/* method to send netmessages to server */
	public void send(NetMessage netmsg) {
		try {
			objectOutput.writeObject(netmsg);
			c.log("Client sent a NetMessage to the server on port " + socket.getLocalPort() + ".");
		}
		catch(IOException eIO) {
			c.log("Exception writing to server: " + eIO);
		}
	}
}
