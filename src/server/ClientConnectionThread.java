package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import shared.*;

public class ClientConnectionThread extends Thread {
	

	private Logger logger = Logger.getLogger("server-info");

	private ServerController c; // reference to controller for accessing controller/gameengine methods
	
	public Socket socket;
	private ObjectInputStream objectInput;
	private ObjectOutputStream objectOutput;
	
	public String clientName;
	public int uniqueId;
	
	private NetMessage netmsg;
	
	boolean quit = false;

	public boolean ingame = false;

	public ClientConnectionThread(ServerController controller, Socket socket, int uniqueId) {
		this.c = controller;
		this.socket = socket;
		this.uniqueId = uniqueId;
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
			
			switch(netmsg.getMessageType()) {
				
			case MESSAGE:
				//stuff
			case GUESS:
				String guessResponse = this.c.getGameEngine().playerGuess(message);
				NetMessage responseMessage = new NetMessage(MessageType.GUESS, guessResponse);
				sendMsg(responseMessage);
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
		return true;
	}

	public void closeStreams() throws Exception{
		// TODO Auto-generated method stub
		
	}
	
	private void log(String s) {
		logger.log(Level.INFO, String.format("ClientID: %s, %s", uniqueId, s));
	}

}
