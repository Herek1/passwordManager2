package db;



import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

public class EngineStartup {
    private final Tables table = new Tables();
    private final Map<String, String> tablesToCreate = new LinkedHashMap<>();

    public void run(Connection engineConnection){

        tablesToCreate.put("users", """
            CREATE TABLE users (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        master_password VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL
                    );
        """);
        tablesToCreate.put("passwords", """
            CREATE TABLE passwords (
                id SERIAL PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                login VARCHAR(255) NOT NULL,
                password_encrypted TEXT NOT NULL,
                domain VARCHAR(255),
                created_at TIMESTAMP DEFAULT NOW(),
                updated_at TIMESTAMP DEFAULT NOW(),
                CONSTRAINT unique_user_domain UNIQUE(username, domain, login)
            );
        """);

        tablesToCreate.put("audit_logs", """
            CREATE TABLE audit_logs (
                id SERIAL PRIMARY KEY,
                timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                user_id BIGINT NULL,
                ip_address INET NULL,
                action VARCHAR(50) NOT NULL,
                success BOOLEAN NOT NULL DEFAULT TRUE,
                request_data TEXT NULL,
                response_data TEXT NULL
        );
        """);

        for (Map.Entry<String, String> entry : tablesToCreate.entrySet()) {
            String tableName = entry.getKey();
            String createQuery = entry.getValue();

            if (!table.doesTableExist(engineConnection, tableName)) {
                table.createTable(createQuery, engineConnection);
            }
        }
    }

    public Map<String, String> returnMapOfTables(){
        return tablesToCreate;
    }
}