package com.marcusslover.plus.lib.container;

import com.marcusslover.plus.lib.container.extra.InitialLoading;
import com.marcusslover.plus.lib.container.type.MapContainer;
import com.marcusslover.plus.lib.container.type.SingleContainer;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages all containers of the server.
 */
public class ContainerManager {
    @Getter
    private final @NotNull Map<String, AbstractContainer<?>> containerMap = new HashMap<>();

    /**
     * Registers a new container.
     *
     * @param parent    Parent folder name.
     * @param container Instance of the container.
     */
    public void register(@NotNull String parent, @NotNull AbstractContainer<?> container) {
        this.containerMap.put(parent, container);
    }

    /**
     * Initializes all containers.
     *
     * @param plugin Plugin instance.
     */
    public void init(@NotNull Plugin plugin) {
        // check if the plugin is enabled
        if (!plugin.isEnabled()) {
            throw new IllegalStateException("Could not initialize containers because Plugin is not enabled.");
        }

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            boolean mkdirs = dataFolder.mkdirs();
            if (!mkdirs) {
                throw new IllegalStateException("Could not create data folder.");
            }
        }

        /*Creates directories for all containers*/
        for (String parent : this.containerMap.keySet()) {
            File containerFolder = new File(dataFolder, parent);

            if (!containerFolder.exists()) {
                boolean mkdirs = containerFolder.mkdirs();
                if (!mkdirs) {
                    throw new IllegalStateException("Could not create container folder.");
                }
            }

            AbstractContainer<?> container = this.containerMap.get(parent);
            container.setParentFolder(containerFolder);

            /*Extra data settings*/
            InitialLoading initialLoading = this.getInitialLoadingAnnotation(container);
            if (initialLoading == null) {
                continue;
            }
            if (!initialLoading.value()) {
                continue;
            }

            try { // Safe loading.
                if (container instanceof MapContainer<?, ?> mapContainer) {
                    mapContainer.loadAllData();
                } else if (container instanceof SingleContainer<?> singleContainer) {
                    singleContainer.loadAllData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private @Nullable InitialLoading getInitialLoadingAnnotation(@NotNull AbstractContainer<?> container) {
        var klass = container.getClass();
        InitialLoading[] annotationsByType = klass.getAnnotationsByType(InitialLoading.class);
        if (annotationsByType.length >= 1) {
            return annotationsByType[0];
        }
        return null;
    }

    /**
     * You may want to save all containers before the plugin is disabled.
     * It's not a mandatory method, but it's recommended.
     * Additionally, it clears the container map after saving.
     */
    public void shutdown() {
        for (AbstractContainer<?> container : this.containerMap.values()) {
            if (container instanceof SingleContainer<?> singleContainer) {
                try { // Safe saving.
                    singleContainer.saveData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (container instanceof MapContainer<?, ?> mapContainer) {
                try { // Safe saving.
                    mapContainer.saveData(); // Saves all the data.
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.containerMap.clear(); // Clears the map.
    }

    /**
     * Finds a container by its type.
     *
     * @param type Type of the container.
     * @return Optional of the container.
     */
    public <T extends AbstractContainer<?>> @NotNull Optional<T> getByType(@NotNull Class<T> type) {
        //noinspection unchecked
        return this.containerMap.values().stream().filter(x -> x.getClass().equals(type)).findFirst().map(x -> (T) x);
    }
}

