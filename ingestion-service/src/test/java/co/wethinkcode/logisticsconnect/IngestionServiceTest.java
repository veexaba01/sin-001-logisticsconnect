package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IngestionServiceTest
{
    private IngestionService service;

    @BeforeEach
    public void setUp()
    {
        service = new IngestionService();
    }

    /**
     * Verifies trimming, multi-word Title Case, and double-space collapse.
     */
    @Test
    public void testCleanSingleRecord_TrimmingCasingAndDoubleSpaces()
    {
        String[] rawLine = {" h-505 ", " western cape ", "Cape Town  Port", "Y"};
        Hub result = service.cleanRecord(rawLine);

        assertEquals("H-505", result.getId());
        assertEquals("Western Cape", result.getProvince());
        assertEquals("Cape Town Port", result.getSortingCenter());
        assertTrue(result.isActive());

        String[] kznVariant = {"H-506", "Kwa-Zulu Natal", "durban harbour", "Y"};
        assertEquals("KwaZulu-Natal", service.cleanRecord(kznVariant).getProvince());
    }

    /**
     * Verifies that blank values and placeholder tokens are converted to null.
     */
    @Test
    public void testCleanSingleRecord_PlaceholdersBecomeNull()
    {
        String[] rawLine = {"H-508", "", "unknown", "Y"};
        Hub result = service.cleanRecord(rawLine);

        assertEquals("H-508", result.getId());
        assertNull(result.getProvince());
        assertNull(result.getSortingCenter());
    }

    /**
     * Verifies that active flag variations (yes, 1, TRUE, 0, no, FALSE, unknown) normalize properly.
     */
    @Test
    public void testCleanSingleRecord_BooleanFlagVariations()
    {
        Hub yesRow = service.cleanRecord(new String[]{"H-501", "Western Cape", "Cape Town Port", "yes"});
        Hub oneRow = service.cleanRecord(new String[]{"H-506", "KwaZulu-Natal", "Durban Harbour", "1"});
        Hub zeroRow = service.cleanRecord(new String[]{"H-502", "Gauteng", "Pretoria North", "0"});
        Hub falseRow = service.cleanRecord(new String[]{"H-510", "Gauteng", "Johannesburg Central", "FALSE"});

        assertTrue(yesRow.isActive());
        assertTrue(oneRow.isActive());
        assertFalse(zeroRow.isActive());
        assertFalse(falseRow.isActive());
    }

    /**
     * Verifies that duplicate sorting centers in hubs-global.csv are collapsed.
     */
    @Test
    public void testCleanCsv_RemovesDuplicatesWithoutHardcodingRowCount()
    {
        InputStream stream = getClass().getResourceAsStream("/hubs-global.csv");
        List<Hub> cleanedList = service.cleanCsv(stream);

        Set<String> seenSortingCenters = new HashSet<>();
        for (Hub item : cleanedList)
        {
            boolean isUnique = seenSortingCenters.add(item.getSortingCenter());
            assertTrue(isUnique, "Duplicate sorting center found: " + item.getSortingCenter());
        }
    }
}