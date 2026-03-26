package server;

import java.net.*;
import java.io.*;

public class HttpServer {
    private ServerSocket serverSocket;
    private int port;
    private boolean running;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;

        System.out.println("Server started on port " + port);
        System.out.println("http://localhost:" + port);

        while (running) {
            Socket client = serverSocket.accept();
            handleClient(client);
        }
    }

    private void handleClient(Socket client) {
        try {
            HttpRequest request = HttpParser.parse(client.getInputStream());

            System.out.println("\nReceived request:");
            System.out.println("  Method: " + request.getMethod());
            System.out.println("  Path: " + request.getPath());
            System.out.println("  Headers: " + request.getHeaders());
            if (request.getBody() != null && !request.getBody().isEmpty()) {
                System.out.println("  Body: " + request.getBody());
            }

            HttpResponse response = handleRequest(request);

            OutputStream output = client.getOutputStream();
            output.write(response.toBytes());
            output.flush();

            System.out.println("Sent response: " + response.getStatusCode());

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();


        if (request.getMethod().equals("POST")) {
            switch (request.getPath()) {
                case "/api/data":
                    response.setStatusCode(200);
                    response.setStatusText("OK");
                    response.setHeader("Content-Type", "application/json");

                    // Получаем данные из тела запроса
                    String receivedData = request.getBody();
                    System.out.println(" Получены POST данные: " + receivedData);

                    response.setBody("{\"status\": \"ok\", \"received\": " + receivedData + "}");
                    break;

                case "/api/users":
                    response.setStatusCode(201);  // 201 = Created
                    response.setStatusText("Created");
                    response.setHeader("Content-Type", "application/json");
                    response.setBody("{\"status\": \"created\", \"user\": " + request.getBody() + "}");
                    break;

                default:
                    response.setStatusCode(404);
                    response.setStatusText("Not Found");
                    response.setHeader("Content-Type", "application/json");
                    response.setBody("{\"error\": \"POST endpoint not found\"}");
            }
            return response;
        }

        switch (request.getPath()) {
            case "/":
                response.setStatusCode(200);
                response.setStatusText("OK");
                response.setHeader("Content-Type", "text/html; charset=UTF-8");
                response.setBody("""
                <!DOCTYPE html>
                <html>
                <head><title>Manual HTTP Server</title></head>
                <body>
                    <h1>Welcome!</h1>
                    <p>This is a manual HTTP server</p>
                    <ul>
                        <li><a href="/api/hello">/api/hello</a></li>
                    </ul>
                    <h2>Тестирование POST:</h2>
                    <form method="POST" action="/api/data">
                        <input type="text" name="message" placeholder="Введите сообщение">
                        <button type="submit">Отправить POST</button>
                    </form>
                </body>
                </html>
                """);
                break;

            case "/api/hello":
                response.setStatusCode(200);
                response.setStatusText("OK");
                response.setHeader("Content-Type", "application/json");
                response.setBody("{\"message\": \"Hello from manual server!\"}");
                break;

            default:
                response.setStatusCode(404);
                response.setStatusText("Not Found");
                response.setHeader("Content-Type", "text/html");
                response.setBody("<h1>404 Not Found</h1><p>The page you requested does not exist.</p>");
        }

        return response;
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);
        server.start();
    }
}
