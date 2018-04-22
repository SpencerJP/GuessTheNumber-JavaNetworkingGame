package client;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Console {
	
	Scanner scanner;
	public Console() {
		scanner = new Scanner(System.in);
	}
	
	public int getInt() {
		boolean quit = false;
		while(!quit) {
			try {
				int toReturn = scanner.nextInt();
				return toReturn;
			}
			catch(InputMismatchException e) {
				System.out.println("Not a number, try again.");
				continue;
			}
			
		}
		return -1; // should never reach this, fix this for later so user doesn't get stuck in a loop
	}
	
	public String getString() {
		return scanner.nextLine();
	}
	
	
	
	
}	
