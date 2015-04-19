package com.noelniles.alohafileserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AlohaClient {
	
	public static void main(String[] args) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);

        File file = new File("client-files/paper.txt"); //$NON-NLS-1$

        try {
            System.out.println(Messages.getString("AlohaClient.CLIENTWELCOME") //$NON-NLS-1$
                    + Messages.getString("AlohaClient.ASKHNAME")); //$NON-NLS-1$
            
            String hostName = br.readLine();
            
            if ( hostName.length() == 0 ) {
            	// If user did not enter a name
            	// use the default host name
                hostName = "localhost"; //$NON-NLS-1$
            }
            
            System.out.println(Messages.getString("AlohaClient.ASKPNUM")); //$NON-NLS-1$
            String portNum = br.readLine();
            
            if (portNum.length() == 0) {
            	// default port number
            	portNum = Messages.getString("AlohaClient.PORTNUM");	 //$NON-NLS-1$
            }
            
            AlohaSocket socket = null;
            
			try {
				socket = new AlohaSocket(
				        InetAddress.getByName(hostName), Integer.parseInt(portNum));
			} catch (UnknownHostException e) {
				System.out.println(Messages.getString("AlohaClient.UNKHOSTERR")); //$NON-NLS-1$
				e.printStackTrace();
			}
			
            boolean done = false;
            String echo;
            
            while (!done) {

                System.out.println(Messages.getString("AlohaClient.CMDMENU")); //$NON-NLS-1$
                String message = br.readLine();
                
                switch (message) {
                
                case "100": //$NON-NLS-1$
                    System.out.println(Messages.getString("AlohaClient.LOGINMSG")); //$NON-NLS-1$
                    String login = br.readLine();
                    
                    if (login.charAt(0) == 'T') {
                        System.out.println(Messages.getString("AlohaClient.LOGINSUCCESS")); //$NON-NLS-1$
                    } else {
                        System.out.println(Messages.getString("AlohaClient.LOGINFAIL")); //$NON-NLS-1$
                    }
                    socket.sendMessage("100"); //$NON-NLS-1$
                    
                case "200": //$NON-NLS-1$
                    socket.sendMessage("200"); //$NON-NLS-1$
                    socket.sendFile(file);
                    break;
                    
                case "400": //$NON-NLS-1$
                    System.out.println(Messages.getString("AlohaClient.LOGOUTMSG")); //$NON-NLS-1$
                    done = true;
                    socket.sendMessage("400"); //$NON-NLS-1$
                    socket.close();
                    break;
                    
                default:
                	System.out.println(Messages.getString("AlohaClient.INVALIDCMD")); //$NON-NLS-1$
                	continue;
                }
                
                // get reply from server
                echo = socket.receiveMessage();
                System.out.println(echo);
            } // end while
        } // end try
        catch (Exception ex) {
            ex.printStackTrace();
        } // end catch
    } // end main
}
