package com.jason.ftpserver;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DataWelcomeHandler implements Runnable {
	
private ServerSocket dataServerSocket;
private Scanner scanner;
	
	public DataWelcomeHandler(int port) throws UnknownHostException, IOException {
		dataServerSocket = new ServerSocket(port,10,Inet4Address.getLocalHost());
		System.out.println("Data welcome socket started with address " + dataServerSocket.getInetAddress() + 
				" port " + dataServerSocket.getLocalPort());
	}	
		
	@Override
	public void run() {
		while(true) {
			Socket dataSocket = null;
			Socket ctrlSocket = null;
			try {
				dataSocket = dataServerSocket.accept();
				System.out.println("Data connection attempt from " + dataSocket.getInetAddress() + 
						" port " + dataSocket.getPort());
				dataSocket.setSoTimeout(5000); //read timeout of 5 seconds
				scanner = new Scanner(dataSocket.getInputStream());
				Long connectionId = scanner.nextLong();
				
				ctrlSocket = Main.conMap.get(connectionId);
				
				if (ctrlSocket == null) {
					System.out.println("Data connection attempt failed from " + 
							dataSocket.getInetAddress() + " port " + dataSocket.getPort());
					dataSocket.close();
				} else {
					System.out.println("Data connection completed from " +
							dataSocket.getInetAddress() + " port " + dataSocket.getPort());
					Main.conMap.remove(connectionId);
					new Thread(new ServerSession(ctrlSocket,dataSocket)).start();
				}
			} catch (SocketTimeoutException e) {
				System.out.println("Read of data socket timed out... closing data Socket");
				try {
					if (dataSocket != null && !dataSocket.isClosed())					
						dataSocket.close();
					} catch (IOException e1) {
						throw new IllegalStateException("Problem closing data socket from timed out connection: " + e1);
					}
			} catch (IOException e) {
				throw new IllegalStateException("Data Sever socket hadler io exception while running: " + e);
			} 
		}
	}
}
