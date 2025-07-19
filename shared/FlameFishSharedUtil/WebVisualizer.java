// In shared/FlameFishSharedUtil/WebInterfaceUtil.java
package shared.FlameFishSharedUtil;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebVisualizer {
    private static HttpServer server;
    private static ConcurrentHashMap<String, Position> positions = new ConcurrentHashMap<>();
    private static int arenaWidth;
    private static int arenaHeight;

    public static void initialize(int width, int height) {
        arenaWidth = width;
        arenaHeight = height;
        try {
            if (server == null) {
                server = HttpServer.create(new InetSocketAddress(8080), 0);
                server.createContext("/positions", new PositionsHandler());
                server.createContext("/", new StaticHandler());
                server.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updatePositions(Map<Integer, DetectedRobot> detectedRobots, int turn) {
        positions.clear();
        detectedRobots.forEach((id, robot) -> {
            Pose pose = robot.estimateCurrentPose(turn);
            positions.put(String.valueOf(id), new Position(pose.getX(), pose.getY()));
        });
    }

    static class Position {
        public double x, y;
        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    static class PositionsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            positions.forEach((id, pos) -> {
                json.append(String.format(
                    "{\"id\":%s,\"x\":%.2f,\"y\":%.2f},",
                    id, pos.x, pos.y
                ));
            });
            if (json.length() > 1) json.setLength(json.length() - 1);
            json.append("]");

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, json.length());
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.close();
        }
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<!DOCTYPE html><html><body>" +
                "<canvas id='canvas' width='" + arenaWidth + "' height='" + arenaHeight + "'></canvas>" +
                "<script>" +
                "const canvas = document.getElementById('canvas');" +
                "const ctx = canvas.getContext('2d');" +
                "function update() {" +
                "  fetch('/positions').then(r => r.json()).then(robots => {" +
                "    ctx.clearRect(0, 0, canvas.width, canvas.height);" +
                "    ctx.strokeStyle = 'black';" +
                "    ctx.strokeRect(0, 0, canvas.width, canvas.height);" +
                "    robots.forEach(robot => {" +
                "      ctx.beginPath();" +
                "      ctx.arc(robot.x, canvas.height - robot.y, 10, 0, Math.PI*2);" +
                "      ctx.fillStyle = 'red';" +
                "      ctx.fill();" +
                "      ctx.fillText('Bot ' + robot.id, robot.x + 15, canvas.height - robot.y);" +
                "    });" +
                "  });" +
                "}" +
                "setInterval(update, 100);" +
                "</script></body></html>";

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}