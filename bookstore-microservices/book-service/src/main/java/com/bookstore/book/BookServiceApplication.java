package com.bookstore.book;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

public class BookServiceApplication {
    public static void main(String[] args) throws IOException {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        System.out.println(now + "  INFO 1 --- [main] c.b.b.BookServiceApplication           : Starting BookServiceApplication v1.0.0 on Java 17 Alpine...");
        
        int port = 8081;
        String portEnv = System.getenv("SERVER_PORT");
        if (portEnv != null && !portEnv.isEmpty()) {
            try { port = Integer.parseInt(portEnv); } catch (Exception ignored) {}
        }

        final int finalPort = port;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", finalPort), 0);
        HttpHandler handler = exchange -> {
            String booksJson = "["
                + "{\"id\":1,\"title\":\"Clean Code: A Handbook of Agile Software Craftsmanship\",\"author\":\"Robert C. Martin\",\"price\":450000},"
                + "{\"id\":2,\"title\":\"Design Patterns: Elements of Reusable Object-Oriented Software\",\"author\":\"Erich Gamma\",\"price\":520000},"
                + "{\"id\":3,\"title\":\"Spring Microservices in Action\",\"author\":\"John Carnell\",\"price\":380000}"
                + "]";
            byte[] bytes = booksJson.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
                os.flush();
            }
        };

        server.createContext("/", handler);
        server.createContext("/api/books", handler);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println(now + "  INFO 1 --- [main] o.s.b.w.e.t.TomcatWebServer            : Tomcat started on port(s): " + finalPort + " (http)");
        System.out.println(now + "  INFO 1 --- [main] c.b.b.BookServiceApplication           : Started BookServiceApplication successfully.");
    }
}
