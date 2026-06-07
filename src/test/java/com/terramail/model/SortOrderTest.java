package com.terramail.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SortOrderTest {

    @Test
    void testCreateSortOrder() {
        SortOrder sortOrder = new SortOrder(SortOrder.Field.DATE, SortOrder.Direction.DESC);
        assertEquals(SortOrder.Field.DATE, sortOrder.getField());
        assertEquals(SortOrder.Direction.DESC, sortOrder.getDirection());
    }

    @Test
    void testAscendingDirection() {
        SortOrder sortOrder = new SortOrder(SortOrder.Field.SUBJECT, SortOrder.Direction.ASC);
        assertEquals(SortOrder.Direction.ASC, sortOrder.getDirection());
    }

    @Test
    void testReversed() {
        SortOrder asc = new SortOrder(SortOrder.Field.FROM, SortOrder.Direction.ASC);
        SortOrder reversed = asc.reversed();
        assertEquals(SortOrder.Field.FROM, reversed.getField());
        assertEquals(SortOrder.Direction.DESC, reversed.getDirection());

        SortOrder desc = new SortOrder(SortOrder.Field.FROM, SortOrder.Direction.DESC);
        SortOrder reversedDesc = desc.reversed();
        assertEquals(SortOrder.Direction.ASC, reversedDesc.getDirection());
    }

    @Test
    void testNullFieldThrows() {
        assertThrows(NullPointerException.class, () -> new SortOrder(null, SortOrder.Direction.ASC));
    }

    @Test
    void testNullDirectionThrows() {
        assertThrows(NullPointerException.class, () -> new SortOrder(SortOrder.Field.DATE, null));
    }

    @Test
    void testEqualsAndHashCode() {
        SortOrder s1 = new SortOrder(SortOrder.Field.DATE, SortOrder.Direction.DESC);
        SortOrder s2 = new SortOrder(SortOrder.Field.DATE, SortOrder.Direction.DESC);
        SortOrder s3 = new SortOrder(SortOrder.Field.SUBJECT, SortOrder.Direction.DESC);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1, s3);
    }

    @Test
    void testToString() {
        SortOrder sortOrder = new SortOrder(SortOrder.Field.DATE, SortOrder.Direction.DESC);
        String str = sortOrder.toString();
        assertTrue(str.contains("DATE"));
        assertTrue(str.contains("DESCENDING"));
    }

    @Test
    void testFieldValues() {
        assertAll("Sort fields",
            () -> assertEquals("SUBJECT", SortOrder.Field.SUBJECT.name()),
            () -> assertEquals("FROM", SortOrder.Field.FROM.name()),
            () -> assertEquals("DATE", SortOrder.Field.DATE.name()),
            () -> assertEquals("TO", SortOrder.Field.TO.name()),
            () -> assertEquals("CC", SortOrder.Field.CC.name())
        );
    }
}
