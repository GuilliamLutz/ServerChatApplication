import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class serverInterface {
    private final int serverPort;
    private final String serverName;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    public serverInterface(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        serverInterface client = new serverInterface("localhost", 8818);
        if (!client.connect()){
            System.err.println("Connection failed");
        }else{
            System.out.println("Connection succesfull");
            if (client.login("guest", "lul")){
                System.out.println("Connected succesfully");
            }else{
                System.err.println("Connection failed");
            }
        }

        Scanner s = new Scanner(System.in);
        s.next();
    }

    private boolean login(String name, String password) throws IOException {
        String command = ("login " + name + " " + password + '\n');
        this.serverOut.write(command.getBytes());
        System.out.println("Login error for: " + name);
        String serverResponse = this.bufferedIn.readLine();
        System.out.println("Severresponse: " + serverResponse);

        if (serverResponse.equals("Logged in!")){
            return true;
        }else {
            return false;
        }
    }

    private boolean connect() {
        try {
            this.socket = new Socket(this.serverName, this.serverPort);
            this.serverOut = this.socket.getOutputStream();
            this.serverIn = this.socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(this.serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
