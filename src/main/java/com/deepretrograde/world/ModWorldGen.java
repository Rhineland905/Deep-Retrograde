package com.deepretrograde.world;

import com.deepretrograde.init.BlockInit;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class ModWorldGen implements IWorldGenerator {

    private static WorldGenerator tuffGenerator;

    private static WorldGenerator getTuffGenerator() {
        if (tuffGenerator == null) {
            tuffGenerator = new WorldGenMinable(
                    BlockInit.tuff.getDefaultState(), 32, BlockMatcher.forBlock(BlockInit.deepslate));
        }
        return tuffGenerator;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            replaceVanillaBlocks(world, chunkX, chunkZ);
            generateCustomFeatures(world, random, chunkX, chunkZ);
        }
    }

    // В 1.12.2 нет отрицательных Y, поэтому симулируем 1.21.1:
    // Y=0–4   → бедрок (не трогаем, аналог Y < -60 в 1.21.1)
    // Y=4–56  → дипслейт 100% (аналог Y=-60..–8 в 1.21.1)
    // Y=56–72 → зона смешивания (аналог Y=-8..0 в 1.21.1)
    // Y>72    → обычный камень
    private static final int DEEPSLATE_START   = 4;   // выше бедрока
    private static final int DEEPSLATE_SOLID   = 56;  // до этой высоты — 100% дипслейт
    private static final int DEEPSLATE_BLEND   = 72;  // до этой высоты — плавный переход

    private void replaceVanillaBlocks(World world, int chunkX, int chunkZ) {
        Chunk chunk = world.getChunk(chunkX, chunkZ);
        Random random = world.rand;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                for (int y = DEEPSLATE_START; y <= DEEPSLATE_BLEND; y++) {
                    pos.setPos(worldX, y, worldZ);
                    Block currentBlock = chunk.getBlockState(pos).getBlock();

                    boolean shouldBeDeepslate;
                    if (y <= DEEPSLATE_SOLID) {
                        shouldBeDeepslate = true;
                    } else {
                        // Плавное затухание: 100% на DEEPSLATE_SOLID → 0% на DEEPSLATE_BLEND
                        float chance = 1.0f - ((float)(y - DEEPSLATE_SOLID) / (DEEPSLATE_BLEND - DEEPSLATE_SOLID));
                        shouldBeDeepslate = random.nextFloat() < chance;
                    }

                    if (shouldBeDeepslate) {
                        // === 1. ЗАМЕНА ПОРОДЫ ===
                        if (currentBlock == Blocks.STONE || currentBlock == Blocks.DIRT || currentBlock == Blocks.GRAVEL) {
                            chunk.setBlockState(pos, BlockInit.deepslate.getDefaultState());
                        }
                        // === 2. ЗАМЕНА РУД ===
                        else if (currentBlock == Blocks.IRON_ORE) {
                            chunk.setBlockState(pos, BlockInit.deepslate_iron_ore.getDefaultState());
                        } else if (currentBlock == Blocks.COAL_ORE) {
                            chunk.setBlockState(pos, BlockInit.deepslate_coal_ore.getDefaultState());
                        } else if (currentBlock == Blocks.GOLD_ORE) {
                            chunk.setBlockState(pos, BlockInit.deepslate_gold_ore.getDefaultState());
                        } else if (currentBlock == Blocks.DIAMOND_ORE) {
                            chunk.setBlockState(pos, BlockInit.deepslate_diamond_ore.getDefaultState());
                        } else if (currentBlock == Blocks.EMERALD_ORE) {
                            chunk.setBlockState(pos, BlockInit.deepslate_emerald_ore.getDefaultState());
                        } else if (currentBlock == Blocks.REDSTONE_ORE) {
                            chunk.setBlockState(pos, BlockInit.deepslate_redstone_ore.getDefaultState());
                        } else if (currentBlock == Blocks.LAPIS_ORE) {
                            chunk.setBlockState(pos, BlockInit.deepslate_lapis_lazuli_ore.getDefaultState());
                        }
                    }
                }
            }
        }
        chunk.markDirty();
    }



    private void generateCustomFeatures(World world, Random random, int chunkX, int chunkZ) {
        // === ТУФ ===  (Y=4–56, как в 1.21.1 — только в зоне дипслейта)
        int tuffChances = 2;
        for (int i = 0; i < tuffChances; i++) {
            int x = chunkX * 16 + 8 + random.nextInt(16);
            int y = DEEPSLATE_START + random.nextInt(DEEPSLATE_SOLID - DEEPSLATE_START);
            int z = chunkZ * 16 + 8 + random.nextInt(16);
            getTuffGenerator().generate(world, random, new BlockPos(x, y, z));
        }

        // === КАЛЬЦИТ ===  (Y=5–40)
        if (random.nextFloat() < 0.20f) {
            int cx = chunkX * 16 + 8 + random.nextInt(16);
            int cy = DEEPSLATE_START + 1 + random.nextInt(36);
            int cz = chunkZ * 16 + 8 + random.nextInt(16);
            generateSphere(world, new BlockPos(cx, cy, cz), BlockInit.calcite, 2);
        }

        // === СКАЛК ===  (Y=4–30, глубже всего — как Ancient City в 1.21.1)
        if (random.nextFloat() < 0.15f) {
            int sx = chunkX * 16 + 8 + random.nextInt(16);
            int sy = DEEPSLATE_START + random.nextInt(26);
            int sz = chunkZ * 16 + 8 + random.nextInt(16);
            generateSculkPatch(world, random, new BlockPos(sx, sy, sz), 4);
        }
    }

    private void generateSphere(World world, BlockPos center, Block block, int radius) {
        IBlockState placeState = block.getDefaultState();
        int cx = center.getX(), cy = center.getY(), cz = center.getZ();
        int r2 = radius * radius;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= r2) {
                        pos.setPos(cx + dx, cy + dy, cz + dz);
                        Block currentBlock = world.getBlockState(pos).getBlock();
                        if (currentBlock == Blocks.STONE || currentBlock == BlockInit.deepslate) {
                            world.setBlockState(pos, placeState, 2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Алгоритм генерации пятен скалка.
     * Он расползается от центра, заражая только открытые поверхности блоков.
     */
    private void generateSculkPatch(World world, Random random, BlockPos center, int radius) {
        IBlockState sculkState = BlockInit.sculk.getDefaultState();
        int cx = center.getX(), cy = center.getY(), cz = center.getZ();
        int r2 = radius * radius;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz > r2 || random.nextFloat() >= 0.7f) continue;

                    int px = cx + dx, py = cy + dy, pz = cz + dz;
                    pos.setPos(px, py, pz);
                    Block currentBlock = world.getBlockState(pos).getBlock();

                    if (currentBlock == Blocks.STONE || currentBlock == BlockInit.deepslate || currentBlock == BlockInit.tuff) {
                        if (hasAirNeighbor(world, neighbor, px, py, pz)) {
                            world.setBlockState(pos, sculkState, 2);
                        }
                    }
                }
            }
        }
    }

    private boolean hasAirNeighbor(World world, BlockPos.MutableBlockPos n, int x, int y, int z) {
        n.setPos(x, y + 1, z); if (world.isAirBlock(n)) return true;
        n.setPos(x, y - 1, z); if (world.isAirBlock(n)) return true;
        n.setPos(x, y, z - 1); if (world.isAirBlock(n)) return true;
        n.setPos(x, y, z + 1); if (world.isAirBlock(n)) return true;
        n.setPos(x + 1, y, z); if (world.isAirBlock(n)) return true;
        n.setPos(x - 1, y, z); return world.isAirBlock(n);
    }
}