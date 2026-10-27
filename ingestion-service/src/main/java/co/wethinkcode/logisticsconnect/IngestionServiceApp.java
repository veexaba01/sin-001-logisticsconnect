package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7050);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/hubs-global.csv (hubs, sorting centers, regional districts data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
        List<Hub> hubs = loadCleanedHubs();
        app.get("hubs", ctx -> ctx.contentType("application/json").result(toJsonArray(hubs)));
    }

    /**
     * Loads and cleans hubs-global.csv from the classpath.
     */
    public static List<Hub> loadCleanedHubs()
    {
        IngestionService service = new IngestionService();
        InputStream csvStream = IngestionServiceApp.class.getResourceAsStream("/hubs-global.csv");
        return service.cleanCsv(csvStream);
    }

    /**
     * Serializes a list of Hub objects into a JSON array string.
     */
    public static String toJsonArray(List<Hub> hubs)
    {
        List<String> jsonItems = new ArrayList<>();
        for (Hub item : hubs)
        {
            jsonItems.add(item.toJson());
        }
        return "[" + String.join(",", jsonItems) + "]";
    }
}
