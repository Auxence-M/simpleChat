// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;

  private String loginID;


  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
    sendToServer("#login <"+loginID+">");
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message) {
    try {
      if (message.charAt(0) == '#'){

        if (message.startsWith("#sethost")){
          String host="";
          host = message.substring(10,message.length()-1);
          if (!isConnected()){
            setHost(host);
            System.out.println("host set to: "+host);
            System.out.println("sethost<"+host+">: successful");
          }else {
            System.out.println("Cannot set host if connected\n"+"sethost<"+host+">: unsuccessful");
          }
        } else if (message.startsWith("#setport")){
          int port= 0;
          port=Integer.parseInt(message.substring(10,message.length()-1)) ;
          if (!isConnected()){
            setPort(port);
            System.out.println("port set to: "+port);
            System.out.println("setport<"+port+">: successful");
          }else {
            System.out.println("Cannot set port if connected\n"+"setport<"+port+">: unsuccessful");
          }

        } else if (message.equals("#quit")){
          if (isConnected()){
            closeConnection();
          }
          System.out.println("Terminating...");
          System.out.println("quit: successful");
          System.exit(0);

        }  else if (message.equals("#logoff")){
          closeConnection();
          System.out.println("logoff: successful");

        } else if (message.equals("#login")){
          if (!isConnected()){
            openConnection();
            sendToServer("#login <"+loginID+">");
            System.out.println("login: successful");
          } else {
            System.out.println("Cannot login if you are already connected to the server");
            System.out.println("Login: unsuccessful");
          }

        }else if (message.equals("#gethost")){
          System.out.println("Host name: "+getHost());

        }else if (message.equals("#getport")){
          System.out.println("Port number: "+getPort());

        }else {
          System.out.println("Invalid command");
        }

      }else {

        sendToServer(message);
      }

    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  @Override
  protected void connectionClosed() {
    System.out.println("Disconnecting..");
    System.out.println("Connexion closed");



  }

  @Override
  protected void connectionException(Exception exception) {
    System.out.println("SERVER SHUTTING DOWN! DISCONNECTING!");
    System.out.println("Abnormal termination of connection.");
  }
}
//End of ChatClient class
