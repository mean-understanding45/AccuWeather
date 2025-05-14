package service;

import config.ConfigLoader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MinimalGoogleDriveUploader {
    public String uploadCSVAsGoogleSheet(String csvFilePath, String sheetName) throws IOException {
        String accessToken = ConfigLoader.getInstance().get("google.access.token");
        String uploadEndpoint = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart";
        String permissionEndpoint = "https://www.googleapis.com/drive/v3/files/%s/permissions";

        byte[] csvData = Files.readAllBytes(new File(csvFilePath).toPath());

        String boundary = "===123456===";
        String metadata = String.format(
                "{ \"name\": \"%s\", \"mimeType\": \"application/vnd.google-apps.spreadsheet\" }",
                sheetName
        );

        // Construct multipart body
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteStream, StandardCharsets.UTF_8), true);

        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Type: application/json; charset=UTF-8\r\n\r\n");
        writer.append(metadata).append("\r\n");

        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Type: text/csv\r\n\r\n");
        writer.flush();
        byteStream.write(csvData);
        writer.append("\r\n--").append(boundary).append("--\r\n");
        writer.close();

        // Upload the file
        HttpURLConnection conn = (HttpURLConnection) new URL(uploadEndpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "multipart/related; boundary=" + boundary);
        conn.setDoOutput(true);
        conn.getOutputStream().write(byteStream.toByteArray());

        // Parse response to extract file ID
        String jsonResponse = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String fileId = extractFileId(jsonResponse);
        if (fileId == null) {
            throw new IOException("Failed to extract file ID from response.");
        }

        // Make the file public to anyone
        makePublic(fileId, accessToken, String.format(permissionEndpoint, fileId));

        return "https://docs.google.com/spreadsheets/d/" + fileId;
    }

    private void makePublic(String fileId, String accessToken, String endpoint) throws IOException {
        HttpURLConnection permissionConn = (HttpURLConnection) new URL(endpoint).openConnection();
        permissionConn.setRequestMethod("POST");
        permissionConn.setRequestProperty("Authorization", "Bearer " + accessToken);
        permissionConn.setRequestProperty("Content-Type", "application/json");
        permissionConn.setDoOutput(true);

        String permissionJson = "{ \"role\": \"reader\", \"type\": \"anyone\" }";
        try (OutputStream os = permissionConn.getOutputStream()) {
            os.write(permissionJson.getBytes(StandardCharsets.UTF_8));
        }

        if (permissionConn.getResponseCode() != 200) {
            String error = new String(permissionConn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new IOException("Failed to set public permission: " + error);
        }
    }

    private String extractFileId(String json) {
        int start = json.indexOf("\"id\": \"") + 7;
        int end = json.indexOf("\"", start);
        if (start > 6 && end > start) {
            return json.substring(start, end);
        }
        return null;
    }
}
