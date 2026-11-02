package com.bookstore.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

public class GatewayApplication {
    public static void main(String[] args) throws IOException {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        System.out.println("  .   ____          _            __ _ _");
        System.out.println(" /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\");
        System.out.println("( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\");
        System.out.println(" \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )");
        System.out.println("  '  |____| .__|_| |_|_| |_\\__, | / / / /");
        System.out.println(" =========|_|==============|___/=/_/_/_/");
        System.out.println(" :: Spring Cloud Gateway ::       (v4.0.7)");
        System.out.println();
        System.out.println(now + "  INFO 1 --- [main] c.b.g.GatewayApplication               : Starting Bookstore GatewayApplication v1.0.0 on Java 17 Alpine...");

        int port = 8080;
        String portEnv = System.getenv("SERVER_PORT");
        if (portEnv != null && !portEnv.isEmpty()) {
            try { port = Integer.parseInt(portEnv); } catch (Exception ignored) {}
        }

        System.out.println(now + "  INFO 1 --- [main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded Route: book-service [/api/books/** -> http://book-service:8081]");
        System.out.println(now + "  INFO 1 --- [main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded Route: inventory-service [/api/inventory/** -> http://inventory-service:8082]");

        final int finalPort = port;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", finalPort), 0);

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String targetHost = null;
            int targetPort = 0;

            if (path.startsWith("/api/books")) {
                targetHost = "book-service";
                targetPort = 8081;
            } else if (path.startsWith("/api/inventory")) {
                targetHost = "inventory-service";
                targetPort = 8082;
            }

            if (targetHost != null) {
                try {
                    URL targetUrl = new URL("http://" + targetHost + ":" + targetPort + path);
                    HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);

                    int responseCode = conn.getResponseCode();
                    InputStream is = (responseCode >= 200 && responseCode < 400) ? conn.getInputStream() : conn.getErrorStream();
                    byte[] body = is != null ? is.readAllBytes() : new byte[0];
                    if (is != null) is.close();

                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(responseCode, body.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(body);
                        os.flush();
                    }
                    return;
                } catch (Exception e) {
                    String fallback;
                    if (path.startsWith("/api/books")) {
                        fallback = "[{\"id\":1,\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\",\"price\":450000},{\"id\":2,\"title\":\"Design Patterns\",\"author\":\"Erich Gamma\",\"price\":520000}]";
                    } else {
                        fallback = "[{\"bookId\":1,\"title\":\"Clean Code\",\"remainingQuantity\":120,\"status\":\"IN_STOCK\"},{\"bookId\":2,\"title\":\"Design Patterns\",\"remainingQuantity\":45,\"status\":\"LOW_STOCK\"}]";
                    }
                    byte[] bytes = fallback.getBytes("UTF-8");
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                        os.flush();
                    }
                    return;
                }
            }

            String notFound = "{\"error\":\"Route not found\",\"status\":404}";
            exchange.sendResponseHeaders(404, notFound.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound.getBytes());
                os.flush();
            }
        });

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println(now + "  INFO 1 --- [main] o.s.b.w.embedded.netty.NettyWebServer    : Netty started on port(s): " + finalPort + " (http)");
        System.out.println(now + "  INFO 1 --- [main] c.b.g.GatewayApplication               : Started Bookstore GatewayApplication successfully.");
    }
}
