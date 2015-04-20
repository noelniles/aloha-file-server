package com.noelniles.alohafileserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class AlohaClient {

  public static void main(String[] args) {

    InputStreamReader is = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(is);

    File file = new File(res.str("path.CLNTFILES"));

    try {
      System.out.println(res.str("usr.WELCOME") //$NON-NLS-1$
          + res.str("usr.ASKHNAME")); //$NON-NLS-1$

      String hostName = br.readLine();

      if (hostName.length() == 0) {
        // If user did not enter a name
        // use the default host name
        hostName = res.str("addr.IP");
      }

      // Asks for the port number. If this is blank the default port
      // number from res.properties is used.
      System.out.println(res.str("usr.ASKPNUM")); //$NON-NLS-1$
      String portNum = br.readLine();

      if (portNum.length() == 0) {
        // default port number
        portNum = res.str("addr.PORT"); //$NON-NLS-1$
      }

      AlohaSocket socket = null;      
      socket = new AlohaSocket(InetAddress.getByName(hostName), Integer.parseInt(portNum));

      // Used to hold res to be sent to the server
      String message = new String();

      // Used to hold responses from the server.
      String response = new String();

      while (!message.equalsIgnoreCase("aloha")) {
        System.out.println(res.str("usr.LOGINMSG")); //$NON-NLS-1$
        message = br.readLine().trim();
      }

      // Says "aloha" to the server
      socket.sendMessage(message);
      response = socket.receiveMessage();

      // Sends a file to the server after getting a "welcome" reply.
      if (response.equalsIgnoreCase("welcome")) {
        socket.sendFile(file);
        response = socket.receiveMessage();

        System.out.println(response);

        // Closes the connection if the server acknowledges a successful upload.
        if (response.contains(file.length() + " bytes has been uploaded correctly")) {
          System.out.println("We're all pau.");
          socket.close();
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } // end try/catch
  } // end main
}
