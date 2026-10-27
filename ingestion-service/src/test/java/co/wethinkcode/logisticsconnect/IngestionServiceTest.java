package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IngestionServiceTest {
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
}