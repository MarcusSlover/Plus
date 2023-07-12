package tests;

import com.marcusslover.plus.lib.container.extra.InitialLoading;
import com.marcusslover.plus.lib.container.type.MapContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static tests.PlayerDataContainer.PlayerData;

@InitialLoading // Used to load everything on plugin startup
public class PlayerDataContainer extends MapContainer<UUID, PlayerData> {
    public PlayerDataContainer() { // Default constructor
        super(
                UUID::toString, // We use UUID as the key, so we need to convert it to string
                UUID::fromString, // While reading from file, we need to convert it back to UUID
                PlayerData.class // We point to the class that we want to store
        );
    }

    /**
     * This method is called when the container is trying to get a value from the map.
     * We must create a default value for the key, in case it doesn't exist.
     *
     * @param key The key of the object.
     * @return A brand-new instance of the object we want to store.
     */
    @Override
    protected @NotNull PlayerData emptyValue(@NotNull UUID key) {
        return new PlayerData(key); // Not to be confused, each player has its own instance of PlayerData.
    }

    /**
     * This class is used to store the data of the player, it gets serialized and deserialized automatically.
     * It can be pretty much anything, as long as you know how to use GSON serialization.
     * In this case, we are storing the UUID of the player and the amount of kills.
     */
    public static class PlayerData {
        private final UUID uuid;
        private int kills = 0;

        public PlayerData(UUID uuid) {
            this.uuid = uuid;
        }

        public void addKill() {
            this.kills++;
        }

        public UUID getUuid() {
            return uuid;
        }
    }
}
