import com.hasnain.restaurant.dsa.LinkedQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedQueueTest {

    private LinkedQueue<String> queue;

    @BeforeEach
    void setUp() {
        queue = new LinkedQueue<>();
    }

    @Test
    void newQueueIsEmpty() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void dequeueReturnsItemsInFifoOrder() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertEquals("A", queue.dequeue());
        assertEquals("B", queue.dequeue());
        assertEquals("C", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekDoesNotRemoveItem() {
        queue.enqueue("A");

        assertEquals("A", queue.peek());
        assertEquals(1, queue.size());
    }

    @Test
    void dequeueOnEmptyQueueThrows() {
        assertThrows(NoSuchElementException.class, () -> queue.dequeue());
    }

    @Test
    void peekOnEmptyQueueThrows() {
        assertThrows(NoSuchElementException.class, () -> queue.peek());
    }

    @Test
    void queueWorksAgainAfterBecomingEmpty() {
        queue.enqueue("A");
        queue.dequeue();
        queue.enqueue("B");

        assertEquals("B", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void clearEmptiesTheQueue() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void handlesManyItems() {
        for (int i = 0; i < 100_000; i++) {
            queue.enqueue("item" + i);
        }
        assertEquals(100_000, queue.size());
        assertEquals("item0", queue.dequeue());
    }
}