package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransitServiceTest
{
    /**
     * Verifies that estimated transit hours increase as the delay stage increases.
     */
    @Test
    public void testCalculateEtaHours_ScalesWithDelayStage()
    {
        assertEquals(24.0, TransitServiceApp.calculateEtaHours(0));
        assertEquals(36.0, TransitServiceApp.calculateEtaHours(3));
        assertEquals(56.0, TransitServiceApp.calculateEtaHours(8));
    }

    /**
     * Verifies that extractStage reads only the stage number and ignores digits in hubId.
     */
    @Test
    public void testExtractStage_ParsesStageFromJson()
    {
        String json = "{\"hubId\":\"H-501\",\"stage\":3}";
        assertEquals(3, TransitServiceApp.extractStage(json));
        assertEquals(0, TransitServiceApp.extractStage("invalid"));
    }
}