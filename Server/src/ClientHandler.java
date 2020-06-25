


import java.io.*;

import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

/**
 * this class functions as a thread for each new client it handles the communication between the server and the client
 */
public class ClientHandler extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private OutputStream outputStream;
    private String user = null;

    public ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            clientSocketHandler();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    private void clientSocketHandler() throws IOException, InterruptedException {
        //recieves data from the client and/ sends it to the server
        InputStream inputStream = clientSocket.getInputStream();
        //recieves data from the server and sends it to the client
        this.outputStream = clientSocket.getOutputStream();

        //reads what comes in trough the inputstream
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;



        while ((line = reader.readLine()) != null) {
            //makes an array with split up words to detect commands for logging in etc.
            String[] input = line.split(" ");
            if (input != null && input.length > 0) {
                String command = input[0];
                System.out.println(command);
                System.out.println(input[0]);
                if (command.equals("quit") || command.equals("logoff")){
                    logoffHandler();
                    break;
                } else if (command.equals("login")) {
                    loginHandler(this.outputStream, input);
                }else{
                    String unknown = ("Unknown command: " + command + "\n");
                    this.outputStream.write(unknown.getBytes());
                }
            }
            String msg = ("You typed: " + line + "\n");

            System.out.println(msg);
            outputStream.write(msg.getBytes());
        }

        clientSocket.close();
    }

    private void logoffHandler() throws IOException {
        List<ClientHandler> clientHandlers = server.getHandlerList();
        String logoffNotification = ("User " + this.user + " went offline\n");
        for(ClientHandler handler : clientHandlers){
            handler.send(logoffNotification);
        }
        this.clientSocket.close();
    }

    public String getUser(){
        return this.user;
    }

    private void loginHandler(OutputStream outputStream, String[] input) throws IOException {
        //this method will check the input when the command login has been selected
        // if will check if the name thats filled in is a name of an existing user and if the password is correct
        //when this is correct the user will be logged in to the chatsever
        if(input.length == 3) {
            String name = input[1];
            String password = input[2];


            if (name.equals("guest") && password.equals("guest")){
                String msg = "Logged in!\n";
                this.outputStream.write(msg.getBytes());
                this.user = name;
                System.out.println("User logged in succesfully: " + name + "\n");

                List<ClientHandler> clientHandlers = server.getHandlerList();

                for (ClientHandler handler : clientHandlers){
                        if (handler.getUser() != null) {
                            if (!name.equals(handler.getUser())) {
                                String userStatus = ("Online: " + handler.getUser() + "\n");
                                send(userStatus);
                            }
                        }
                }

                //            //sends the messages from the clienthandler to all the other clienthandlers that the user is connected
                String loginNotification = ("User " + this.user + " is online \n");
                for(ClientHandler handler : clientHandlers){
                    if (!name.equals(handler.getUser())) {
                        handler.send(loginNotification);
                    }
                }
            }else if (name.equals("Guilliam") && password.equals("Guilliam")) {
                String msg = "Logged in!\n";
                this.outputStream.write(msg.getBytes());
                this.user = name;
                System.out.println("User logged in succesfully: " + name + "\n");

                List<ClientHandler> clientHandlers = server.getHandlerList();

                for (ClientHandler handler : clientHandlers){
                    if (handler.getUser() != null) {
                        if (!name.equals(handler.getUser())) {
                            String userStatus = ("Online: " + handler.getUser() + "\n");
                            send(userStatus);
                        }
                    }
                }

                //            //sends the messages from the clienthandler to all the other clienthandlers that the user is connected
                String loginNotification = ("User " + this.user + " is online \n");
                for(ClientHandler handler : clientHandlers){
                    if (!name.equals(handler.getUser())) {
                        handler.send(loginNotification);
                    }
                }
            }
            else{
                String msg = "Wrong login data\n";
                this.outputStream.write(msg.getBytes());
            }


        }
    }

    private void send(String msg) throws IOException {
        if (this.user != null) {
            outputStream.write(msg.getBytes());
        }
    }

}
