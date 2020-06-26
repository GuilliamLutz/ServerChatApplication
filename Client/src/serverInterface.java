import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class serverInterface {
    private final int serverPort;
    private final String serverName;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;

    public serverInterface(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        serverInterface serverInterface = new serverInterface("localhost", 8818);
        if (!serverInterface.connect()){
            System.err.println("Connection failed");
        }else{
            System.out.println("Connection succesfull");
        }

        Scanner s = new Scanner(System.in);
        s.next();
    }

    private boolean connect() {
        try {
            this.socket = new Socket(this.serverName, this.serverPort);
            this.serverOut = this.socket.getOutputStream();
            this.serverIn = this.socket.getInputStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
