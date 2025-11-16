package tests;

import com.marcusslover.plus.lib.item.Button;
import com.marcusslover.plus.lib.item.Canvas;
import com.marcusslover.plus.lib.item.Canvas.BasicPopulator.ViewStrategy;
import com.marcusslover.plus.lib.item.Item;
import com.marcusslover.plus.lib.item.Menu;
import com.marcusslover.plus.lib.sound.Note;
import com.marcusslover.plus.lib.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuTest extends Menu {
    private final ViewStrategy vs = ViewStrategy.parse(new String[]{
        "ooooooooo",
        "ooooxoooo",
        "ooooxoooo",
        "ooooxoooo",
        "ooooxoooo",
        "ooooxoooo",
    });

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
            .viewStrategy(vs/*Canvas.PopulatorContext.DefaultViewStrategy.MIDDLE*/)
            // Pages
            .pageForwards(Button.create(0, 0).item(Item.of(Material.ARROW, 1, "Next page")))
            .pageBackwards(Button.create(0, 1).item(Item.of(Material.ARROW, 1, "Previous page")))
            // Content of the menu
            .content(player, (pop) -> {
                Item item = Item.of(Material.SHIELD, 1, "Shield #%s".formatted(pop.element()));
                pop.button().item(item); // Item of the button
                // Click event
                pop.canvas().button(pop.button(), (click) -> {
                    Text name = click.clickedItem().name();
                    if (name == null) {
                        name = Text.of("null");
                    }
                    Text.of("You clicked ").append(name).append("!").send(click.player());
                    Note.of("something", 1, 1).send(click.player());
                    click.player().closeInventory();
                }).handleException(throwable -> {
                    // Handle exception
                });
            }).end()
            .genericClick((ctx) -> {
                // Generic click event
            }).handleException(throwable -> {
                // Handle exception
            }).end();
    }
}
