import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number

	public static void main(String[] args) throws Exception {
		System.out.println("Server started..\nWaiting for client connection...\n"); 
        	ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 1;
        	try {
            		while(true) {
                		new Handler(listener.accept(),clientNum).start();
				System.out.println("Client "  + clientNum + " is connected!");
				clientNum++;
            			}
        	}catch(Exception e)
        	{
        		System.out.println("Enter valid IP and Port");
        	}
        	finally {
            		listener.close();
        	} 
    	}

	/**
     	* A handler thread class.  Handlers are spawned from the listening
     	* loop and are responsible for dealing with a single client's requests.
     	*/
    	private static class Handler extends Thread {
        private String message;    //message received from the client
		private String MESSAGE;    //uppercase message send to the client
		private Socket connection;
        private ObjectInputStream myInputStream;	//stream read from the socket
        private ObjectOutputStream myOutputStream;    //stream write to the socket
		private int no;		//The index number of the client
		final static String SERVER_PATH = "C:\\Users\\13528\\Documents\\SocketProgramming\\Server"; 

        	public Handler(Socket connection, int no) {
            		this.connection = connection;
	    		this.no = no;
        	}

        public void run() {
 		try{
			//initialize Input and Output streams
			myOutputStream = new ObjectOutputStream(connection.getOutputStream());
			myOutputStream.flush();
			myInputStream = new ObjectInputStream(connection.getInputStream());
			try{
				while(true)
				{
					//receive the message sent from the client
					message = (String)myInputStream.readObject();
					//show the message to the user
					System.out.println("Receive message: " + message + " from client " + no);
					//Capitalize all letters in the message
					processClientRequest(message);
					MESSAGE = message.toUpperCase();
					//send MESSAGE back to the client
					// sendMessage(MESSAGE);
				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
		}
		catch(IOException ioException){
			System.out.println("Disconnect with Client " + no);
		}
		finally{
			//Close connections
			try{
				myInputStream.close();
				myOutputStream.close();
				connection.close();
			}
			catch(IOException ioException){
				System.out.println("Disconnect with Client " + no);
			}
		}
	}

	//send a message to the output stream
	public void sendMessage(String msg)
	{
		try{
			myOutputStream.writeObject(msg);
			myOutputStream.flush();
			System.out.println("Send message: " + msg + " to Client " + no);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void processClientRequest(String input) throws IOException, ClassNotFoundException {
		 String[] commandPart = input.split(" ");
		 switch(commandPart[0]) {
		 case "dir":
		     sendOutputToClient(documentsInServer());
			 break;
			 
		 case "upload":
			 makeNewFileAndWriteData(commandPart[1]);
	         break;
		 case "get":
			 writeFileToOutputStram(commandPart[1]);
			 break;
		 
		 default:
			System.out.println("Operation not supported");
			break;
		 }	
	}

	private void writeFileToOutputStram(String document) {
		try {
			File myDocument = new File(SERVER_PATH + "\\" + document);
			if(!myDocument.exists()) {
				sendOutputToClient("Absent");
				return;
			}
			sendOutputToClient("Present");
			InputStream is = new FileInputStream(myDocument); 	
	        sendOutputToClient(is.readAllBytes());
	        is.close();
		} catch(Exception e) {
			sendOutputToClient("Error while getting the file");
		}
    }

	private void makeNewFileAndWriteData(String document) throws IOException, ClassNotFoundException {
	if("Absent".equalsIgnoreCase((String) myInputStream.readObject())) {
      	 System.out.println("File is not present :(");
      	 return;
      }
		String fileName = SERVER_PATH + "\\" + document;
       BufferedOutputStream bufferedOS = new BufferedOutputStream(new FileOutputStream(fileName));
       
		bufferedOS.write((byte[]) myInputStream.readObject());
		bufferedOS.close();
		
       System.out.println("File Received - Success");
	}

	private String documentsInServer() throws IOException{
		File myServerDirectory = new File(SERVER_PATH);
       
       File[] myDocumentList = myServerDirectory.listFiles();
       String output = "Documents present in Server\n";
       
       for(int i=0; i<myDocumentList.length ; i++)
       {
       	if(myDocumentList[i].isFile())
       	{
       		output = output.concat(myDocumentList[i].getName()) + "\n";
       	}
       }
       return output;
	}

	void sendOutputToClient(Object clientOutputSend) {
		try {
			myOutputStream.writeObject(clientOutputSend);
			myOutputStream.flush();
		} catch (IOException e) {
			System.out.println("Error while writing to output stream");
			e.printStackTrace();
		}
	}

    }

}
