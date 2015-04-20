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
import java.net.SocketException;

public class AlohaSocket {
	private Socket socket;

	private DataInputStream input;

	private DataOutputStream output;

	AlohaSocket(InetAddress acceptorHost, int acceptorPort)
			throws SocketException, IOException {
		socket = new Socket(acceptorHost, acceptorPort);
		setStreams();
	}

	AlohaSocket(Socket socket) throws IOException {
		this.socket = socket;
		setStreams();
	}

	private void setStreams() {
		// get an input stream for reading from the data socket
		try {
			input = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("***ERROR: There was an error getting the input stream");
			e.printStackTrace();
		}
		try {
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("***ERROR: There was an error setting up the output stream");
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		try {
			output.writeUTF(message);
			output.flush();
		} catch (IOException e) {
			System.out.println("***ERROR: There was an error sending the message");
			e.printStackTrace();
		}
	} // end sendMessage

	public String receiveMessage() {
		String message = new String();
		try {
			message = input.readUTF();
		} catch (IOException e) {
			System.out.println("***ERROR: There was an error reading message. Maybe the stream was closed.");
			e.printStackTrace();
		}
		return message;
	} // end receiveMessage

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("***ERROR: There was problem closing the socket");
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
			System.out.println("***ERROR: Could not create new file on server");
			e.printStackTrace();
		}
		byte[] buf = new byte[Short.MAX_VALUE];
		
		int bytesSent = 0;
		
		try {
			while ((bytesSent = input.readShort()) != -1){
				try {
					input.readFully(buf, 0, bytesSent);
					
				} catch (EOFException e) {
					System.out.println("***ERROR: Reached end of file before reading fully.");
					e.printStackTrace();
					
				} catch (IOException e){
					System.out.println("***ERROR: The stream is closed");
				}
				
				try {
					fileOut.write(buf, 0, bytesSent);
					
				} catch (IOException e) {
					System.out.println("***ERROR: There was error writing the output file");
					e.printStackTrace();
				}	
			}
			
		} catch (EOFException e) {
			System.out.println("***ERROR: Reached end of file before reading two bytes");
			
		} catch (IOException e) {
			System.out.println("***ERROR: There was a problem reading the input. Maybe the connection is closed");
		}
	}
}
