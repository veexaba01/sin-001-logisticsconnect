package co.wethinkcode.logisticsconnect;

/**
 * Represents a single cleaned logistics hub record.
 */
public class Hub
{
    private final String id;
    private final String province;
    private final String sortingCenter;
    private final boolean active;

    public Hub(String id, String province, String sortingCenter, boolean active)
    {
        this.id = id;
        this.province = province;
        this.sortingCenter = sortingCenter;
        this.active = active;
    }

    public String getId()
    {
        return id;
    }

    public String getProvince()
    {
        return province;
    }

    public String getSortingCenter()
    {
        return sortingCenter;
    }

    public boolean isActive()
    {
        return active;
    }

    /**
     * Formats this hub record as a JSON object string.
     */
    public String toJson()
    {
        String provinceVal = (province == null) ? "null" : "\"" + province + "\"";
        String centerVal = (sortingCenter == null) ? "null" : "\"" + sortingCenter + "\"";
        return "{\"id\":\"" + id + "\",\"province\":" + provinceVal
                + ",\"sortingCenter\":" + centerVal + ",\"active\":" + active + "}";
    }
}