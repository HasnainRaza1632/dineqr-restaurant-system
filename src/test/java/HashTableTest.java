
import com.hasnain.restaurant.dsa.HashTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = new HashTable<>();
    }

    /** Key whose hashCode is always the same, so every key collides. */
    private static class BadKey {
        private final String name;

        BadKey(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BadKey other && Objects.equals(name, other.name);
        }
    }

    /** Key with a chosen hashCode, to test negative and extreme hash values. */
    private static class FixedHashKey {
        private final int hash;
        private final String id;

        FixedHashKey(int hash, String id) {
            this.hash = hash;
            this.id = id;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof FixedHashKey other && Objects.equals(id, other.id);
        }
    }

    // ---------- basics ----------

    @Test
    void newTableIsEmpty() {
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    void defaultCapacityIs16() {
        assertEquals(16, table.capacity());
    }

    @Test
    void invalidInitialCapacityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new HashTable<String, Integer>(0));
        assertThrows(IllegalArgumentException.class, () -> new HashTable<String, Integer>(-5));
    }

    @Test
    void putThenGetReturnsValue() {
        table.put("order-1", 100);

        assertEquals(100, table.get("order-1"));
        assertEquals(1, table.size());
        assertFalse(table.isEmpty());
    }

    @Test
    void putNewKeyReturnsNull() {
        assertNull(table.put("A", 1));
    }

    @Test
    void putExistingKeyOverwritesAndReturnsOldValue() {
        table.put("A", 1);

        assertEquals(1, table.put("A", 2));
        assertEquals(2, table.get("A"));
        assertEquals(1, table.size());
    }

    @Test
    void getMissingKeyReturnsNull() {
        assertNull(table.get("missing"));
    }

    @Test
    void containsKeyWorks() {
        table.put("A", 1);

        assertTrue(table.containsKey("A"));
        assertFalse(table.containsKey("B"));
    }

    @Test
    void nullKeyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
    }

    @Test
    void keysAreComparedByEqualsNotIdentity() {
        table.put(new String("table-5"), 5);

        assertEquals(5, table.get(new String("table-5")));
    }

    // ---------- remove ----------

    @Test
    void removeReturnsValueAndDeletesKey() {
        table.put("A", 1);

        assertEquals(1, table.remove("A"));
        assertFalse(table.containsKey("A"));
        assertNull(table.get("A"));
        assertEquals(0, table.size());
    }

    @Test
    void removeMissingKeyReturnsNullAndKeepsSize() {
        table.put("A", 1);

        assertNull(table.remove("B"));
        assertEquals(1, table.size());
    }

    @Test
    void canReinsertAfterRemove() {
        table.put("A", 1);
        table.remove("A");
        table.put("A", 2);

        assertEquals(2, table.get("A"));
        assertEquals(1, table.size());
    }

    // ---------- collisions ----------

    @Test
    void collidingKeysAreAllStoredAndFound() {
        HashTable<BadKey, Integer> t = new HashTable<>();
        t.put(new BadKey("A"), 1);
        t.put(new BadKey("B"), 2);
        t.put(new BadKey("C"), 3);

        assertEquals(3, t.size());
        assertEquals(1, t.get(new BadKey("A")));
        assertEquals(2, t.get(new BadKey("B")));
        assertEquals(3, t.get(new BadKey("C")));
    }

    @Test
    void overwriteWorksInsideACollisionChain() {
        HashTable<BadKey, Integer> t = new HashTable<>();
        t.put(new BadKey("A"), 1);
        t.put(new BadKey("B"), 2);

        assertEquals(2, t.put(new BadKey("B"), 20));
        assertEquals(20, t.get(new BadKey("B")));
        assertEquals(2, t.size());
    }

    @Test
    void removeFromHeadMiddleAndTailOfChain() {
        HashTable<BadKey, Integer> t = new HashTable<>();
        t.put(new BadKey("A"), 1);
        t.put(new BadKey("B"), 2);
        t.put(new BadKey("C"), 3);
        t.put(new BadKey("D"), 4);

        assertEquals(2, t.remove(new BadKey("B"))); // middle
        assertEquals(1, t.remove(new BadKey("A"))); // one end
        assertEquals(4, t.remove(new BadKey("D"))); // other end

        assertEquals(1, t.size());
        assertEquals(3, t.get(new BadKey("C")));
        assertNull(t.get(new BadKey("A")));
    }

    @Test
    void negativeAndExtremeHashCodesWork() {
        HashTable<FixedHashKey, String> t = new HashTable<>();
        FixedHashKey minValue = new FixedHashKey(Integer.MIN_VALUE, "min");
        FixedHashKey negative = new FixedHashKey(-17, "neg");
        FixedHashKey maxValue = new FixedHashKey(Integer.MAX_VALUE, "max");

        t.put(minValue, "a");
        t.put(negative, "b");
        t.put(maxValue, "c");

        assertEquals("a", t.get(minValue));
        assertEquals("b", t.get(negative));
        assertEquals("c", t.get(maxValue));
    }

    // ---------- resizing ----------

    @Test
    void capacityDoublesWhenLoadFactorExceeded() {
        HashTable<String, Integer> t = new HashTable<>(4);

        t.put("A", 1);
        t.put("B", 2);
        t.put("C", 3);
        assertEquals(4, t.capacity()); // size 3 is not above 0.75 * 4 = 3

        t.put("D", 4);
        assertEquals(8, t.capacity()); // size 4 is above 3, so double
    }

    @Test
    void overwritingDoesNotTriggerResize() {
        HashTable<String, Integer> t = new HashTable<>(4);
        t.put("A", 1);
        t.put("B", 2);
        t.put("C", 3);

        t.put("A", 10);
        t.put("B", 20);
        t.put("C", 30);

        assertEquals(4, t.capacity());
    }

    @Test
    void allEntriesSurviveResizing() {
        HashTable<String, Integer> t = new HashTable<>(2);

        for (int i = 0; i < 1000; i++) {
            t.put("key" + i, i);
        }

        assertEquals(1000, t.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, t.get("key" + i));
        }
    }

    // ---------- keys, clear, scale ----------

    @Test
    void keysReturnsEveryKeyExactlyOnce() {
        table.put("A", 1);
        table.put("B", 2);
        table.put("C", 3);

        Set<String> found = new HashSet<>();
        int count = 0;
        for (String k : table.keys()) {
            found.add(k);
            count++;
        }

        assertEquals(3, count);
        assertEquals(Set.of("A", "B", "C"), found);
    }

    @Test
    void keysOnEmptyTableIsEmpty() {
        assertFalse(table.keys().iterator().hasNext());
    }

    @Test
    void clearRemovesEverything() {
        table.put("A", 1);
        table.put("B", 2);
        table.clear();

        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
        assertNull(table.get("A"));
        assertFalse(table.keys().iterator().hasNext());
    }

    @Test
    void tableStillWorksAfterClear() {
        table.put("A", 1);
        table.clear();
        table.put("B", 2);

        assertEquals(2, table.get("B"));
        assertEquals(1, table.size());
    }

    @Test
    void handlesManyInsertsAndRemoves() {
        int n = 20_000;
        for (int i = 0; i < n; i++) {
            table.put("order-" + i, i);
        }
        for (int i = 0; i < n; i += 2) {
            table.remove("order-" + i); // remove the even ones
        }

        assertEquals(n / 2, table.size());
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                assertNull(table.get("order-" + i));
            } else {
                assertEquals(i, table.get("order-" + i));
            }
        }
    }
}