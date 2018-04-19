package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerController {

	private Logger logger = Logger.getLogger("server-info");
	
	private LinkedList<ClientConnectionThread> lobbyQueue = new LinkedList<ClientConnectionThread>();

	private int port;
	private GameEngine g;
	
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
				log("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();
				
				if(!quit)
					break;
				ClientConnectionThread t = new ClientConnectionThread(socket);
				
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



	private void log(String s) {
		logger.log(Level.INFO, s);
	}
}
