import java.io.*;
import java.net.Socket;

/**
this class functions as a thread for each new client
 */
public class ClientHandler extends Thread {

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
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
        }
    }

    private void clientSocketHandler() throws IOException, InterruptedException {
        //recieves data from the client and/ sends it to the server
        InputStream inputStream = clientSocket.getInputStream();
        //recieves data from the server and sends it to the client
        OutputStream outputStream = clientSocket.getOutputStream();

        //reads what comes in trough the inputstream
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null){
            if ("quit".equalsIgnoreCase(line)){
                break;
            }
            String msg = "You typed: " + line + "\n";
            System.out.println(msg);
            outputStream.write(msg.getBytes());
        }

        clientSocket.close();
    }

}
