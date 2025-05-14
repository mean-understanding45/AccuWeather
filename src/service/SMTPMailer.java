package service;

import config.ConfigLoader;
import model.CityWeatherData;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class SMTPMailer {
    ConfigLoader configLoader;

    public SMTPMailer() throws IOException {
        this.configLoader=ConfigLoader.getInstance();
    }
    public void sendEmail(String googleSheetLink) {
        String smtpServer = configLoader.get("smtp.server");
        int port = configLoader.getInt("smtp.port");
        String username = configLoader.get("smtp.username");
        String password = configLoader.get("smtp.password");
        String from = username;
        String to = configLoader.get("smtp.to");
        String subject = configLoader.get("smtp.subject");
        String body = configLoader.get("smtp.body");
        String boundary = "----=_Part_0_" + System.currentTimeMillis();
        body += "Link to Google Sheets: " + googleSheetLink + "\n\nThanks,\nNavin Bhandari";
        
        try {

            Socket socket = new Socket(smtpServer, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println(reader.readLine()); // Server greeting

            sendCommand(writer, "EHLO localhost");
            readMultiline(reader);

            sendCommand(writer, "STARTTLS");
            System.out.println(reader.readLine());

            // Upgrade to TLS
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, smtpServer, port, true);
            sslSocket.startHandshake();

            reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));

            sendCommand(writer, "EHLO localhost");
            readMultiline(reader);

            sendCommand(writer, "AUTH LOGIN");
            System.out.println(reader.readLine());

            sendCommand(writer, Base64.getEncoder().encodeToString(username.getBytes()));
            System.out.println(reader.readLine());

            sendCommand(writer, Base64.getEncoder().encodeToString(password.getBytes()));
            System.out.println(reader.readLine());

            sendCommand(writer, "MAIL FROM:<" + from + ">");
            System.out.println(reader.readLine());

        // Handle multiple recipients
            String[] recipients = to.split(",");
            for (String recipient : recipients) {
                sendCommand(writer, "RCPT TO:<" + recipient.trim() + ">");
                System.out.println(reader.readLine());
            }


            sendCommand(writer, "DATA");
            System.out.println(reader.readLine());

            // Start writing the MIME email with attachment
            writer.write("From: " + from + "\r\n");
            writer.write("To: " + to + "\r\n");
            writer.write("Subject: " + subject + "\r\n");
            writer.write("MIME-Version: 1.0\r\n");
            writer.write("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n");
            writer.write("\r\n");

            // Body part
            writer.write("--" + boundary + "\r\n");
            writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
            writer.write("Content-Transfer-Encoding: 7bit\r\n");
            writer.write("\r\n");
            writer.write(body + "\r\n");
            writer.write("\r\n");


            // End MIME message
            writer.write("\r\n--" + boundary + "--\r\n");
            writer.write(".\r\n");
            writer.flush();
            System.out.println(reader.readLine());

            sendCommand(writer, "QUIT");
            System.out.println(reader.readLine());

            writer.close();
            reader.close();
            sslSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendCommand(BufferedWriter writer, String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
    }

    private static void readMultiline(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("Server: " + line);
            if (!line.startsWith("250-")) break;
        }
    }
}