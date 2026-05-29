package io.github.opencubicchunks.cubicchunks.api.worldgen;

import io.github.opencubicchunks.cubicchunks.api.worldgen.populator.ICubicPopulator;

/** Compile-only stub — настоящий класс предоставляется CubicChunks в рантайме. */
public class CubeGeneratorsRegistry {

    /** Для кастомных кубических генераторов (world type = CubicChunks). */
    public static void register(ICubicPopulator populator, int priority) {
        throw new UnsupportedOperationException();
    }

    /** Для режима совместимости (cubicchunks:default — обёртка ванильного генератора). */
    public static void registerForCompatibilityGenerator(ICubicPopulator populator) {
        throw new UnsupportedOperationException();
    }
}
