package com.marcusslover.plus.lib.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {


    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public ChunkGenerator.ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biome) {
        return this.createChunkData(world);
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    @Override
    public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        final Location spawnLocation = new Location(world, 0.0D, 64.0D, 0.0D);
        final Location blockLocation = spawnLocation.clone().subtract(0D, 1D, 0D);
        blockLocation.getBlock().setType(Material.BEDROCK);
        return spawnLocation;
    }
}
