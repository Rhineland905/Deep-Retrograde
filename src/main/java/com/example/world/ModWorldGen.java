package com.example.world;

import com.example.init.BlockInit;
import net.minecraft.block.Block;
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
    private WorldGenerator tuff_generator;
    private boolean isInitialized = false;

    private void init() {
        if (isInitialized) return;
        tuff_generator = new WorldGenMinable(BlockInit.tuff.getDefaultState(), 32, BlockMatcher.forBlock(BlockInit.deepslate));
        isInitialized = true;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            init();

            replaceVanillaBlocks(world, chunkX, chunkZ);
            generateCustomFeatures(world, random, chunkX, chunkZ);
        }
    }

    private void replaceVanillaBlocks(World world, int chunkX, int chunkZ) {
        Chunk chunk = world.getChunk(chunkX, chunkZ);
        Random random = world.rand; // Берем рандом мира

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                // Увеличиваем диапазон проверки до 30, чтобы создать зону смешивания
                for (int y = 0; y <= 30; y++) {
                    BlockPos pos = new BlockPos(worldX, y, worldZ);
                    Block currentBlock = chunk.getBlockState(pos).getBlock();

                    // Логика размытия границы:
                    // До 15 высоты — сланец 100%
                    // От 15 до 25 — шанс плавно падает
                    boolean shouldBeDeepslate = false;

                    if (y <= 15) {
                        shouldBeDeepslate = true;
                    } else if (y <= 25) {
                        // Рассчитываем шанс: на 16 высоте он ~90%, на 24 высоте ~10%
                        float chance = 1.0f - ((float)(y - 15) / (25 - 15));
                        if (random.nextFloat() < chance) {
                            shouldBeDeepslate = true;
                        }
                    }

                    if (shouldBeDeepslate) {
                        // === 1. ЗАМЕНА ПОРОДЫ ===
                        if (currentBlock == Blocks.STONE || currentBlock == Blocks.BEDROCK || currentBlock == Blocks.DIRT || currentBlock == Blocks.GRAVEL) {
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
        // === ТУФ ===
        int tuffChances = 2;
        for (int i = 0; i < tuffChances; i++) {
            int x = chunkX * 16 + 8 + random.nextInt(16);
            int y = random.nextInt(21);
            int z = chunkZ * 16 + 8 + random.nextInt(16);
            tuff_generator.generate(world, random, new BlockPos(x, y, z));
        }

        // === КАЛЬЦИТ ===
        if (random.nextFloat() < 0.20f) {
            int cx = chunkX * 16 + 8 + random.nextInt(16);
            int cy = 5 + random.nextInt(15);
            int cz = chunkZ * 16 + 8 + random.nextInt(16);
            generateSphere(world, new BlockPos(cx, cy, cz), BlockInit.calcite, 2);
        }

        // === СКАЛК (Deep Dark биом) ===
        // Шанс 15% на появление пятна скалка в чанке
        if (random.nextFloat() < 0.15f) {
            int sx = chunkX * 16 + 8 + random.nextInt(16);
            int sy = 5 + random.nextInt(15); // Ищем место от Y=5 до Y=20
            int sz = chunkZ * 16 + 8 + random.nextInt(16);

            generateSculkPatch(world, random, new BlockPos(sx, sy, sz), 4);
        }
    }

    private void generateSphere(World world, BlockPos center, Block block, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        BlockPos pos = center.add(x, y, z);
                        Block currentBlock = world.getBlockState(pos).getBlock();
                        if (currentBlock == Blocks.STONE || currentBlock == BlockInit.deepslate) {
                            world.setBlockState(pos, block.getDefaultState(), 2);
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
        // Проверяем область вокруг центра
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    if (x * x + y * y + z * z <= radius * radius && random.nextFloat() < 0.7f) {
                        BlockPos currentPos = center.add(x, y, z);
                        Block currentBlock = world.getBlockState(currentPos).getBlock();

                        if (currentBlock == Blocks.STONE || currentBlock == BlockInit.deepslate || currentBlock == BlockInit.tuff) {
                            if (world.isAirBlock(currentPos.up()) || world.isAirBlock(currentPos.down()) ||
                                    world.isAirBlock(currentPos.north()) || world.isAirBlock(currentPos.south()) ||
                                    world.isAirBlock(currentPos.east()) || world.isAirBlock(currentPos.west())) {

                                world.setBlockState(currentPos, BlockInit.sculk.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }
        }
    }
}