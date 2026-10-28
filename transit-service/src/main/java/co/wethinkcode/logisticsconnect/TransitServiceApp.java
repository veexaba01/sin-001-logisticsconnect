package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

public class TransitServiceApp {

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
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.logisticsconnect.mq.MqConfig)
