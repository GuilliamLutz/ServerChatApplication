import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private final int serverPort;
    private final String serverName;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<String> onlineList = new ArrayList<>();

    private String username;

    private ArrayList<UserStaturListener> userStaturListeners = new ArrayList<>();


    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public Client(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 8818);
        Scanner s = new Scanner(System.in);
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

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String sender, String message) {
                //System.out.println("You got an message from " + sender + ": " + message);
            }
        });

        if (!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection succesfull");
            String inputLine = s.nextLine();
            String[] input = inputLine.split(" ", 3);

            if (client.login(input[1], input[2])) {
                System.out.println("Connected succesfully");

//                client.msg("Guilliam", "Hello World!");
            } else {
                System.err.println("Connection failed");
            }
        }


        while (true) {
            String inputLine = s.nextLine();
            String[] input = inputLine.split(" ");
            if (input.length > 2) {
                String[] inputMessage = inputLine.split(" ", 3);
                client.msg(inputMessage[1], inputMessage[2]);
            } else if (input.length == 2) {
                String[] groupCommands = inputLine.split(" ", 2);
                client.groupCommands(groupCommands[0], groupCommands[1]);
            } else if (input.length == 1) {
                String quitCommand = inputLine;
                client.quit(quitCommand);
            }
        }
    }

    private void quit(String quitCommand) throws IOException {
        this.serverOut.write(quitCommand.getBytes());
    }

    public void msg(String reciever, String message) throws IOException {
        String command = ("msg " + reciever + " " + message + '\n');
        this.serverOut.write(command.getBytes());

    }

    public void groupCommands(String command, String group) throws IOException {
        String action = (command + " " + group + "\n");
        this.serverOut.write(action.getBytes());
    }

    private boolean login(String name, String password) throws IOException {
        String command = ("login " + name + " " + password + '\n');
        this.serverOut.write(command.getBytes());
//        System.out.println("Login error for: " + name);
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
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    readMessageLoop();
                }
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line = this.bufferedIn.readLine();
            System.out.println(line);
//            while ((line) != null) {
            String[] input = line.split(" ", 3);
            if (input != null && input.length > 0) {
                String command = input[0];
                if (command.equals("Online")) {
                    onlineHandler(input);
                } else if (command.equals("Offline")) {
                    offlineHandler(input);
                } else if (!command.equals(username)) {
                    messageHandler(input);
                }
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void messageHandler(String[] input) {
        String reciever = input[0];
        if (input.length > 2) {
            String message = input[1] + " " + input[2];

            for (MessageListener listener : this.messageListeners) {
                listener.onMessage(reciever, message);
            }
        }
    }

    private void offlineHandler(String[] input) {
        String name = input[2];
        for (UserStaturListener listener : this.userStaturListeners) {
            listener.offline(name);
            this.onlineList.remove(name);
        }
    }

    private void onlineHandler(String[] input) {
        String name = input[2];
        this.username = name;
        for (UserStaturListener listener : this.userStaturListeners) {
            listener.online(name);
            this.onlineList.add(name);
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

    public void addMessageListener(MessageListener listener) {
        this.messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        this.messageListeners.remove(listener);
    }

    public ArrayList<UserStaturListener> getUserStaturListeners() {
        return userStaturListeners;
    }

    public String getUsername() {
        return username;
    }
}
