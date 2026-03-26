package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode;
    private String statusText;
    private Map<String, String> headers;
    private String body;

    private HttpResponse handleRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();


        if (request.getMethod().equals("POST") && request.getPath().equals("/api/data")) {
            // Обработка POST запроса
            response.setStatusCode(200);
            response.setStatusText("OK");
            response.setHeader("Content-Type", "application/json");

            // Получаем данные из тела запроса
            String receivedData = request.getBody();
            System.out.println(" Получены данные: " + receivedData);

            response.setBody("{\"status\": \"ok\", \"received\": \"" + receivedData + "\"}");

        } else if (request.getMethod().equals("POST") && request.getPath().equals("/api/users")) {
            // Создание нового пользователя
            response.setStatusCode(201);  // 201 = Created
            response.setStatusText("Created");
            response.setHeader("Content-Type", "application/json");
            response.setBody("{\"status\": \"created\", \"user\": " + request.getBody() + "}");

        } else {

            switch (request.getPath()) {
                case "/":
                    response.setStatusCode(200);
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
                    response.setHeader("Content-Type", "application/json");
                    response.setBody("{\"message\": \"Hello from manual server!\"}");
                    break;

                default:
                    response.setStatusCode(404);
                    response.setStatusText("Not Found");
                    response.setHeader("Content-Type", "text/html");
                    response.setBody("<h1>404 Not Found</h1>");
            }
        }

        return response;
    }

    public HttpResponse() {
        this.headers = new HashMap<>();
        this.statusCode = 200;
        this.statusText = "OK";
    }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeader(String key, String value) { headers.put(key, value); }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public byte[] toBytes() throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        if (body != null) {
            sb.append("Content-Length: ").append(body.getBytes("UTF-8").length).append("\r\n");
        }

        sb.append("\r\n");

        if (body != null) {
            sb.append(body);
        }

        return sb.toString().getBytes("UTF-8");
    }
}
