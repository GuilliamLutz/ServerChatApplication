import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    public static void main(String[] args) {
        int port = 8818;
        try {
            //creates serversocket on a port
            ServerSocket serverSocket = new ServerSocket(port);
            //loop that accepts incoming connections
            while (true) {
                System.out.println("bout to accept");
                //represents socket to the client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted from" + clientSocket);
                //creates a new clientHandler that handles the communication with the clientSocket
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
                            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
