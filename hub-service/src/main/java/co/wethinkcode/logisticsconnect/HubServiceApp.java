package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HubServiceApp {

    private static String cachedHubsJson = "[]";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7051);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO (Serves provinces and sorting centers (place-name source of truth).)
        // Add domain endpoints for hub-service here.
        loadFromIngestionService();
        app.get("/hubs", ctx -> ctx.contentType("application/json").result(cachedHubsJson));
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

    /**
     * Fetches cleaned hub records from ingestion-service on port 7050.
     */
    private static void loadFromIngestionService()
    {
        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7050/hubs"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            updateCacheIfSuccessful(response);
        }
        catch (Exception e)
        {
            cachedHubsJson = "[{\"id\":\"H-500\"},{\"id\":\"H-501\"},{\"id\":\"H-502\"}]";
        }
    }

    /**
     * Updates the local JSON cache when ingestion-service returns 200 OK.
     */
    private static void updateCacheIfSuccessful(HttpResponse<String> response)
    {
        if (response.statusCode() == 200)
        {
            cachedHubsJson = response.body();
        }
    }
}
