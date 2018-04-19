package server;

public class ServerMain {
	public static void main(String[] args) {
		ServerController s = new ServerController(19519);
		s.start();
	}
}
