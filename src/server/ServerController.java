package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import shared.NetMessage;
import shared.Score;


public class ServerController {

	public static final int MAX_INGAME_PLAYERS = 2;

	private Logger logger = Logger.getLogger("server-info");
	
	private LinkedList<ServerConnectionThread> lobbyQueue = new LinkedList<ServerConnectionThread>();

	ArrayList<ServerConnectionThread> currentPlayers = new ArrayList<ServerConnectionThread>();
	
	private int port;
	private GameEngine g;
	private int uniqueId = 0;
	private ServerGameThread s = null;

	public int digitSize = -1; // digitSize to network through the program
	
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
				
				if (lobbyQueue.size() >= MAX_INGAME_PLAYERS && s == null) {
					log("Got enough players, starting game...");
					ServerGameThread sgt = new ServerGameThread(this);
					sgt.start();
				}
				
				log("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();
				log("Server accepting new client");
				ServerConnectionThread t = new ServerConnectionThread(this, socket, ++uniqueId);
				
				lobbyQueue.add(t);
				
				t.start();
			}
			try {
				serverSocket.close();
				Iterator<ServerConnectionThread> iter = lobbyQueue.iterator();
				while(iter.hasNext()) {
					ServerConnectionThread tc = iter.next();
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
	
	public LinkedList<ServerConnectionThread> getLobbyQueue() {
		return lobbyQueue;
	}
	
	public void broadcast(NetMessage msg, boolean onlyInGame) {
		if (onlyInGame) {
			for(int i = lobbyQueue.size(); --i >= 0;) {
				ServerConnectionThread c = getCurrentPlayers().get(i);
				// try to write to the Client if it fails remove it from the list
				if(!c.sendMsg(msg)) {
					lobbyQueue.remove(i);
					log("ClientID: " + c.uniqueId + " not found, removing them from lobby.");
				}
			}
		}
		else {
			for(int i = lobbyQueue.size(); --i >= 0;) {
				ServerConnectionThread c = lobbyQueue.get(i);
				// try to write to the Client if it fails remove it from the list
				if(!c.sendMsg(msg)) {
					lobbyQueue.remove(i);
					log("ClientID: " + c.uniqueId + " not found, removing them from lobby.");
				}
			}
		}
	}

	public ArrayList<ServerConnectionThread> getCurrentPlayers() {
		return currentPlayers;
	}

	void log(String s) {
		logger.log(Level.INFO, s);
	}

	public String buildScoreBoard() {
		String scoreboard = "";
		Iterator<ServerConnectionThread> iter = currentPlayers.iterator();
		while(iter.hasNext()) {
			ServerConnectionThread ct = iter.next();
			Score s = ct.getScore();
			scoreboard = scoreboard + ct.clientName + ": " + s.getGuesses() + " guesses" + (s.isWinner() || s.isForfeit() ? " - " + (s.isWinner() ? " WINNER\n" : " FORFEIT\n") : "\n");
		}
		return scoreboard;
	}
}
