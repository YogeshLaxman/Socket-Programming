import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client2 {
	Socket mySocket;           
	ObjectOutputStream myOutputStream;         
 	ObjectInputStream myInputStream;          
	String inputFromClient;                
	String OutputFromServer;    
	char[] clientPassword;
	
	final String setUserName = "user2";
	final String setUserPassword = "password2";
	final String CLIENT_DIRECTORY = "C:\\Users\\13528\\Documents\\SocketProgramming\\Client2" ;
	private String[] clientState = { "Client started...\nEnter ftpclient <hostname> <port>",
			  "Connected to server",
			  "Validate username",
			  "Validate password",
			  "Login successful",
			  "Enter command"
			  };
	static int currentClientState = -1;
	public static void main(String args[])
	{
		Client2 myClient = new Client2();
		myClient.run(); 
	}
	

	public Client2() {}
	void run()
	{	
		currentClientState = 0;
		try 
		{
			
			
				//System.out.println("CurrentState: "+ currentClientState);
				System.out.println("Client started...\r\n" + 
									"Enter ftpclient <hostname> <port>");
				BufferedReader myBufferedReader = new BufferedReader(new InputStreamReader(System.in));
				Console myConsole = System.console();
		        while(true) {
		        	
		        	try 
		        	{	
		        		if(currentClientState != 2)
		        		{	
		        			inputFromClient = myBufferedReader.readLine();
		        			processInputFromClient(inputFromClient, currentClientState);
		        		}
		        		else
		        		{
		        			 	clientPassword = myConsole.readPassword();
				                String clientInput = new String(clientPassword);
								processInputFromClient(clientInput, currentClientState);
		        		}
						
		        	}
		            catch(Exception e){
		                System.err.println("Catch exception  " + e);
		            }

		        }
		}
	        
		catch (Exception ex)
        {
            System.err.println("exception occured" + ex);
        }
        finally
        {
            //Close connections
            try
            {
                myInputStream.close();
                myOutputStream.close();
                mySocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
		
	}
	
	private void processInputFromClient(String input, int currentState) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		switch(currentState) 
		{
		case 0:
			processInitialState(input);
			break;
		case 1:
			processUserName(input);
			break;
		case 2:
			processUserPassword(input);
			break;
		case 3:
			processCommand(input);
			break;
		default:
			System.out.println("Invalid state of client");
			break;
		}
	}

	

	private void processUserPassword(String input) {
		// TODO Auto-generated method stub
		if(input.contentEquals(setUserPassword)) 
		{
			System.out.println("\n Client login successful. Please enter request:"
					+ "\ndir"
					+ "\nget <filename>"
					+ "\nupload <filename>");
			currentClientState=3;
		}
		else
		{
			System.out.println("\nInvalid password.\nEnter password:");
		}
	}

	private void processUserName(String input) {
		// TODO Auto-generated method stub
		if(input.contentEquals(setUserName)) 
		{
			System.out.println("\nEnter password:");
			currentClientState=2;
		}
		else
		{
			System.out.println("\nInvalid username.\nEnter username:");
		}
		
	}


	private void processInitialState(String command) {
		// TODO Auto-generated method stub
		
			String[] commandPart = command.split(" ");
			//System.out.println("\ncommandPart[0]:"+ commandPart[0]);
			if(commandPart[0].equals("ftpclient"))
			{
				try 
				{
					mySocket = new Socket(commandPart[1], Integer.parseInt(commandPart[2]));

					myOutputStream = new ObjectOutputStream(mySocket.getOutputStream());
					myOutputStream.flush();
					myInputStream = new ObjectInputStream(mySocket.getInputStream());
					System.out.println("Connected to server as " + commandPart[1] + " at port " + commandPart[2]);
					currentClientState=1;
					System.out.println("\nEnter client Username:");
				}
				catch (IOException ex) 
				{
					System.out.println("Enter valid host and port");
				}
			}
			else
			{
				System.out.println("Connection unsuccessful.\n Make sure that Server is online and \nEnter ftpclient <hostname> <port>");
			}
            
		
	}

	private void processCommand(String input) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		String[] commandPart = input.split(" ");
		switch(commandPart[0]) {
		case "dir":
			writeObjectToOutputStream(input);
			System.out.println("\nFiles present at Server:" + "\n" + (String) myInputStream.readObject());
			break;
		case "get":
			writeObjectToOutputStream(input);
			makeNewFileAndWriteData(commandPart[1]);
			break;
		case "upload":
			writeObjectToOutputStream(input);
			writeFileToOutputStram(commandPart[1]);
			break;
		case "ftpclient":
			 System.out.println("Already connected to the Server\nPlease enter valid commands\n dir\n get <filename>\n upload <filename>");
			 break;
		default:
			System.out.println("Operation not supported");
			break;
		}
	}
	
	private void makeNewFileAndWriteData(String document) throws IOException, ClassNotFoundException {
		if("Absent".equalsIgnoreCase((String) myInputStream.readObject())) {
	       	 System.out.println("File is not present :(");
	       	 return;
	       }
			String fileName = CLIENT_DIRECTORY + "\\" + document;
	        BufferedOutputStream bufferedOS = new BufferedOutputStream(new FileOutputStream(fileName));
	        
			bufferedOS.write((byte[]) myInputStream.readObject());
			bufferedOS.close();
			
	        System.out.println("File Received - Success");
		}

	private void writeFileToOutputStram(String document) {
		try {
			File myDocument = new File(CLIENT_DIRECTORY + "\\" + document);
			if(!myDocument.exists()) {
				writeObjectToOutputStream("Absent");
				System.out.println("File is not present");
				return;
			}
			writeObjectToOutputStream("Present");
			InputStream is = new FileInputStream(myDocument); 	
	        writeObjectToOutputStream(is.readAllBytes());
	        is.close();
			System.out.println("File Sent");
		} catch(Exception e) {
			writeObjectToOutputStream("Error while getting the file");
		}
     }
	
	
	void writeObjectToOutputStream(Object clientOutputSend) {
		try {
			myOutputStream.writeObject(clientOutputSend);
			myOutputStream.flush();
		} catch (IOException e) {
			System.out.println("Error while writing to output stream");
			e.printStackTrace();
		}
	}
}
