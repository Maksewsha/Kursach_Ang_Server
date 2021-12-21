import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

class Main {

    public static void main(String[] args) {
        System.out.print("Введите порт для сервера: ");
        int port = new Scanner(System.in).nextInt();
        Server server = new Server(port);
    }
}
