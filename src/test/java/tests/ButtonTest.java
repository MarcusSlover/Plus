package tests;

import com.marcusslover.plus.lib.item.Button;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ButtonTest {
    @Test
    public void xy() {
        Button button = Button.create(0, 0);
        assertTrue(button.within(0));
    }

    @Test
    public void xy0() {
        Button button = Button.create(1, 1);
        assertTrue(button.within(10));
    }

    @Test
    public void raw() {
        Button button = Button.create(0);
        assertTrue(button.within(0));
    }

    @Test
    public void rawAll() {
        Button button;
        for (int i = 0; i < 54; i++) {
            button = Button.create(i);
            assertTrue(button.within(i));
        }
    }

    @Test
    public void xyAll() {
        Button button;
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 6; y++) {
                button = Button.create(x, y);
                int i = Button.transformSlot(y, x);
                assertTrue(button.within(i));
            }
        }
    }

}
