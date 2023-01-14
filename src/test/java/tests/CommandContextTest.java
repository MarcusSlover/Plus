package tests;

import com.marcusslover.plus.lib.command.CommandContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandContextTest {
    @SuppressWarnings("DataFlowIssue") // i know that sender can't be null but it doesn't matter here
    @Test
    public void childContextTest() {
        String[] parentArgs = new String[]{"world", "plus"};
        CommandContext context = new CommandContext(null, "hello", parentArgs);
        assertEquals(context.parent(), null);
        assertEquals(context.label(), "hello");
        assertArrayEquals(context.args(), parentArgs);

        CommandContext child = context.child(1);
        assertEquals(context, child.parent());
        assertEquals(child.label(), "world");
        assertArrayEquals(child.args(), new String[]{parentArgs[1]});
    }
}
