package Server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoServer {
    private final int port;

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (var server = new ServerSocket(port)) {
            try (var clientSocket = server.accept()) {
                handle(clientSocket);
            }
        } catch (IOException e) {
            System.out.printf("Вероятнее всего порт %s занят.%n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        var input = socket.getInputStream();
        var isr = new InputStreamReader(input, "UTF-8");
        try (var scanner = new Scanner(isr)) {
            while (true) {
                var message = scanner.nextLine().strip();
                System.out.printf("Got: %s%n", message);
                String reversedMessage = new StringBuilder(message).reverse().toString();
                var send = new PrintWriter(socket.getOutputStream(), true);
                send.println(reversedMessage);
                System.out.println("Reversing message: " + reversedMessage);
                if (message.equalsIgnoreCase("bye")) {
                    System.out.println("Bye bye");
                    return;
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Client dropped connection");
        }
    }
}

