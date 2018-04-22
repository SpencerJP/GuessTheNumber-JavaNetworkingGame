package server;

public class ServerGameThread extends Thread {

	
	
	private ServerController c;

	public ServerGameThread(ServerController serverController) {
		this.c = serverController;
	}

}
