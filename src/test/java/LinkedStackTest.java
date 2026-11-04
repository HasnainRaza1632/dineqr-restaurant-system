
import com.hasnain.restaurant.dsa.LinkedStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedStackTest {

    private LinkedStack<String> stack;

    @BeforeEach
    void setUp() {
        stack = new LinkedStack<>();
    }

    @Test
    void newStackIsEmpty() {
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void popReturnsItemsInLifoOrder() {
        stack.push("A");
        stack.push("B");
        stack.push("C");

        assertEquals("C", stack.pop());
        assertEquals("B", stack.pop());
        assertEquals("A", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void peekDoesNotRemoveItem() {
        stack.push("A");

        assertEquals("A", stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    void popOnEmptyStackThrows() {
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }

    @Test
    void peekOnEmptyStackThrows() {
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }

    @Test
    void stackWorksAgainAfterBecomingEmpty() {
        stack.push("A");
        stack.pop();
        stack.push("B");

        assertEquals("B", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void clearEmptiesTheStack() {
        stack.push("A");
        stack.push("B");
        stack.clear();

        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void handlesManyItems() {
        for (int i = 0; i < 100_000; i++) {
            stack.push("item" + i);
        }
        assertEquals(100_000, stack.size());
        assertEquals("item99999", stack.pop());
    }
}