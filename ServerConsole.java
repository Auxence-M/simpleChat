import java.io.*;
import java.util.Scanner;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole
 *
 * @author Auxence MEDJA
 * @version October 2020
 */

public class ServerConsole implements ChatIF {

    //Class variables *************************************************

    /**
     * The default port to connect on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Instance variables **********************************************

    /**
     * ChatIF instance that will be used to display messages written in the console.
     */
    ChatIF serverUI;

    /**
     * EchoServer instance that created this Server console
     */
    EchoServer server;



    /**
     * Scanner to read from the console
     */
    Scanner fromConsole;

    public ServerConsole(int port){
        try{
            server = new EchoServer(port);

            server.listen(); //Start listening for connections


        }catch (Exception exception){

            System.out.println("Error: Unable to start listening for some unknown reasons!. Terminating server");
            System.exit(1); // Something we knew could go wrong happen so we are shutting down the whole system
        }

        // Create scanner object to read from console
        fromConsole = new Scanner(System.in);


    }

    //Instance methods ************************************************

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the client's message handler.
     */
    public void accept()
    {
        try
        {

            String message;

            while (true)
            {
                message = fromConsole.nextLine();
                handleMessageFromConsole(message);

            }
        }
        catch (Exception ex)
        {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    /**
     * This method handles messages from the Server UI
     * If command execute them
     * If simple message display them
     *
     * @param message The string received.
     */

    private void handleMessageFromConsole(String message) throws IOException {
        try {

            if (message.charAt(0) == '#'){
                if (message.startsWith("#setport")){
                    int port= 0;
                    port=Integer.parseInt(message.substring(10,message.length()-1)) ;
                    if (!server.isListening()){
                        server.setPort(port);
                        System.out.println("port set to: "+port);
                        System.out.println("setport<"+port+">: successful");
                    }else {
                        System.out.println("Cannot set port if listening for new connections\n"+
                                "setport<"+port+">: unsuccessful");
                    }
                }else if (message.equals("#quit")){
                    System.out.println("quit: successful");
                    System.exit(0);
                }else if (message.equals("#stop")){
                    server.stopListening();
                    server.sendToAllClients("WARNING - The server has stopped listening for connections");
                    System.out.println("stop: successful");
                }else if (message.equals("#close")){
                    server.stopListening();
                    server.close();
                    System.out.println("close: successful");
                }else if (message.equals("#start")){
                    if (!server.isListening()){
                        server.listen();
                        System.out.println("start: successful");
                    }else {
                        System.out.println("Cannot start listening for new connections if already listening");
                        System.out.println("start: unsuccessful");
                    }
                }else if (message.equals("#getport")){
                    System.out.println("Port number: "+server.getPort());
                }else {
                    System.out.println("Invalid command");
                }
            }else {
                display(message);
                server.sendToAllClients("SERVER MESSAGE> " + message);
            }

        }catch (Exception exception){
            System.out.println(exception.getMessage());
            System.out.println("Unable to send massage or executes command.");
        }
    }

    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message) {

        System.out.println("SERVER MESSAGE> " + message);
    }

    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Server UI.
     * @param args[0] The port number to listen on.  Defaults to 5555
     *          if no argument is entered.
     */
    public static void main(String[] args) {

        int port = 0; //Port to listen on

        try
        {
            port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch(Throwable t)
        {
            port = DEFAULT_PORT; //Set port to 5555
        }
        ServerConsole sc = new ServerConsole(port);
        sc.accept();
    }






}
