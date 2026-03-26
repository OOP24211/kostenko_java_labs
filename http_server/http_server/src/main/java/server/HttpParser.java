package server;

import java.io.*;
import java.util.*;

public class HttpParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, "UTF-8"));

        HttpRequest request = new HttpRequest();

        // 1. Парсим первую строку
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("Empty request");
        }

        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        request.setMethod(parts[0]);
        request.setPath(parts[1]);
        request.setVersion(parts[2]);

        // 2. Парсим заголовки
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                request.setHeader(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        // 3. Парсим тело
        String contentLength = request.getHeader("Content-Length");
        if (!contentLength.isEmpty()) {
            int length = Integer.parseInt(contentLength);
            char[] bodyChars = new char[length];
            reader.read(bodyChars, 0, length);
            request.setBody(new String(bodyChars));
        }

        return request;
    }

    public static String toString(HttpRequest request) {
        StringBuilder sb = new StringBuilder();

        sb.append(request.getMethod()).append(" ")
          .append(request.getPath()).append(" ")
          .append(request.getVersion()).append("\r\n");

        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        sb.append("\r\n");

        if (request.getBody() != null) {
            sb.append(request.getBody());
        }

        return sb.toString();
    }
}
