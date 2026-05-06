package server;

import db.Engine;
import db.dao.AuditLogDAO;
import server.Requests.RequestHandler;
import server.Util.ErrorResponseUtil;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Engine engine;

    private Integer userId = null;
    private final String clientIp;

    private boolean lastLogResult;
    private int failCounter = 0;

    public ClientHandler(Socket socket, Engine engine) {
        this.clientSocket = socket;
        this.engine = engine;
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
            AuditLogDAO auditLogDAO = new AuditLogDAO(engine.returnConnection());
            Logger logger = new Logger(auditLogDAO, this);
            RequestHandler requestHandler = new RequestHandler(engine.returnConnection(), logger);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {

                String response = requestHandler.handle(inputLine, this);
                logger.saveLogs(this.userId, this.clientIp, inputLine, response);
                if(failCounter%5 == 0 && failCounter != 0){
                    response = ErrorResponseUtil.createTimeOutResponse(this);
                }
                out.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getClientIp() { return clientIp; }

    public void setFailCounter(int value){this.failCounter = value;}
    public int getFailCounter(){return failCounter;}

    public void setLastLogResult(boolean value){this.lastLogResult = value;}
    public boolean getLastLogResult(){return this.lastLogResult;}
}