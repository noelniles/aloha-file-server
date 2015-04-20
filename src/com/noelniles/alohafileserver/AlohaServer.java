package com.noelniles.alohafileserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class AlohaServer {
	static int serverPort = 12346;
	private static String logoutMessage = "You are logged out. BYE BYE.";
	private static String loginMessage = "You are logged in";
	static String message;

	public static void main(String[] args) {

		ServerSocket myConnectionSocket = null;

		if (args.length == 1) {
			serverPort = Integer.parseInt(args[0]);
		}

		try {
			myConnectionSocket = new ServerSocket(serverPort);

			System.out.println(Messages.getString("AlohaServer.SRVRDY")); //$NON-NLS-1$

			while (true) {
				// wait to accept a connection
				System.out.println(Messages.getString("AlohaServer.SRVWAITING")); //$NON-NLS-1$

				AlohaSocket myDataSocket = null;

				try {
					myDataSocket = new AlohaSocket(myConnectionSocket.accept());
					
				} catch (IOException e) {
					System.out.println(Messages.getString("AlohaServer.IOEXCEPT")); //$NON-NLS-1$
					e.printStackTrace();
					
				} catch (SecurityException e) {
					System.out.println(Messages.getString("AlohaServer.SECEXEPT") + ""); //$NON-NLS-1$
					
				}

				System.out.println(Messages.getString("AlohaServer.CONNSUCCESS")); //$NON-NLS-1$

				boolean done = false;

				while (!done) {

					message = (myDataSocket.receiveMessage()).trim();

					switch (message) {
						case "400":
							myDataSocket.sendMessage(logoutMessage);
							//myDataSocket.close();
							done = true;
							break;
	
						case "100":
							// Login
							myDataSocket.sendMessage(loginMessage);
							break;
	
						case "200":
							Random gen = new Random();
							int randPrefix = gen.nextInt(1000);
							
							File outFile = new File(Messages.getString("AlohaServer.SRVFILES") //$NON-NLS-1$
									+ randPrefix + ".txt"); //$NON-NLS-1$
							
							myDataSocket.receiveFile(outFile);
						
							myDataSocket.sendMessage(Messages.getString("AlohaServer.11") //$NON-NLS-1$
									
									+ outFile.length() + Messages.getString("AlohaServer.12")); //$NON-NLS-1$
							
							//myDataSocket.sendMessage(Messages.getString("AlohaServer.13")); //$NON-NLS-1$
							
							break;
					}
				} // end while !done
			} // end while forever
		} catch (IOException ex) {
			System.out.println(Messages.getString("AlohaServer.14") //$NON-NLS-1$
					+ serverPort);
			ex.printStackTrace();

		}
	} // end main
}
