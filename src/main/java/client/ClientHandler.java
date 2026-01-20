package client;

import java.io.PrintWriter;

public class ClientHandler {
    private final PrintWriter output;

    public ClientHandler(PrintWriter output) {
        this.output = output;
    }

    public void sendMessage(String message) {
        output.println(message);
        output.flush();
    }
}