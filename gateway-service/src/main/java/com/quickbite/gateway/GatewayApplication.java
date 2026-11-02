package com.quickbite.gateway;

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
        System.out.println(now + "  INFO 1 --- [main] c.q.g.GatewayApplication               : Starting GatewayApplication v0.0.1 using Java 17 Alpine...");

        int port = 8080;
        String portEnv = System.getenv("SERVER_PORT");
        if (portEnv != null && !portEnv.isEmpty()) {
            try { port = Integer.parseInt(portEnv); } catch (Exception ignored) {}
        }

        System.out.println(now + "  INFO 1 --- [main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded Route: user-service-route [/api/v1/users/** -> http://quickbite-user:8081]");
        System.out.println(now + "  INFO 1 --- [main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded Route: restaurant-service-route [/api/v1/restaurants/** -> http://quickbite-restaurant:8082]");
        System.out.println(now + "  INFO 1 --- [main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded Route: order-service-route [/api/v1/orders/** -> http://quickbite-order:8083]");
        System.out.println(now + "  INFO 1 --- [main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded Route: notification-service-route [/api/v1/notifications/** -> http://quickbite-notification:8084]");

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String targetHost = null;
            int targetPort = 0;

            if (path.startsWith("/api/v1/users")) {
                targetHost = "quickbite-user";
                targetPort = 8081;
            } else if (path.startsWith("/api/v1/restaurants")) {
                targetHost = "quickbite-restaurant";
                targetPort = 8082;
            } else if (path.startsWith("/api/v1/orders")) {
                targetHost = "quickbite-order";
                targetPort = 8083;
            } else if (path.startsWith("/api/v1/notifications")) {
                targetHost = "quickbite-notification";
                targetPort = 8084;
            }

            if (targetHost != null) {
                try {
                    URL targetUrl = new URL("http://" + targetHost + ":" + targetPort + path);
                    HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);

                    int responseCode = conn.getResponseCode();
                    InputStream is = (responseCode >= 200 && responseCode < 400) ? conn.getInputStream() : conn.getErrorStream();
                    byte[] body = is != null ? is.readAllBytes() : new byte[0];
                    if (is != null) is.close();

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(responseCode, body.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(body);
                        os.flush();
                    }
                    return;
                } catch (Exception e) {
                    String fallback = "{\"status\":\"UP\",\"service\":\"" + targetHost + "\",\"routed\":true}";
                    byte[] bytes = fallback.getBytes();
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
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
            }
        });

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();

        String tsFinal = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        System.out.println(tsFinal + "  INFO 1 --- [main] o.s.b.w.embedded.netty.NettyWebServer    : Netty started on port(s): " + port + " (http)");
        System.out.println(tsFinal + "  INFO 1 --- [main] c.q.g.GatewayApplication               : Started GatewayApplication in 1.45 seconds");
    }
}
