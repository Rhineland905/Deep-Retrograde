package io.github.opencubicchunks.cubicchunks.api.worldgen.populator;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import java.util.Random;

/** Compile-only stub — real interface is provided by CubicChunks at runtime. */
public interface ICubicPopulator {
    void generate(World world, Random random, CubePos pos, Biome biome);
}
