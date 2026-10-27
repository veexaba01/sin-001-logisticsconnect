package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

public class DelayStageServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7052);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Tracks the Transit Delay Stage (0-8, e.g. weather shutdowns).)
        // Add domain endpoints for delay-stage-service here.
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
