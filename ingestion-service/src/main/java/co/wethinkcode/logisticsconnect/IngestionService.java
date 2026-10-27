package co.wethinkcode.logisticsconnect;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cleans and normalizes raw hub records from hubs-global.csv.
 */
public class IngestionService
{
    private static final Set<String> PLACEHOLDERS = Set.of("", "n/a", "tbd", "unknown", "-", "nan");
    private static final Set<String> TRUE_FLAGS = Set.of("y", "yes", "1", "true");

    /**
     * Cleans a single 4-column CSV row into a normalized Hub object.
     */
    public Hub cleanRecord(String[] rawLine)
    {
        if (rawLine == null || rawLine.length < 4)
        {
            return null;
        }

        String id = cleanId(rawLine[0]);
        String province = cleanProvince(rawLine[1]);
        String sortingCenter = cleanSortingCenter(rawLine[2]);
        boolean active = cleanActiveFlag(rawLine[3]);

        return new Hub(id, province, sortingCenter, active);
    }

    /**
     * Reads hubs-global.csv from an InputStream, cleans each row, and collapses duplicate sorting centers.
     */
    public List<Hub> cleanCsv(InputStream csvStream)
    {
        Map<String, Hub> uniqueHubs = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                processLine(line, uniqueHubs);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading CSV file", e);
        }

        return new ArrayList<>(uniqueHubs.values());
    }

    /**
     * Cleans one non-header CSV line and keeps the first occurrence of each sorting center.
     */
    private void processLine(String line, Map<String, Hub> uniqueHubs)
    {
        if (isSkippableLine(line))
        {
            return;
        }

        Hub cleaned = cleanRecord(line.split(",", -1));
        if (cleaned != null)
        {
            String key = (cleaned.getSortingCenter() != null) ? cleaned.getSortingCenter() : cleaned.getId();
            uniqueHubs.putIfAbsent(key, cleaned);
        }
    }

    /**
     * Returns true if the line is blank or is the CSV header row.
     */
    private boolean isSkippableLine(String line)
    {
        return line.isBlank() || line.trim().toLowerCase().startsWith("hub_id");
    }

    /**
     * Trims whitespace and converts the hub ID to uppercase, or returns null if placeholder.
     */
    private String cleanId(String raw)
    {
        if (isPlaceholder(raw))
        {
            return null;
        }
        return raw.trim().toUpperCase();
    }

    /**
     * Trims, collapses spaces, and normalizes province names, or returns null if placeholder.
     */
    private String cleanProvince(String raw)
    {
        if (isPlaceholder(raw))
        {
            return null;
        }
        String collapsed = raw.trim().replaceAll("[\\s\\u00A0]+", " ");
        if (collapsed.toLowerCase().contains("zulu"))
        {
            return "KwaZulu-Natal";
        }
        return toTitleCase(collapsed);
    }

    /**
     * Trims, collapses internal double spaces, and Title-Cases the sorting center, or returns null if placeholder.
     */
    private String cleanSortingCenter(String raw)
    {
        if (isPlaceholder(raw))
        {
            return null;
        }
        String collapsed = raw.trim().replaceAll("[\\s\\u00A0]+", " ");
        return toTitleCase(collapsed);
    }

    /**
     * Normalizes the active flag boolean representation (Y, yes, 1, true -> true).
     */
    private boolean cleanActiveFlag(String raw)
    {
        if (raw == null)
        {
            return false;
        }
        String flag = raw.trim().toLowerCase();
        return TRUE_FLAGS.contains(flag);
    }

    /**
     * Returns true if the value is null, blank, or a known placeholder token.
     */
    private boolean isPlaceholder(String value)
    {
        if (value == null)
        {
            return true;
        }
        return PLACEHOLDERS.contains(value.trim().toLowerCase());
    }

    /**
     * Converts a space-separated phrase into Title Case.
     */
    private String toTitleCase(String phrase)
    {
        String[] words = phrase.toLowerCase().split(" ");
        for (int i = 0; i < words.length; i++)
        {
            words[i] = capitalizeWord(words[i]);
        }
        return String.join(" ", words);
    }

    /**
     * Capitalizes the first character of a single word.
     */
    private String capitalizeWord(String word)
    {
        if (word.isEmpty())
        {
            return word;
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}