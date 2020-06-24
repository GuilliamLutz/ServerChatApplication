import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private final int serverPort;
    private ArrayList<ClientHandler> handlerList = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ClientHandler> getHandlerList(){
        return handlerList;
    }

    @Override
    public void run() {
        try {
            //creates serversocket on a port
            ServerSocket serverSocket = new ServerSocket(serverPort);
            //loop that accepts incoming connections this also makes it possible to accommodate multiple clients
            while (true) {
                System.out.println("bout to accept");
                //represents socket to the client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted from" + clientSocket);
                //creates a new clientHandler that handles the communication with the clientSocket
                ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                handlerList.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
