package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Console {
	
	private BufferedReader br;
	public Console() {
		br = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public int getInt() throws IOException {
		boolean quit = false;
		while(!quit) {
			try {
				String line;
				while ((line = br.readLine()) != null) {
					return Integer.parseInt(line);
				}
			}
			catch(NumberFormatException e) {
				System.out.println("Not a number, try again.");
				continue;
			}
			
		}
		return -1; // should never reach this, fix this for later so user doesn't get stuck in a loop
	}
	
	public String getString() throws IOException {
        String line;
		while ((line = br.readLine()) != null) {
            return line;
        }
		return "";
	}
	
	
	
	
}	
