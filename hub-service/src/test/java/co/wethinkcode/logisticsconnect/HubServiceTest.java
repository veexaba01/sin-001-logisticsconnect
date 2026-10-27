package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HubServiceTest
{
    /**
     * Verifies that known hub IDs return true and unknown IDs return false.
     */
    @Test
    public void testIsValidHubId_ChecksKnownAndUnknownIds()
    {
        String sampleJson = "[{\"id\":\"H-501\",\"province\":\"Western Cape\",\"sortingCenter\":\"Cape Town Port\"}]";

        assertTrue(HubServiceApp.isValidHubId(sampleJson, "h-501"));
        assertFalse(HubServiceApp.isValidHubId(sampleJson, "H-999"));
    }
}