package backend2;

import junit.framework.TestCase;

/**
 * Created by home on 9/20/16.
 */
public class RobustInputOutputTest extends TestCase {
    public void testCases() {
        // sync <= now

        assertEquals(50, RobustInputOutput.stallTime(100,2100)); // exactly on sync
        assertEquals(1, RobustInputOutput.stallTime(100,2100+50-1)); // far end of sync range
        assertEquals(750, RobustInputOutput.stallTime(100,2100-700)); // earliest end of sync range
        assertEquals(0, RobustInputOutput.stallTime(100,2100-700-1)); // before sync range
        assertEquals(0, RobustInputOutput.stallTime(100,2100+50+1)); // after sync range
        assertEquals(0, RobustInputOutput.stallTime(100,2100-50+200)); // completely outside of sync range

        // sync near starting boundary

        assertEquals(750, RobustInputOutput.stallTime(25,2025-700)); // earliest end of sync range
        assertEquals(0, RobustInputOutput.stallTime(25,2025-700-1)); // before sync range

        // sync near ending boundary

        assertEquals(0, RobustInputOutput.stallTime(1900,3900+50+1)); // after sync range
        assertEquals(0, RobustInputOutput.stallTime(1900,3900-50+200)); // completely outside of sync range
        assertEquals(1, RobustInputOutput.stallTime(1900,3900+50-1)); // far end of sync range

    }
}
