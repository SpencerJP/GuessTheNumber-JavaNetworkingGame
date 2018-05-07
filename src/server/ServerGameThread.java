package server;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import shared.MessageType;
import shared.NetMessage;

public class ServerGameThread extends Thread {

	private Logger logger = Logger.getLogger("server-info");
	
	private ServerController c;

	public ServerGameThread(ServerController serverController) {
		this.c = serverController;
	}

	public void run() {
		preGame();
	}
	
	public void preGame() {
		log("Setting up game in ServerGameThread.");
		for(int i = 0; i < ServerController.MAX_INGAME_PLAYERS; i++) {
			
			ServerConnectionThread ct = c.getLobbyQueue().poll(); // get first in queue
			c.getCurrentPlayers().add(ct); // add them to the current player list
			c.getLobbyQueue().offer(ct); // move them to back of queue
			
			ct.ingame = true;
			if (i == 0) { // if first player, tell them to pick the digit size
				log("Waiting for the first player to give us the digit size.");
				NetMessage chooseDigitSize = new NetMessage(MessageType.FIRSTPLAYERCHOOSESDIGITSIZE, ".");
				ct.hasPermissionToSelectDigits = true;
				ct.sendMsg(chooseDigitSize);
				synchronized(c.getGameEngine()) {
			            try { 
							c.getGameEngine().wait();
							// digits have been decided, begin game
							inGame();
						} catch (InterruptedException e) {
							c.log("Interruption error!");
						}
	            	}
			}
		
			
		}
	}
	
	public void inGame() {
		// Changing to ingame state.
		log("Alerting clients that we are in game state. ignore: " + c.getCurrentPlayers().size());
		Iterator<ServerConnectionThread> iter = c.getCurrentPlayers().iterator();
		while(iter.hasNext()) {
			ServerConnectionThread ct = iter.next();
			NetMessage digitSizeUpdate = new NetMessage(MessageType.DIGITSIZE, Integer.toString(c.digitSize));
			ct.sendMsg(digitSizeUpdate);
			NetMessage gameStateUpdate = new NetMessage(MessageType.GAMESTATEUPDATE, "ingame");
			ct.sendMsg(gameStateUpdate);
		}
		boolean exit = false;
		while (!exit) {
			exit = checkInGameStatus();
		}
		postGame();
	}
	
	public void postGame() {
		NetMessage n = new NetMessage(MessageType.MESSAGE, c.buildScoreBoard());
		c.broadcast(n, false);
		NetMessage n2 = new NetMessage(MessageType.GAMESTATEUPDATE, "finishedgame");
		c.broadcast(n2, true);
		c.getCurrentPlayers().clear();
	}
	
	public boolean checkInGameStatus() {
		Iterator<ServerConnectionThread> iter = c.getCurrentPlayers().iterator();
		while(iter.hasNext()) {
			ServerConnectionThread ct = iter.next();
			if (ct.ingame == true) {
				return false;
			}
		}
		return true;
	}
	private void log(String s) {
		logger.log(Level.INFO, s);
	}
}
