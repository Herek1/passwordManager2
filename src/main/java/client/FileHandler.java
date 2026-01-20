package client;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;


public class FileHandler {
    private final Path csvPath;
    private final String masterPassowrd;
    private final String userName;

    public FileHandler(String fileName, String masterPassword, String userName){
        this.csvPath = Path.of(fileName);
        this.masterPassowrd = masterPassword;
        this.userName = userName;
        initCsv();
    };


    private void initCsv() {
        try {
            if (!Files.exists(csvPath)) {
                Files.createFile(csvPath);
                try (CSVPrinter printer = new CSVPrinter(
                        Files.newBufferedWriter(csvPath),
                        CSVFormat.DEFAULT.withHeader("url", "login", "password", "salt","user")
                )) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void checkAndSaveEntry(String url, String login, String password){
        deleteEntry(url,login);
        saveEntry(url,login,password);
    }
    private void saveEntry(String url, String login, String password) {
        try {
            byte[] entrySalt = new byte[16];
            new SecureRandom().nextBytes(entrySalt);

            String encrypted = Encryption.encryptPassword(masterPassowrd, password, entrySalt);
            String saltStr = Base64.getEncoder().encodeToString(entrySalt);

            try (CSVPrinter printer = new CSVPrinter(
                    Files.newBufferedWriter(csvPath, StandardOpenOption.APPEND),
                    CSVFormat.DEFAULT
            )) {
                printer.printRecord(url, login, encrypted, saltStr, userName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String[]> checkEntry(String url, String userName, String masterPassword) {
        ArrayList<String[]> result = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(csvPath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("url", "login", "password", "salt", "user")
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {
                String recordUrl = record.get("url");
                String recordUser = record.get("user");

                if (recordUrl.toLowerCase().contains(url.toLowerCase()) && recordUser.equals(userName)) {
                    String login = record.get("login");
                    String encryptedPassword = record.get("password");
                    String saltStr = record.get("salt");

                    byte[] saltBytes = Base64.getDecoder().decode(saltStr);
                    String decryptedPassword = Encryption.decryptPassword(masterPassword, encryptedPassword, saltBytes);
                    if (decryptedPassword == null) {
                        throw new IllegalArgumentException("WRONG_MASTER_PASSWORD");
                    }
                    result.add(new String[]{recordUrl,login, decryptedPassword});
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file", e);
        }

        return result;
    }
    public void deleteEntry(String url, String login) {
        try (Reader reader = Files.newBufferedReader(csvPath)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("url", "login", "password", "salt", "user")
                    .withFirstRecordAsHeader()
                    .parse(reader);

            Path tmp = Files.createTempFile("pm_tmp", ".csv");

            try (CSVPrinter printer = new CSVPrinter(
                    Files.newBufferedWriter(tmp),
                    CSVFormat.DEFAULT.withHeader("url", "login", "password", "salt", "user")
            )) {
                for (CSVRecord record : records) {
                    boolean isDuplicate = record.get("url").equals(url) && record.get("login").equals(login) && record.get("user").equals(userName);

                    if (!isDuplicate) {
                        printer.printRecord(
                                record.get("url"),
                                record.get("login"),
                                record.get("password"),
                                record.get("salt"),
                                record.get("user")
                        );
                    }
                }
            }

            Files.move(tmp, csvPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
