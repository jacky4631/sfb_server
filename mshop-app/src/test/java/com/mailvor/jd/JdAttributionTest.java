package com.mailvor.jd;

import com.mailvor.utils.JdAttribution;
import com.mailvor.utils.OrderUtil;
import org.junit.Test;
import static org.junit.Assert.*;

public class JdAttributionTest {
    @Test
    public void conversionAndOrderAttributionRoundTripWithoutSubUnionPermission() {
        for (long uid : new long[] {1L, 123L, JdAttribution.POSITION_BASE - 1}) {
            long position = JdAttribution.positionId(uid);
            assertEquals("sfb_" + uid, JdAttribution.subUnionId(uid));
            assertEquals(Long.valueOf(uid), OrderUtil.getJdOrderUser(null, position));
            assertEquals(Long.valueOf(uid), OrderUtil.getJdOrderUser("", position));
            assertEquals(Long.valueOf(uid), OrderUtil.getJdOrderUser("sfb_" + uid, position));
        }
    }

    @Test
    public void rejectsOtherAppsAndLegacyUntaggedOrders() {
        for (Long position : new Long[] {null, -1L, 0L, 123L,
                JdAttribution.POSITION_BASE, 2 * JdAttribution.POSITION_BASE,
                2 * JdAttribution.POSITION_BASE + 123, Long.MAX_VALUE}) {
            assertEquals(Long.valueOf(0), OrderUtil.getJdOrderUser(null, position));
            assertEquals(Long.valueOf(123), OrderUtil.getJdOrderUser("sfb_123", position));
        }
    }

    @Test
    public void invalidSubUnionIdFallsBackToPosition() {
        long position = JdAttribution.positionId(123);
        for (String channel : new String[] {"other_123", "123", "sfb_0123",
                "sfb_-123", "sfb_", "sfb_9223372036854775808"}) {
            assertEquals(Long.valueOf(123), OrderUtil.getJdOrderUser(channel, position));
            assertEquals(Long.valueOf(0), OrderUtil.getJdOrderUser(channel, null));
        }
    }

    @Test
    public void validSubUnionIdTakesPriorityOverMissingOrConflictingPosition() {
        assertEquals(Long.valueOf(124), OrderUtil.getJdOrderUser("sfb_124", JdAttribution.positionId(123)));
        assertEquals(Long.valueOf(124), OrderUtil.getJdOrderUser("sfb_124", null));
        assertEquals(Long.valueOf(Long.MAX_VALUE), OrderUtil.getJdOrderUser("sfb_" + Long.MAX_VALUE, null));
    }

    @Test
    public void anonymousConversionIsAllowedButCannotBindAnOrder() {
        assertEquals(JdAttribution.POSITION_BASE, JdAttribution.positionId(0));
        assertEquals("sfb_0", JdAttribution.subUnionId(0));
        assertEquals(Long.valueOf(0), OrderUtil.getJdOrderUser("sfb_0", JdAttribution.positionId(0)));
    }

    @Test
    public void rejectsUidsThatWouldOverlapAnotherAppRange() {
        for (long uid : new long[] {Long.MIN_VALUE, -1, JdAttribution.POSITION_BASE, Long.MAX_VALUE}) {
            try {
                JdAttribution.positionId(uid);
                fail("Invalid UID accepted: " + uid);
            } catch (IllegalArgumentException expected) {
                // Required to keep different App ranges disjoint.
            }
        }
    }
}
