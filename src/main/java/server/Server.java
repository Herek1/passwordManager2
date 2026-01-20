package server;

import server.Requests.RequestHandler;

import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 12345;
    private static final Logger logger = new Logger();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            int clientPort = clientSocket.getPort();
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                RequestHandler requestHandler = new RequestHandler();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received \"" + inputLine + "\" from " + clientAddress + ":" + clientPort);
                    String response = requestHandler.handle(inputLine);
                    //logger.saveLogs((clientPort+";"+inputLine), response);
                    out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}