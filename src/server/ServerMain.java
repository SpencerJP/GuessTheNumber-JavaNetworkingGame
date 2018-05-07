package server;

public class ServerMain {
	public static void main(String[] args) {
		ServerController s = new ServerController(40000);
		s.start();
	}
}
