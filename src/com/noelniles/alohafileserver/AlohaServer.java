package com.noelniles.alohafileserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

public class AlohaServer {
  //This should be 1024-65535 because lower ports are used by many common programs
  private static int serverPort = Integer.parseInt(res.str("addr.PORT")); //$NON-NLS-1$
  
  //Holds responses that are sent to the client
  private static String message;

  // Generates a unique name
  private static String uniqName() {
    UUID uuid = UUID.randomUUID();
    return res.str("path.SRVFILES") //$NON-NLS-1$
        + "-" + uuid + ".txt";
  }

  public static void main(String[] args) {

    if (args.length == 1) {
      serverPort = Integer.parseInt(args[0]);
    }
    
    // Uses try-with-resources so there is no need to call close() on the socket.
    try (ServerSocket socket = new ServerSocket(serverPort)) {
      System.out.println(res.str("usr.SRVRDY")); //$NON-NLS-1$

      while (true) {
        // wait to accept a connection
        System.out.println(res.str("usr.SRVWAITING")); //$NON-NLS-1$

        AlohaSocket stream = null;
        try {
          stream = new AlohaSocket(socket.accept());
        } catch (IOException e) {
          System.out.println(res.str("err.IOEXCEPT")); //$NON-NLS-1$
          e.printStackTrace();
        } catch (SecurityException e) {
          System.out.println(res.str("err.SECEXCEPT") + ""); //$NON-NLS-1$					
        }
        
        // Connection succeeded
        System.out.println(res.str("conn.SUCCESS")); //$NON-NLS-1$

        boolean done = false;
        while (!done) {
          // Receives a message from the client. The server is expecting an "aloha" message.
          message = (stream.receiveMessage()).trim();

          // Checks for "aloha" message. Responds with "welcome". 
          // Then receives a file from the client.
          if (message.equals("aloha")) {
            stream.sendMessage("welcome");
            File outFile = new File(uniqName());
            stream.receiveFile(outFile);
            stream.sendMessage("Your file named " + outFile + " with the size " + outFile.length()
                + " bytes has been uploaded correctly.");
            done = true;
          }
        } // End while !done
      } // End endless while
    } catch (IOException ex) {
      System.out.println(res.str("err.ADDR") //$NON-NLS-1$
          + serverPort);
      ex.printStackTrace();
    }
  } // end main
}
