


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

        List<ClientHandler> clientHandlers = server.getHandlerList();

        while ((line = reader.readLine()) != null) {
            //makes an array with split up words to detect commands for logging in etc.
            String[] input = line.split(" ");
            if (input != null && input.length > 0) {
                String command = input[0];
                System.out.println(command);
                System.out.println(input[0]);
                if ("quit".equalsIgnoreCase(line)) {
                    break;
                } else if (command.equals("login")) {
                    loginHandler(this.outputStream, input);
                }else{
                    String unknown = "Unknown command: " + command;
                    this.outputStream.write(unknown.getBytes());
                }
            }
            String msg = "You typed: " + line + "\n";
//            //sends the messages from the clienthandler to all the other clienthandlers connected to the server
//            for(ClientHandler handler : clientHandlers){
//                handler.send(msg);
//            }
            System.out.println(msg);
            outputStream.write(msg.getBytes());
        }

        clientSocket.close();
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
                System.out.println("User logged in succesfully: " + name);
            }else if (name.equals("Guilliam") && password.equals("Guilliam")) {
                String msg = "Logged in!\n";
                this.outputStream.write(msg.getBytes());
                this.user = name;
                System.out.println("User logged in succesfully: " + name);
            }
            else{
                String msg = "Wrong login data\n";
                this.outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException {
        outputStream.write(msg.getBytes());
    }

}
