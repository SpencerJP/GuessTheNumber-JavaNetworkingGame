package client;


public class ClientMain {
	public static void main(String[] args) {
		ClientController c = new ClientController("127.0.0.1",40000);
		c.start();
	}
}
