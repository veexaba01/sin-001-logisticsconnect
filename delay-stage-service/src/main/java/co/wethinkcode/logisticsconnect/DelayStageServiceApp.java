package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class DelayStageServiceApp {

    private static final Map<String, Integer> stagesByHub = new HashMap<>();

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7052);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Tracks the Transit Delay Stage (0-8, e.g. weather shutdowns).)
        // Add domain endpoints for delay-stage-service here.
        app.get("/delay-stage/{hubId}", DelayStageServiceApp::handleGetStage);
    }

    /**
     * Returns the current delay stage (0-8) for the requested hub ID.
     */
    private static void handleGetStage(Context ctx)
    {
        String hubId = ctx.pathParam("hubId").trim().toUpperCase();
        int stage = stagesByHub.getOrDefault(hubId, 0);
        ctx.status(200).contentType("application/json").result(buildStageJson(hubId, stage));
    }

    /**
     * Formats the hub ID and delay stage as a JSON string.
     */
    public static String buildStageJson(String hubId, int stage)
    {
        return "{\"hubId\":\"" + hubId + "\",\"stage\":" + stage + "}";
    }

    /**
     * Returns true if the delay stage is within the valid 0 to 8 range.
     */
    public static boolean isValidStage(int stage)
    {
        return stage >= 0 && stage <= 8;
    }

    /**
     * Extracts the integer delay stage from a query parameter or JSON body, or returns -1 if invalid.
     */
    public static int parseStage(String rawInput)
    {
        if (rawInput == null)
        {
            return -1;
        }
        String digits = rawInput.replaceAll("[^0-9-]", "");
        return digits.isEmpty() ? -1 : Integer.parseInt(digits);
    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.logisticsconnect.mq.MqConfig)
