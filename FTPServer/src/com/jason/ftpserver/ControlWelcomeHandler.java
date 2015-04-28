package com.jason.ftpserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class ControlWelcomeHandler implements Runnable {
	
	private static Random random;
	
	private ServerSocket controlServerSocket;
	private PrintWriter writer;
	
	static {
		random = new Random();
	}
	
	public ControlWelcomeHandler(int port) throws UnknownHostException, IOException {
		controlServerSocket = new ServerSocket(port,10,Inet4Address.getLocalHost());
		System.out.println("Control welcome socket started with address " + controlServerSocket.getInetAddress() + 
				" port " + controlServerSocket.getLocalPort());
	}	
		
	@Override
	public void run() {
		while(true) {
			Socket socket;
			try {
				socket = controlServerSocket.accept();
				System.out.println("Control socket connection attempt from " 
						+ socket.getInetAddress() + " port " + socket.getPort());
				socket.setSoTimeout(5000); //read timeout
				
				//send connection ID to client.  He/She will need this when opening a data socket
				Long connectionId = random.nextLong();
				writer = new PrintWriter(socket.getOutputStream(),true);
				writer.println(connectionId.toString());
				Main.conMap.put(connectionId, socket);
				
			} catch (IOException e) {
				throw new IllegalStateException("Control Sever socket hadler io exception while running: " + e);
			}
		}
	}
}
