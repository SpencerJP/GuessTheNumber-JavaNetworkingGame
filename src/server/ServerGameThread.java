package server;

import java.util.Iterator;

import shared.MessageType;
import shared.NetMessage;

public class ServerGameThread extends Thread {

	
	
	private ServerController c;

	public ServerGameThread(ServerController serverController) {
		this.c = serverController;
	}

	public void run() {
		preGame();
	}
	
	public void preGame() {
		for(int i = 0; i < ServerController.MAX_INGAME_PLAYERS; i++) {
			
			ClientConnectionThread ct = c.getLobbyQueue().poll(); // get first in queue
			
			ct.ingame = true;
			NetMessage gameStateUpdate = new NetMessage(MessageType.GAMESTATEUPDATE, "1");
			ct.sendMsg(gameStateUpdate);
			if (i == 0) { // if first player, tell them to pick the digit size
				NetMessage chooseDigitSize = new NetMessage(MessageType.FIRSTPLAYERCHOOSESDIGITSIZE, ".");
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
		
			c.getCurrentPlayers().add(ct); // add them to the current player list
			c.getLobbyQueue().offer(ct); // move them to back of queue
			
		}
	}
	
	public void inGame() {
		Iterator<ClientConnectionThread> iter = c.getCurrentPlayers().iterator();
		while(iter.hasNext()) {
			ClientConnectionThread ct = iter.next();
			NetMessage gameStateUpdate = new NetMessage(MessageType.GAMESTATEUPDATE, "2");
			ct.sendMsg(gameStateUpdate);
		}
		while(true) {
			Iterator<ClientConnectionThread> iter2 = c.getCurrentPlayers().iterator();
			while(iter.hasNext()) {
				ClientConnectionThread ct = iter2.next();
				if (!ct.ingame) {
					iter2.remove();
				}
			}
		}
	}
	
	public void postGame() {
		
		c.getCurrentPlayers().clear();
	}
}
