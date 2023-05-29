package tests;

import com.marcusslover.plus.lib.exception.EscapedException;
import com.marcusslover.plus.lib.exception.Handle;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class HandlerTest {

    @Test
    public void testSuppliers() {
        try {
            Handle.throwable(() -> {
                if (true) {
                    throw new TestException("Test");
                }

                return null;
            });
        } catch (Throwable t) {
            fail();
        }

        Handle.throwable(() -> {
            if (true) {
                throw new TestException("Test");
            }

            return null;
        }, (t) -> {
            assertSame(TestException.class, t.getClass());
        });


        AtomicBoolean bool = new AtomicBoolean(false);

        Handle.throwable(() -> {
            if (true) {
                throw new TestException("Test");
            }

            return null;
        }, (t) -> {
            assertSame(TestException.class, t.getClass());
        }, () -> {
            bool.set(true);
        });

        assertTrue(bool.get());
    }

    @Test
    public void testRunnables() {
        try {
            Handle.throwable(() -> {
                if (true) {
                    throw new TestException("Test");
                }
            });
        } catch (Throwable t) {
            fail();
        }

        Handle.throwable(() -> {
            if (true) {
                throw new TestException("Test");
            }
        }, (t) -> {
            assertSame(TestException.class, t.escapedClass());
            assertSame(EscapedException.class, t.getClass());
        });

        AtomicBoolean bool = new AtomicBoolean(false);

        Handle.throwable(() -> {
            if (true) {
                throw new TestException("Test");
            }
        }, (t) -> {
            assertSame(TestException.class, t.escapedClass());
            assertSame(EscapedException.class, t.getClass());
        }, () -> {
            bool.set(true);
        });

        assertTrue(bool.get());
    }

    public static class TestException extends Exception {
        public TestException(String message) {
            super(message);
        }
    }
}
