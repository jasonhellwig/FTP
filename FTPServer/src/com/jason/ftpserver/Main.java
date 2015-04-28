package com.jason.ftpserver;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
	
	public static ConcurrentHashMap<Long, Socket> conMap = new ConcurrentHashMap<Long,Socket>();

	public static void main(String[] args) {
		
		//set up listening socket
		try {
					
			new Thread(new ControlWelcomeHandler(25060)).start();
			new Thread(new DataWelcomeHandler(25061)).start();

		} catch (IOException e) {
			throw new IllegalStateException("Problem creating server welcoming sockets: " + e);
		}

	}
	
}
