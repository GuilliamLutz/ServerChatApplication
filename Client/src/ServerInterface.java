import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerInterface {
    private final int serverPort;
    private final String serverName;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<UserStaturListener> userStaturListeners = new ArrayList<>();

    public ServerInterface(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ServerInterface client = new ServerInterface("localhost", 8818);
        client.addUserStatusListener(new UserStaturListener() {
            @Override
            public void online(String name) {
                System.out.println("ONLINE: " + name);
            }

            @Override
            public void offline(String name) {
                System.out.println("OFFLINE: " + name);
            }
        });
        if (!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection succesfull");
            if (client.login("guest", "guest")) {
                System.out.println("Connected succesfully");
            } else {
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

        if (serverResponse.equals("Logged in!")) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    private void startMessageReader() {
        Thread t = new Thread(){
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = this.bufferedIn.readLine()) != null) {
                String[] input = line.split(" ", 3);
                if (input != null && input.length > 0) {
                    String command = input[0];
                    if (command.equals("Online")){
                        onlineHandler(input);
                    }else if(command.equals("Offline")){
                        offlineHandler(input);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            try{
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void offlineHandler(String[] input) {
        String name = input[2];
        for (UserStaturListener listener : this.userStaturListeners){
            listener.offline(name);
        }
    }

    private void onlineHandler(String[] input) {
        String name = input[2];
        for (UserStaturListener listener : this.userStaturListeners){
            listener.online(name);
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

    public void addUserStatusListener(UserStaturListener listener) {
        this.userStaturListeners.add(listener);
    }

    public void removeUserStatusListener(UserStaturListener listener) {
        this.userStaturListeners.remove(listener);
    }
}