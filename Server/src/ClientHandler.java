


import java.io.*;

import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * this class functions as a thread for each new client it handles the communication between the server and the client
 */
public class ClientHandler extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private OutputStream outputStream;
    private String user = null;
    private HashSet<String> groupSet = new HashSet<>();

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
            String[] input = line.split(" ", 3);
            if (input != null && input.length > 0) {
                String command = input[0];
//                System.out.println(command);
//                System.out.println(input[0]);
                if (command.equals("quit") || command.equals("logoff")){
                    logoffHandler();
                    break;
                } else if (command.equals("login")) {
                    loginHandler(this.outputStream, input);
                }else if(command.equals("msg")){
                    messageHandler(input);
                }
                else if (command.equals("join")){
//                    String unknown = ("Unknown command: " + command + "\n");
//                    this.outputStream.write(unknown.getBytes());
                    joinHandler(input);
                }else if (command.equals("leave")){
                    leaveHandler(input);

                }
            }
            String msg = ("You typed: " + line + "\n");

            System.out.println(msg);
            //outputStream.write(msg.getBytes());
        }

        clientSocket.close();
    }

    private void leaveHandler(String[] input) throws IOException {
        if(input.length >=1) {
            String group = input[1];
            if (input.length > 1) {
                if (this.groupSet.contains(group)) {
                    this.groupSet.remove(group);
                    send("You left: " + group + "\n");
                }
            }
        }else{
            send("Type which group you want to leave\n");
        }


    }
    public boolean isMemberOfGroup(String group){
        return this.groupSet.contains(group);

    }

    private void joinHandler(String[] input) throws IOException {
        if (input.length>1){
            String group = input[1];
            this.groupSet.add(group);
            this.outputStream.write(("You joined " + group + "\n").getBytes());
        }
    }

    private void groupMessageHandler(String[] input) throws IOException {
        List<ClientHandler> handlerList = this.server.getHandlerList();
        String message = input.toString();
        for (ClientHandler handler : handlerList) {
        }

    }

    //format private messaging: 0="msg command" 1="user" 2="msg..."
    //format group messaging: 0="msg command" 1="#groupchat" 2="msg..."
    private void messageHandler(String[] input) throws IOException {
        String reciever = input[1];
        String message = input[2];

        boolean isGroup = reciever.charAt(0)=='#';

        List<ClientHandler> handlerList = this.server.getHandlerList();
        for (ClientHandler handler : handlerList){
            if (isGroup){
                if (handler.isMemberOfGroup(reciever)){
                    String outgoingMessage = (this.user + " in " +  reciever +  ":\n" + message + "\n");
                    handler.send(outgoingMessage);
                }
            }else {
                if (reciever.equals(handler.getUser())) {
                    String outgoingMessage = (this.user + ": " + message + "\n");
                    System.out.println("ÖUTGOING:" + outgoingMessage);
                    handler.send(outgoingMessage);
                }
            }
        }
    }

    private void logoffHandler() throws IOException {
        List<ClientHandler> clientHandlers = this.server.getHandlerList();
        String logoffNotification = ("Offline now: " + this.user + "\n");
        for(ClientHandler handler : clientHandlers){
            handler.send(logoffNotification);
        }
        this.server.removeClient(this);
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
//                System.out.println("User logged in succesfully: " + name + "\n");

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
                String loginNotification = ("Online now: " + this.user + "\n");
                for(ClientHandler handler : clientHandlers){
                    if (!name.equals(handler.getUser())) {
                        handler.send(loginNotification);
                    }
                }
            }else if (name.equals("Guilliam") && password.equals("Guilliam")) {
                String msg = "Logged in!\n";
                this.outputStream.write(msg.getBytes());
                this.user = name;
//                System.out.println("User logged in succesfully: " + name + "\n");

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
                String loginNotification = ("Online now: " + this.user + "\n");
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
