package server;

import db.Engine;
import db.dao.AuditLogDAO;
import server.Requests.RequestHandler;

import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {

        Engine engine = new Engine();
        engine.start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, engine)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}