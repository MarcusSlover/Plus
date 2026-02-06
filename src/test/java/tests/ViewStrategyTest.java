package tests;

import com.marcusslover.plus.lib.item.Button;
import com.marcusslover.plus.lib.item.Canvas.BasicPopulator.ViewStrategy;
import com.marcusslover.plus.lib.item.Canvas.BasicPopulator.ViewStrategy.ViewStrategyContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ViewStrategyTest {

    @Test
    public void test() {
        ViewStrategy viewStrategy = ViewStrategy.parseLayout(false, new String[]{
            "---------",
            "--aaaaa--",
            "--aaaaa--",
            "--ccccc--",
            "--ddddd--",
            "---------",
        });
        List<Integer> elements = new ArrayList<>();
        elements.add(100);
        elements.add(200);
        elements.add(300);
        elements.add(400);
        elements.add(500);
        int counter = 0;
        for (Integer element : elements) {
            Button button = Button.create(counter); // creating the button
            ViewStrategyContext ctx = ViewStrategyContext.of(counter, null, button);
            int handle = viewStrategy.handle(ctx);
            System.out.println("Button: " + button);
            System.out.println("Element: " + element + ", Handle: " + handle);
            counter++;
        }
    }
}
