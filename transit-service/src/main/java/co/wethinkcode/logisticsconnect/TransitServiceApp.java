package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TransitServiceApp {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7053);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Calculates estimated arrival windows based on hub and delay stage.)
        // Add domain endpoints for transit-service here.
    }

    /**
     * Calculates estimated transit time in hours.
     */
    public static double calculateEtaHours(int delayStage)
    {
        return 24.0 + (delayStage * 4.0);
    }

    /**
     * Extracts the integer delay stage after the stage key in a JSON payload.
     */
    public static int extractStage(String json)
    {
        if (json == null || !json.contains("\"stage\""))
        {
            return 0;
        }
        String afterStage = json.substring(json.indexOf("\"stage\"") + 7);
        String digits = afterStage.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    /**
     * Formats the hub ETA response as a JSON string.
     */
    public static String buildEtaJson(String hubId, int stage, double etaHours)
    {
        return "{\"hubId\":\"" + hubId.trim().toUpperCase()
                + "\",\"delayStage\":" + stage
                + ",\"estimatedHours\":" + etaHours + "}";
    }

    /**
     * Calls hub-service (port 7051) to verify whether a hub ID exists.
     */
    private static boolean isValidHub(String hubId) throws Exception
    {
        String cleanId = hubId.trim().toUpperCase();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7051/hubs/" + cleanId))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    /**
     * Queries delay-stage-service (port 7052) for the hub's current delay stage.
     */
    private static int fetchDelayStage(String hubId)
    {
        try
        {
            String cleanId = hubId.trim().toUpperCase();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7052/delay-stage/" + cleanId))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return extractStage(response.body());
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.logisticsconnect.mq.MqConfig)
