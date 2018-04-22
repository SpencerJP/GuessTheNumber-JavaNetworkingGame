package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import shared.*;


public class ServerController {

	private Logger logger = Logger.getLogger("server-info");
	
	private LinkedList<ClientConnectionThread> lobbyQueue = new LinkedList<ClientConnectionThread>();

	private ArrayList<ClientConnectionThread> currentPlayers = new ArrayList<ClientConnectionThread>();
	
	private int port;
	private GameEngine g;
	private int uniqueId = 0;
	private ServerGameThread s = null;
	
	public ServerController(int port) {
		this.port = port;
		this.g = new GameEngine();
	}
	
	public void start() {
		boolean quit = false;
		
		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);

			while(!quit) 
			{
				
				if (lobbyQueue.size() >= 3 && s == null) {
					log("Got enough players, starting game...");
					for(int i = 0; i < 3; i++) {
						
						ClientConnectionThread c = lobbyQueue.poll(); // get first in queue
						
						c.ingame = true;
						NetMessage gameStateUpdate = new NetMessage(MessageType.GAMESTATEUPDATE, "1");
						c.sendMsg(gameStateUpdate);
						if (i == 0) { // if first player, tell them to pick the digit size
							NetMessage chooseDigitSize = new NetMessage(MessageType.FIRSTPLAYERCHOOSESDIGITSIZE, ".");
							c.sendMsg(chooseDigitSize);
						}
					
						currentPlayers.add(c); // add them to the current player list
						lobbyQueue.offer(c); // move them to back of queue
						
					}
				}
				
				log("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();
				
				if(!quit)
					break;
				ClientConnectionThread t = new ClientConnectionThread(this, socket, ++uniqueId);
				
				lobbyQueue.add(t);
				
				t.start();
			}
			try {
				serverSocket.close();
				Iterator<ClientConnectionThread> iter = lobbyQueue.iterator();
				while(iter.hasNext()) {
					ClientConnectionThread tc = iter.next();
					try {
						tc.closeStreams();
					} catch(Exception e) {
						log("Exception while closing client: " + e);
					}
					
				}
			} catch(IOException e) {
				log("Exception while closing client: " + e);
			}
		} catch(IOException e) {
				log("Exception on new ServerSocket: " + e.getMessage());
			}
			
	}

	public GameEngine getGameEngine() {
		return g;
	}
	
	public LinkedList<ClientConnectionThread> getLobbyQueue() {
		return lobbyQueue;
	}
	
	public void broadcast(NetMessage msg, boolean onlyInGame) {
		if (onlyInGame) {
			for(int i = lobbyQueue.size(); --i >= 0;) {
				ClientConnectionThread c = currentPlayers.get(i);
				// try to write to the Client if it fails remove it from the list
				if(!c.sendMsg(msg)) {
					lobbyQueue.remove(i);
					log("ClientID: " + c.uniqueId + " not found, removing them from lobby.");
				}
			}
		}
		else {
			for(int i = lobbyQueue.size(); --i >= 0;) {
				ClientConnectionThread c = lobbyQueue.get(i);
				// try to write to the Client if it fails remove it from the list
				if(!c.sendMsg(msg)) {
					lobbyQueue.remove(i);
					log("ClientID: " + c.uniqueId + " not found, removing them from lobby.");
				}
			}
		}
	}

	private void log(String s) {
		logger.log(Level.INFO, s);
	}
}
