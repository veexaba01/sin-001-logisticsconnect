package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DelayStageServiceTest
{
    /**
     * Verifies that the delay stages 0-8 are valid and parseStage extracts integer stages from JSON.
     */
    @Test
    public void testIsValidStageAndParseStage()
    {
        assertTrue(DelayStageServiceApp.isValidStage(0));
        assertTrue(DelayStageServiceApp.isValidStage(8));
        assertFalse(DelayStageServiceApp.isValidStage(-1));
        assertFalse(DelayStageServiceApp.isValidStage(9));

        assertEquals(3, DelayStageServiceApp.parseStage("{\"stage\": 3}"));
        assertEquals(-1, DelayStageServiceApp.parseStage("invalid"));
    }
}