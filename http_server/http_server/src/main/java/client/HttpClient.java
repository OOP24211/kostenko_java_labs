package client;

import java.net.*;
import java.io.*;
import server.HttpRequest;
import server.HttpResponse;
import server.HttpParser;

public class HttpClient {

    private String host;
    private int port;

    public HttpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public HttpResponse send(HttpRequest request) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            // Отправляем запрос
            OutputStream output = socket.getOutputStream();
            output.write(HttpParser.toString(request).getBytes("UTF-8"));
            output.flush();


            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Читаем статус
            String statusLine = reader.readLine();
            System.out.println("Response status: " + statusLine);

            // Читаем заголовки
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                System.out.println("Header: " + line);
            }

            // Читаем тело
            StringBuilder body = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1){
                body.append((char) ch);
            }

            HttpResponse response = new HttpResponse();
            response.setBody(body.toString());

            return response;
        }
    }

    public HttpResponse get(String path) throws IOException {
        HttpRequest request = new HttpRequest();
        request.setMethod("GET");
        request.setPath(path);
        request.setVersion("HTTP/1.1");
        request.setHeader("Host", host);
        request.setHeader("Connection", "close");

        return send(request);
    }

    public HttpResponse post(String path, String body) throws IOException {
        HttpRequest request = new HttpRequest();
        request.setMethod("POST");
        request.setPath(path);
        request.setVersion("HTTP/1.1");
        request.setHeader("Host", host);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Content-Length", String.valueOf(body.getBytes().length));
        request.setHeader("Connection", "close");
        request.setBody(body);

        return send(request);
    }

    public static void main(String[] args) {
        try {
            HttpClient client = new HttpClient("localhost", 8080);

            System.out.println("=== GET / ===");
            HttpResponse getResponse = client.get("/");
            System.out.println("Body: " + getResponse.getBody());

            System.out.println("\n=== GET /api/hello ===");
            HttpResponse helloResponse = client.get("/api/hello");
            System.out.println("Body: " + helloResponse.getBody());

            System.out.println("\n=== POST /api/data ===");
            String jsonData = "{\"name\":\"Alice\",\"message\":\"Hello from client!\"}";
            HttpResponse postResponse = client.post("/api/data", jsonData);
            System.out.println("Status: " + postResponse.getStatusCode());
            System.out.println("Body: " + postResponse.getBody());

            System.out.println("\n=== POST /api/users ===");
            String newUser = "{\"username\":\"john_doe\",\"email\":\"john@example.com\"}";
            HttpResponse userResponse = client.post("/api/users", newUser);
            System.out.println("Status: " + userResponse.getStatusCode());
            System.out.println("Body: " + userResponse.getBody());


        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}