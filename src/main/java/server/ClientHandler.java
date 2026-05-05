package server;

import db.Engine;
import server.Requests.RequestHandler;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Logger logger;
    private final Engine engine;

    private Integer userId = null;
    private final String clientIp;

    public ClientHandler(Socket socket, Engine engine, Logger logger) {
        this.clientSocket = socket;
        this.engine = engine;
        this.logger = logger;
        this.clientIp = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {

            RequestHandler requestHandler = new RequestHandler(engine.returnConnection(), logger);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {

                String response = requestHandler.handle(inputLine, this);
                logger.saveLogs(this.userId, this.clientIp, inputLine, response);
                out.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getClientIp() { return clientIp; }
}