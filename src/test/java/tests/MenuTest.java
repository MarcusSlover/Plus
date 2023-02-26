package tests;

import com.marcusslover.plus.lib.item.Canvas;
import com.marcusslover.plus.lib.item.Canvas.PopulatorContext.DefaultViewStrategy;
import com.marcusslover.plus.lib.item.Item;
import com.marcusslover.plus.lib.item.Menu;
import com.marcusslover.plus.lib.sound.Note;
import com.marcusslover.plus.lib.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuTest extends Menu {

    @Override
    public void open(@NotNull Canvas emptyCanvas, @NotNull Player player) {
        emptyCanvas
                // Title of the menu
                .title("Test")
                // Rows of the menu
                .rows(6)
                // Populate the menu with elements
                .populate(List.of(1, 2, 3))
                // View strategy
                .viewStrategy(DefaultViewStrategy.MIDDLE)
                // Content of the menu
                .content((element, canvas, button) -> {
                    Item item = Item.of(Material.SHIELD, 1, "Shield #%s".formatted(element));
                    button.item(item); // Item of the button
                    // Click event
                    canvas.button(button, (target, clicked, event) -> {
                        Text name = clicked.name();
                        if (name == null) {
                            name = Text.of("null");
                        }
                        Text.of("You clicked ").append(name).append("!").send(target);
                        Note.of("something", 1, 1).send(target);
                        target.closeInventory();
                    }).handleException(throwable -> {
                       // Handle exception
                    });
                });
        ;
    }
}
