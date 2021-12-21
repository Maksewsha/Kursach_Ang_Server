import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Server {
    private static final String SERVER_NAME = "Angelina's server";
    private static final String COMMAND_LIST = "Имеющиеся команды:\n1) /dice - бросок кубика\n2) /hello - приветствие";

    private int serverPort;

    public Server(int serverPort){
        this.serverPort = serverPort;
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(this.serverPort), 0);
        } catch (
                IOException e) {
            System.out.println("Сервер не запустился.");
        }

        server.createContext("/api/dice", httpExchange -> {
            insertTextIntoResponse(messageToJson(true, "Выпало число " + (int) (Math.random() * (7 - 0) + 1)), httpExchange);
            httpExchange.close();
        });

        server.createContext("/", httpExchange -> {
            insertTextIntoResponse(messageToJson(true, SERVER_NAME), httpExchange);
            httpExchange.close();
        });

        server.createContext("/api/", httpExchange -> {
            if ("GET".equals(httpExchange.getRequestMethod())){
                insertTextIntoResponse(messageToJson(true, "Получено пустое сообщение."), httpExchange);
            } else if ("POST".equals(httpExchange.getRequestMethod())){
                InputStreamReader request = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");


                BufferedReader br = new BufferedReader(request);
                int b;
                StringBuilder buf = new StringBuilder();
                while((b = br.read()) != -1){
                    buf.append((char) b);
                }

                insertTextIntoResponse(messageToJson(true, "Вы отправили: " + buf.substring(8, buf.length() - 2)), httpExchange);

                br.close();
                request.close();
                System.out.println(buf);
            }
            httpExchange.close();
        });

        server.createContext("/api/hello", httpExchange -> {
            String responseText = "Привет! Это простой бот-повторюшка. Список команд /help.";
            insertTextIntoResponse(messageToJson(true, responseText), httpExchange);
            httpExchange.close();
        });

        server.createContext("/api/help", httpExchange -> {
            insertTextIntoResponse(messageToJson(true, COMMAND_LIST), httpExchange);
            httpExchange.close();
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Сервер успешно запустился.");
    }

    private void insertTextIntoResponse(String text, HttpExchange httpHandler) throws IOException {
        httpHandler.sendResponseHeaders(200, text.getBytes(StandardCharsets.UTF_8).length);
        OutputStream outputStream = httpHandler.getResponseBody();
        outputStream.write(text.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String messageToJson(boolean status, String message){
        return "{\"message\":\""+message+"\",\"status\":"+status+"}\n";
    }
}
