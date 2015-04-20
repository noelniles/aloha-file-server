package com.noelniles.alohafileserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class AlohaSocket {
  private Socket socket;
  private DataInputStream input;
  private DataOutputStream output;

  AlohaSocket(InetAddress acceptorHost, int acceptorPort) {
    try {
      socket = new Socket(acceptorHost, acceptorPort);
    } catch (IOException e) {
      System.out.println(res.str("err.IOEXCEPT"));
      e.printStackTrace();
    } catch (SecurityException e) {
      System.out.println(res.str("err.SECEXCEPT"));
    } catch (IllegalArgumentException e) {
      System.out.println(res.str("err.PORT"));
    } catch (NullPointerException e) {
      System.out.println("***ERROR: Address can't be empty");
    }
    setStreams();
  }

  AlohaSocket(Socket socket) {
    this.socket = socket;
    setStreams();
  }

  // Gets an input stream for reading from the data socket
  private void setStreams() {
    try {
      input = new DataInputStream(socket.getInputStream());
    } catch (IOException e) {
      System.out.println(res.str("err.INSTRM")); //$NON-NLS-1$
      e.printStackTrace();
    }
    try {
      output = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      System.out.println(res.str("err.OUTSTRM")); //$NON-NLS-1$
      e.printStackTrace();
    }
  }

  public void sendMessage(String message) {
    try {
      output.writeUTF(message);
      output.flush();
    } catch (IOException e) {
      System.out.println(res.str("err.SNDMSG")); //$NON-NLS-1$
      e.printStackTrace();
    }
  } // end sendMessage

  public String receiveMessage() {
    String message = new String();
    try {
      message = input.readUTF();
    } catch (IOException e) {
      System.out.println(res.str("err.RCVMSG")); //$NON-NLS-1$
      e.printStackTrace();
    }
    return message;
  } // end receiveMessage

  public void close() {
    try {
      socket.close();
    } catch (IOException e) {
      System.out.println(res.str("err.CLOSE")); //$NON-NLS-1$
      e.printStackTrace();
    }
  }

  public void sendFile(File file) throws IOException {
    FileInputStream fileIn = new FileInputStream(file);
    byte[] buf = new byte[Short.MAX_VALUE];
    int bytesRead;
    while ((bytesRead = fileIn.read(buf)) != -1) {
      output.writeShort(bytesRead);
      output.write(buf, 0, bytesRead);
    }
    output.writeShort(-1);
    fileIn.close();
  }

  public void receiveFile(File file) {
    FileOutputStream fileOut = null;

    try {
      fileOut = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      System.out.println(res.str("err.FILENOTFOUND")); //$NON-NLS-1$
      e.printStackTrace();
    }
    byte[] buf = new byte[Short.MAX_VALUE];

    int bytesSent = 0;

    try {
      while ((bytesSent = input.readShort()) != -1) {
        try {
          input.readFully(buf, 0, bytesSent);

        } catch (EOFException e) {
          System.out.println(res.str("err.EOF")); //$NON-NLS-1$
          e.printStackTrace();

        } catch (IOException e) {
          System.out.println(res.str("err.STRMCLOSED")); //$NON-NLS-1$
        }

        try {
          fileOut.write(buf, 0, bytesSent);

        } catch (IOException e) {
          System.out.println(res.str("err.WRTFILE")); //$NON-NLS-1$
          e.printStackTrace();
        }
      }
    } catch (EOFException e) {
      System.out.println(res.str("err.EOS")); //$NON-NLS-1$

    } catch (IOException e) {
      System.out.println(res.str("err.READIN")); //$NON-NLS-1$
    }
  }
}
