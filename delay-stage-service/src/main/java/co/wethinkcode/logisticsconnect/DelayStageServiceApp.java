package co.wethinkcode.logisticsconnect;

import co.wethinkcode.logisticsconnect.mq.MqConfig;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
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
        app.post("/delay-stage/{hubId}", DelayStageServiceApp::handleUpdateStage);
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
     * Updates the delay stage (0-8) for a hub and publishes to ActiveMQ, or returns 400 if invalid.
     */
    private static void handleUpdateStage(Context ctx)
    {
        String hubId = ctx.pathParam("hubId").trim().toUpperCase();
        String input = (ctx.queryParam("stage") != null) ? ctx.queryParam("stage") : ctx.body();
        int newStage = parseStage(input);

        if (isValidStage(newStage))
        {
            stagesByHub.put(hubId, newStage);
            publishStageUpdate(hubId, newStage);
            ctx.status(200).contentType("application/json").result(buildStageJson(hubId, newStage));
        }
        else
        {
            ctx.status(400).contentType("application/json")
                    .result("{\"error\":\"Delay stage must be between 0 and 8\"}");
        }
    }

    /**
     * Publishes the hub's updated delay stage to ActiveMQ topic MqConfig.TOPIC.
     */
    private static void publishStageUpdate(String hubId, int stage)
    {
        try
        {
            ConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(MqConfig.TOPIC);
            MessageProducer producer = session.createProducer(topic);

            TextMessage message = session.createTextMessage(buildStageJson(hubId, stage));
            producer.send(message);
            connection.close();
        }
        catch (Exception e)
        {
            System.out.println("ActiveMQ broker not running: " + e.getMessage());
        }
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
