package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

public class HubServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7051);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Serves provinces and sorting centers (place-name source of truth).)
        // Add domain endpoints for hub-service here.
    }

    /**
     * Checks whether the normalized hub ID exists in the cached JSON list.
     */
    public static boolean isValidHubId(String json, String rawId)
    {
        if (json == null || rawId == null)
        {
            return false;
        }
        String normalizedId = rawId.trim().toUpperCase();
        String searchKey = "\"id\":\"" + normalizedId + "\"";
        return json.contains(searchKey);
    }
}
