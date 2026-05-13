package client;

import java.io.PrintWriter;

public class ClientHandler {
    private final PrintWriter output;
    private String lastMessage = "";

    public ClientHandler(PrintWriter output) {
        this.output = output;
    }

    public void sendMessage(String message) {
        lastMessage = message;
        output.println(message);
        output.flush();
    }

    public void sendLastMessage(){
        output.println(lastMessage);
        output.flush();
    }
}