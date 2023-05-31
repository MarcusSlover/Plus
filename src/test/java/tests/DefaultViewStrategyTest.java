package tests;

import com.marcusslover.plus.lib.item.Canvas;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DefaultViewStrategyTest {
    @Test
    public void test() {
        List<Integer> integers = Canvas.PopulatorContext.DefaultViewStrategy.middleSlots(1);
        System.out.println(integers);
        integers = Canvas.PopulatorContext.DefaultViewStrategy.middleSlots(2);
        System.out.println(integers);
        integers = Canvas.PopulatorContext.DefaultViewStrategy.middleSlots(3);
        System.out.println(integers);
        integers = Canvas.PopulatorContext.DefaultViewStrategy.middleSlots(4);
        System.out.println(integers);
        integers = Canvas.PopulatorContext.DefaultViewStrategy.middleSlots(5);
        System.out.println(integers);
        integers = Canvas.PopulatorContext.DefaultViewStrategy.middleSlots(6);
        System.out.println(integers);
    }
}
