package co.wethinkcode.logisticsconnect;

/**
 * Cleans and normalizes raw hub records from hubs-global.csv.
 */
public class IngestionService
{
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
     * Trims whitespace and converts the hub ID to uppercase.
     */
    private String cleanId(String raw)
    {
        if (raw == null || raw.trim().isEmpty())
        {
            return null;
        }
        return raw.trim().toUpperCase();
    }

    /**
     * Trims, collapses spaces, and normalizes province names and KwaZulu-Natal variants.
     */
    private String cleanProvince(String raw)
    {
        if (raw == null || raw.trim().isEmpty())
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
     * Trims, collapses internal double spaces, and Title-Cases the sorting center name.
     */
    private String cleanSortingCenter(String raw)
    {
        if (raw == null || raw.trim().isEmpty())
        {
            return null;
        }
        String collapsed = raw.trim().replaceAll("[\\s\\u00A0]+", " ");
        return toTitleCase(collapsed);
    }

    /**
     * Normalizes the active flag boolean representation.
     */
    private boolean cleanActiveFlag(String raw)
    {
        if (raw == null)
        {
            return false;
        }
        return raw.trim().equalsIgnoreCase("y");
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