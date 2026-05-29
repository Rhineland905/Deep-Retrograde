package com.deepretrograde.world;

import com.deepretrograde.init.BlockInit;
import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.worldgen.populator.ICubicPopulator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class CubicWorldGen implements ICubicPopulator {

    private static final int WORLD_FLOOR   = -64;
    private static final int BEDROCK_CEIL  = -60;
    private static final int DEEPSLATE_TOP =  -8;
    private static final int STONE_START   =   0;
    private static final int LAVA_LEVEL    = -50;
    private static final int WATER_MIN     = -28;
    private static final int WATER_MAX     =  -8;

    private static final float BLEND_INV         = 1f / (STONE_START  - DEEPSLATE_TOP);
    private static final float BEDROCK_RANGE_INV = 1f / (BEDROCK_CEIL - WORLD_FLOOR);

    private static final int SEA_LEVEL  = 64;
    private static final int SNOW_LINE  = 130;
    private static final int MAX_PEAK   = 278;

    private static volatile boolean INITIALIZED = false;
    private static IBlockState CS_AIR, CS_STONE, CS_BEDROCK, CS_LAVA, CS_WATER,
                                CS_DEEPSLATE, CS_DRIPSTONE,
                                CS_GRASS, CS_DIRT, CS_GRAVEL, CS_SNOW_LAYER;

    private static com.google.common.base.Predicate<IBlockState> DEEPSLATE_PRED;

    private static final ConcurrentHashMap<Long, WorldGenMinable> ORE_GEN_CACHE
            = new ConcurrentHashMap<>();

    private static WorldGenMinable tuffGen;

    private static final ThreadLocal<Random> CAVE_RAND = ThreadLocal.withInitial(Random::new);

    private static volatile long cachedWorldSeed = Long.MIN_VALUE;
    private static NoiseGeneratorSimplex noiseRegional;
    private static NoiseGeneratorSimplex noiseMountain;
    private static NoiseGeneratorSimplex noiseRidge;
    private static NoiseGeneratorSimplex noiseDetail;


    private static synchronized void ensureInit() {
        if (INITIALIZED) return;
        CS_AIR        = Blocks.AIR.getDefaultState();
        CS_STONE      = Blocks.STONE.getDefaultState();
        CS_BEDROCK    = Blocks.BEDROCK.getDefaultState();
        CS_LAVA       = Blocks.LAVA.getDefaultState();
        CS_WATER      = Blocks.WATER.getDefaultState();
        CS_DEEPSLATE  = BlockInit.deepslate.getDefaultState();
        CS_DRIPSTONE  = BlockInit.dripstone_block.getDefaultState();
        CS_GRASS      = Blocks.GRASS.getDefaultState();
        CS_DIRT       = Blocks.DIRT.getDefaultState();
        CS_GRAVEL     = Blocks.GRAVEL.getDefaultState();
        CS_SNOW_LAYER = Blocks.SNOW_LAYER.getDefaultState();
        DEEPSLATE_PRED = BlockMatcher.forBlock(BlockInit.deepslate);
        tuffGen = new WorldGenMinable(BlockInit.tuff.getDefaultState(), 32, DEEPSLATE_PRED);
        INITIALIZED = true;
    }


    private static synchronized void initSurfaceNoise(long worldSeed) {
        if (cachedWorldSeed == worldSeed) return;
        Random rng = new Random(worldSeed ^ 0xA5E621F3C19D7B40L);
        noiseRegional = new NoiseGeneratorSimplex(rng);
        noiseMountain = new NoiseGeneratorSimplex(rng);
        noiseRidge    = new NoiseGeneratorSimplex(rng);
        noiseDetail   = new NoiseGeneratorSimplex(rng);
        cachedWorldSeed = worldSeed;
    }

    private static WorldGenMinable getOreGen(Block ore, int veinSize) {
        long key = ((long) Block.getIdFromBlock(ore) << 16) | veinSize;
        WorldGenMinable gen = ORE_GEN_CACHE.get(key);
        if (gen == null) {
            WorldGenMinable n    = new WorldGenMinable(ore.getDefaultState(), veinSize, DEEPSLATE_PRED);
            WorldGenMinable prev = ORE_GEN_CACHE.putIfAbsent(key, n);
            gen = (prev != null) ? prev : n;
        }
        return gen;
    }


    @Override
    public void generate(World world, Random random, CubePos pos, Biome biome) {
        if (world.provider.getDimension() != 0) return;
        ensureInit();
        initSurfaceNoise(world.getSeed());

        int minY = pos.getMinBlockY();
        int maxY = pos.getMaxBlockY();

        if (maxY >= WORLD_FLOOR && minY <= STONE_START)
            fillAndReplace(world, pos, random, minY, maxY);

        if (maxY >= WORLD_FLOOR && minY <= STONE_START)
            generateFeatures(world, random, pos, minY, maxY);

        if (maxY >= BEDROCK_CEIL && minY <= -1)
            generateCaves(world, pos);

        if (maxY >= LAVA_LEVEL + 1 && minY <= -2)
            generateSpeleothems(world, random, pos);

        if (maxY >= BEDROCK_CEIL && minY <= -1)
            placeLiquids(world, pos);

        if (maxY >= SEA_LEVEL)
            generateMountainTerrain(world, pos);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ПОДЗЕМНАЯ ГЕНЕРАЦИЯ
    // ═════════════════════════════════════════════════════════════════════════


    private void fillAndReplace(World world, CubePos pos, Random random, int minY, int maxY) {
        int fromY = Math.max(minY, WORLD_FLOOR);
        int toY   = Math.min(maxY, STONE_START);
        int baseX = pos.getMinBlockX(), baseZ = pos.getMinBlockZ();
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            int wx = baseX + x;
            for (int z = 0; z < 16; z++) {
                int wz = baseZ + z;
                for (int y = fromY; y <= toY; y++) {
                    final IBlockState state;
                    if (y <= BEDROCK_CEIL) {
                        float density = 1f - (float)(y - WORLD_FLOOR) * BEDROCK_RANGE_INV;
                        state = (density > 0f && random.nextFloat() < density)
                                ? CS_BEDROCK : CS_DEEPSLATE;
                    } else if (y <= DEEPSLATE_TOP) {
                        state = CS_DEEPSLATE;
                    } else {
                        float t = 1f - (float)(y - DEEPSLATE_TOP) * BLEND_INV;
                        state = (random.nextFloat() < t) ? CS_DEEPSLATE : CS_STONE;
                    }
                    mp.setPos(wx, y, wz);
                    world.setBlockState(mp, state, 2);
                }
            }
        }
    }


    private void generateFeatures(World world, Random random, CubePos pos,
                                   int minY, int maxY) {
        if (minY >= BEDROCK_CEIL && maxY <= DEEPSLATE_TOP) {
            tuffGen.generate(world, random, pos.randomPopulationPos(random));
            tuffGen.generate(world, random, pos.randomPopulationPos(random));
            if (random.nextFloat() < 0.20f)
                sphere(world, pos.randomPopulationPos(random), BlockInit.calcite, 2);
        }

        if (random.nextFloat() < 0.15f)
            generateOreSphere(world, random, pos, BlockInit.dripstone_block, 3,
                    BEDROCK_CEIL, DEEPSLATE_TOP);

        if (minY < -30 && maxY <= DEEPSLATE_TOP && random.nextFloat() < 0.15f)
            sculk(world, random, pos.randomPopulationPos(random), 4);

        if (random.nextFloat() < 0.30f)
            generateOreVein(world, random, pos, BlockInit.deepslate_coal_ore,           17, 1,  -8,   0);
        generateOreVein(world, random, pos, BlockInit.deepslate_iron_ore,                4, 2, -64,   0);
        generateOreVein(world, random, pos, BlockInit.deepslate_iron_ore,                9, 1, -24,   0);
        generateOreVein(world, random, pos, BlockInit.deepslate_gold_ore,                9, 1, -64,   0);
        generateOreVein(world, random, pos, BlockInit.deepslate_lapis_lazuli_ore,        7, 1, -32,   0);
        generateOreVein(world, random, pos, BlockInit.deepslate_lapis_lazuli_ore,        7, 1, -64,   0);
        generateOreVein(world, random, pos, BlockInit.deepslate_redstone_ore,            8, 2, -64,   0);
        generateOreVein(world, random, pos, BlockInit.deepslate_redstone_ore,            8, 2, -64,  -8);
        // Алмазы: снижена частота на глубине
        generateOreVein(world, random, pos, BlockInit.deepslate_diamond_ore,             8, 1, -64,   0);  // основная (было 2 попытки → 1)
        if (random.nextFloat() < 0.45f)                                                                    // вторая вена ~45% шанс (было всегда)
            generateOreVein(world, random, pos, BlockInit.deepslate_diamond_ore,         6, 1, -64,   0);
        if (random.nextFloat() < 0.25f)                                                                    // крупная глубинная ~25% (было 100%, size 12)
            generateOreVein(world, random, pos, BlockInit.deepslate_diamond_ore,         7, 1, -64, -32);
        generateOreVein(world, random, pos, BlockInit.deepslate_diamond_ore,             4, 1, -32,   0);  // мелкая — только верхняя зона
        if (random.nextFloat() < 0.10f)
            generateOreVein(world, random, pos, BlockInit.deepslate_emerald_ore,         3, 1, -16,   0);

        if (maxY >= WATER_MIN && minY <= WATER_MAX && random.nextFloat() < 0.05f)
            generateWaterPocket(world, random, pos);
    }


    private void generateCaves(World world, CubePos pos) {
        int cubeX = pos.getMinBlockX() >> 4;
        int cubeY = pos.getMinBlockY() >> 4;
        int cubeZ = pos.getMinBlockZ() >> 4;
        long worldSeed = world.getSeed();
        Random cr = CAVE_RAND.get();

        for (int dcx = -2; dcx <= 2; dcx++) {
            for (int dcy = -1; dcy <= 1; dcy++) {
                for (int dcz = -2; dcz <= 2; dcz++) {
                    int nx = cubeX + dcx, ny = cubeY + dcy, nz = cubeZ + dcz;
                    if (ny * 16 + 15 < BEDROCK_CEIL || ny * 16 > -1) continue;

                    long cubeSeed = worldSeed
                            ^ ((long) nx * 341873128712L)
                            ^ ((long) ny * 132897987541L)
                            ^ ((long) nz * 789734567893L);
                    cr.setSeed(cubeSeed);

                    if (cr.nextFloat() < 0.40f) {
                        float sx = nx * 16 + cr.nextInt(16) + 0.5f;
                        float sy = ny * 16 + cr.nextInt(16) + 0.5f;
                        float sz = nz * 16 + cr.nextInt(16) + 0.5f;
                        carveTunnel(world, cr, pos, sx, sy, sz, false);
                        if (cr.nextFloat() < 0.18f)
                            carveTunnel(world, cr, pos, sx, sy, sz, true);
                    }

                    if (cr.nextFloat() < 0.22f) {
                        int rx = nx * 16 + cr.nextInt(16) + 8;
                        int ry = ny * 16 + cr.nextInt(16) + 8;
                        int rz = nz * 16 + cr.nextInt(16) + 8;
                        if (ry > BEDROCK_CEIL && ry < -1)
                            carveLargeRoom(world, cr, pos, rx, ry, rz);
                    }
                }
            }
        }
    }


    private void generateSpeleothems(World world, Random random, CubePos pos) {
        int minY = Math.max(pos.getMinBlockY(), LAVA_LEVEL + 1);
        int maxY = Math.min(pos.getMaxBlockY(), STONE_START - 2);
        if (maxY - minY < 3) return;

        int baseX = pos.getMinBlockX(), baseZ = pos.getMinBlockZ();
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();

        for (int probe = 0; probe < 8; probe++) {
            int wx = baseX + 1 + random.nextInt(14);
            int wz = baseZ + 1 + random.nextInt(14);

            if (random.nextFloat() < 0.40f) {
                for (int wy = minY; wy <= maxY - 1; wy++) {
                    mp.setPos(wx, wy, wz);
                    Block bot = world.getBlockState(mp).getBlock();
                    if (bot != BlockInit.deepslate && bot != Blocks.STONE) continue;
                    mp.setPos(wx, wy + 1, wz);
                    if (world.getBlockState(mp).getBlock() != Blocks.AIR) continue;
                    int h = 1 + random.nextInt(5);
                    for (int i = 1; i <= h; i++) {
                        int sy = wy + i;
                        if (sy > maxY) break;
                        mp.setPos(wx, sy, wz);
                        if (world.getBlockState(mp).getBlock() != Blocks.AIR) break;
                        world.setBlockState(mp, CS_DRIPSTONE, 2);
                    }
                    break;
                }
            }

            if (random.nextFloat() < 0.35f) {
                for (int wy = maxY; wy >= minY + 1; wy--) {
                    mp.setPos(wx, wy, wz);
                    Block top = world.getBlockState(mp).getBlock();
                    if (top != BlockInit.deepslate && top != Blocks.STONE) continue;
                    mp.setPos(wx, wy - 1, wz);
                    if (world.getBlockState(mp).getBlock() != Blocks.AIR) continue;
                    int h = 1 + random.nextInt(3);
                    for (int i = 1; i <= h; i++) {
                        int sy = wy - i;
                        if (sy < minY) break;
                        mp.setPos(wx, sy, wz);
                        if (world.getBlockState(mp).getBlock() != Blocks.AIR) break;
                        world.setBlockState(mp, CS_DRIPSTONE, 2);
                    }
                    break;
                }
            }
        }
    }


    private void placeLiquids(World world, CubePos pos) {
        int minY = Math.max(pos.getMinBlockY(), BEDROCK_CEIL + 1);
        int maxY = Math.min(pos.getMaxBlockY(), LAVA_LEVEL);
        if (minY > maxY) return;

        int minBX = pos.getMinBlockX(), maxBX = pos.getMaxBlockX();
        int minBZ = pos.getMinBlockZ(), maxBZ = pos.getMaxBlockZ();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int wx = minBX; wx <= maxBX; wx++)
            for (int wz = minBZ; wz <= maxBZ; wz++)
                for (int wy = minY; wy <= maxY; wy++) {
                    p.setPos(wx, wy, wz);
                    if (world.getBlockState(p).getBlock() == Blocks.AIR)
                        world.setBlockState(p, CS_LAVA, 2);
                }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ГОРЫ (наземный рельеф)
    // ═════════════════════════════════════════════════════════════════════════

    private void generateMountainTerrain(World world, CubePos pos) {
        int cubMinY = pos.getMinBlockY();
        int cubMaxY = pos.getMaxBlockY();
        if (cubMaxY < SEA_LEVEL || cubMinY > MAX_PEAK) return;

        int minY = Math.max(cubMinY, SEA_LEVEL);
        int maxY = Math.min(cubMaxY, MAX_PEAK);

        int baseX = pos.getMinBlockX(), baseZ = pos.getMinBlockZ();
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            int wx = baseX + x;
            for (int z = 0; z < 16; z++) {
                int wz = baseZ + z;
                int terrainH = getMountainHeight(wx, wz);
                if (terrainH <= SEA_LEVEL) continue;

                for (int y = minY; y <= Math.min(maxY, terrainH); y++) {
                    mp.setPos(wx, y, wz);
                    Block existing = world.getBlockState(mp).getBlock();

                    if (existing != Blocks.AIR) {
                        if (existing == Blocks.GRASS && y < terrainH - 4)
                            world.setBlockState(mp, y < terrainH - 8 ? CS_STONE : CS_DIRT, 2);
                        continue;
                    }

                    world.setBlockState(mp, chooseSurfaceBlock(y, terrainH), 2);
                }

                int snowY = terrainH + 1;
                if (terrainH >= SNOW_LINE && snowY >= minY && snowY <= maxY) {
                    mp.setPos(wx, snowY, wz);
                    if (world.getBlockState(mp).getBlock() == Blocks.AIR)
                        world.setBlockState(mp, CS_SNOW_LAYER, 2);
                }
            }
        }
    }

    /**
     * Выбирает тип блока в зависимости от глубины от поверхности и высоты.
     *
     *  Y = top       : трава (низкие горы) или камень (высокие)
     *  Y = top-1..-3 : дёрт (низкие) или камень (высокие)
     *  Y < top-3     : камень, изредка гравий на крутых склонах
     */
    private IBlockState chooseSurfaceBlock(int y, int terrainH) {
        int depth = terrainH - y;   // 0 = поверхность, чем больше — тем глубже
        if (terrainH >= SNOW_LINE) {
            return CS_STONE;
        }
        if (depth == 0) return CS_GRASS;
        if (depth <= 3)  return CS_DIRT;
        return ((y ^ (y >> 4)) & 0x1F) == 0 ? CS_GRAVEL : CS_STONE;
    }


    private int getMountainHeight(int wx, int wz) {
        double regional = (noiseRegional.getValue(wx * 0.00040, wz * 0.00040) + 1.0) * 0.5;
        if (regional < 0.30) return SEA_LEVEL;
        double factor = (regional - 0.30) / 0.70;

        double base = (noiseMountain.getValue(wx * 0.00250, wz * 0.00250) + 1.0) * 0.5;

        double r1 = Math.abs(noiseRidge.getValue(wx * 0.00700, wz * 0.00700));
        double r2 = Math.abs(noiseRidge.getValue(wx * 0.01400, wz * 0.01400));
        double ridge = 1.0 - (r1 * 0.65 + r2 * 0.35);
        ridge = Math.max(0.0, ridge);
        ridge = ridge * ridge;

        double detail = (noiseDetail.getValue(wx * 0.02000, wz * 0.02000) + 1.0) * 0.5 * 0.08;

        double h = SEA_LEVEL + factor * (base * 70.0 + ridge * 140.0 + detail * 24.0);
        return (int) Math.min(h, MAX_PEAK);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ОБЩИЕ ХЕЛПЕРЫ
    // ═════════════════════════════════════════════════════════════════════════

    private void generateOreVein(World world, Random random, CubePos pos,
                                   Block ore, int veinSize, int attempts,
                                   int oreMinY, int oreMaxY) {
        int fromY = Math.max(pos.getMinBlockY(), oreMinY);
        int toY   = Math.min(pos.getMaxBlockY(), oreMaxY);
        if (fromY > toY) return;
        WorldGenMinable gen  = getOreGen(ore, veinSize);
        int baseX = pos.getMinBlockX(), baseZ = pos.getMinBlockZ();
        int rangeY = toY - fromY;
        BlockPos.MutableBlockPos orePos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < attempts; i++) {
            orePos.setPos(
                baseX + random.nextInt(16),
                fromY + (rangeY > 0 ? random.nextInt(rangeY + 1) : 0),
                baseZ + random.nextInt(16)
            );
            gen.generate(world, random, orePos);
        }
    }

    private void generateOreSphere(World world, Random random, CubePos pos,
                                     Block block, int radius, int oreMinY, int oreMaxY) {
        int fromY = Math.max(pos.getMinBlockY(), oreMinY);
        int toY   = Math.min(pos.getMaxBlockY(), oreMaxY);
        if (fromY > toY) return;
        int rangeY = toY - fromY;
        sphere(world, new BlockPos(
            pos.getMinBlockX() + random.nextInt(16),
            fromY + (rangeY > 0 ? random.nextInt(rangeY + 1) : 0),
            pos.getMinBlockZ() + random.nextInt(16)
        ), block, radius);
    }

    private void sphere(World world, BlockPos c, Block block, int r) {
        IBlockState s = block.getDefaultState();
        int r2 = r * r;
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++)
            for (int dy = -r; dy <= r; dy++)
                for (int dz = -r; dz <= r; dz++) {
                    if (dx*dx + dy*dy + dz*dz > r2) continue;
                    p.setPos(c.getX()+dx, c.getY()+dy, c.getZ()+dz);
                    Block b = world.getBlockState(p).getBlock();
                    if (b == Blocks.STONE || b == BlockInit.deepslate)
                        world.setBlockState(p, s, 2);
                }
    }

    private void sculk(World world, Random rand, BlockPos c, int r) {
        IBlockState s = BlockInit.sculk.getDefaultState();
        int r2 = r * r;
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos n = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++)
            for (int dy = -r; dy <= r; dy++)
                for (int dz = -r; dz <= r; dz++) {
                    if (dx*dx + dy*dy + dz*dz > r2 || rand.nextFloat() >= 0.7f) continue;
                    int px = c.getX()+dx, py = c.getY()+dy, pz = c.getZ()+dz;
                    p.setPos(px, py, pz);
                    Block b = world.getBlockState(p).getBlock();
                    if ((b == Blocks.STONE || b == BlockInit.deepslate || b == BlockInit.tuff)
                            && airNeighbour(world, n, px, py, pz))
                        world.setBlockState(p, s, 2);
                }
    }

    private void carveLargeRoom(World world, Random random, CubePos target,
                                  int cx, int cy, int cz) {
        float rx = 5.0f + random.nextFloat() * 5.0f;
        float ry = 3.0f + random.nextFloat() * 3.0f;
        float rz = 5.0f + random.nextFloat() * 5.0f;
        carveSphere(world, cx, cy, cz, rx, ry, rz,
                target.getMinBlockX(), target.getMaxBlockX(),
                target.getMinBlockZ(), target.getMaxBlockZ());
    }

    private void carveTunnel(World world, Random random, CubePos target,
                               float sx, float sy, float sz, boolean branch) {
        float yaw   = random.nextFloat() * (float)(Math.PI * 2);
        float pitch = (random.nextFloat() - 0.5f) * 0.5f;
        float x = sx, y = sy, z = sz;
        float w = branch ? (1.2f + random.nextFloat() * 1.5f)
                         : (2.8f + random.nextFloat() * 3.2f);
        int steps = branch ? (30 + random.nextInt(50)) : (60 + random.nextInt(100));

        int minBX = target.getMinBlockX(), maxBX = target.getMaxBlockX();
        int minBZ = target.getMinBlockZ(), maxBZ = target.getMaxBlockZ();
        int margin = (int) Math.ceil(w) + 1;
        int outsideStreak = 0;

        for (int s = 0; s < steps; s++) {
            float cp = MathHelper.cos(pitch);
            x += MathHelper.cos(yaw) * cp;
            y += MathHelper.sin(pitch);
            z += MathHelper.sin(yaw) * cp;
            if (y <= BEDROCK_CEIL || y >= -0.5f) break;

            yaw   += (random.nextFloat() - 0.5f) * 0.5f;
            pitch  = pitch * 0.75f + (random.nextFloat() - 0.5f) * 0.15f;

            if (random.nextFloat() < 0.2f) {
                w = branch ? (1.0f + random.nextFloat() * 1.5f)
                           : (2.0f + random.nextFloat() * 4.5f);
                margin = (int) Math.ceil(w) + 1;
            }

            int ix = (int) x, iy = (int) y, iz = (int) z;
            if (ix < minBX - margin || ix > maxBX + margin
                    || iz < minBZ - margin || iz > maxBZ + margin) {
                if (++outsideStreak > 20) break;
                continue;
            }
            outsideStreak = 0;
            carveSphere(world, ix, iy, iz, w, w * 0.45f, w, minBX, maxBX, minBZ, maxBZ);
        }
    }

    private void carveSphere(World world, int cx, int cy, int cz,
                               float rx, float ry, float rz,
                               int minBX, int maxBX, int minBZ, int maxBZ) {
        int irx = Math.max(1, (int) Math.ceil(rx));
        int iry = Math.max(1, (int) Math.ceil(ry));
        int irz = Math.max(1, (int) Math.ceil(rz));
        float invRx = 1f / rx, invRy = 1f / ry, invRz = 1f / rz;

        int wyMin = Math.max(cy - iry, BEDROCK_CEIL + 1);
        int wyMax = Math.min(cy + iry, STONE_START - 1);
        if (wyMin > wyMax) return;
        int dyAbsMin = wyMin - cy, dyAbsMax = wyMax - cy;

        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int dx = -irx; dx <= irx; dx++) {
            float fnx  = dx * invRx, fnx2 = fnx * fnx;
            if (fnx2 > 1.0f) continue;
            int wx = cx + dx;
            if (wx < minBX || wx > maxBX) continue;

            for (int dz = -irz; dz <= irz; dz++) {
                float fnz = dz * invRz, xz = fnx2 + fnz * fnz;
                if (xz > 1.0f) continue;
                int wz = cz + dz;
                if (wz < minBZ || wz > maxBZ) continue;

                for (int dy = dyAbsMin; dy <= dyAbsMax; dy++) {
                    float fny = dy * invRy;
                    if (xz + fny * fny > 1.0f) continue;
                    p.setPos(wx, cy + dy, wz);
                    Block b = world.getBlockState(p).getBlock();
                    if (b == BlockInit.deepslate          || b == Blocks.STONE
                            || b == Blocks.DIRT           || b == Blocks.GRAVEL
                            || b == BlockInit.tuff        || b == BlockInit.calcite
                            || b == BlockInit.dripstone_block
                            || b == BlockInit.deepslate_coal_ore
                            || b == BlockInit.deepslate_iron_ore
                            || b == BlockInit.deepslate_gold_ore
                            || b == BlockInit.deepslate_diamond_ore
                            || b == BlockInit.deepslate_emerald_ore
                            || b == BlockInit.deepslate_redstone_ore
                            || b == BlockInit.deepslate_lapis_lazuli_ore) {
                        world.setBlockState(p, CS_AIR, 2);
                    }
                }
            }
        }
    }

    private void generateWaterPocket(World world, Random random, CubePos pos) {
        int fromY = Math.max(pos.getMinBlockY(), WATER_MIN);
        int toY   = Math.min(pos.getMaxBlockY(), WATER_MAX);
        if (fromY > toY) return;

        int baseX = pos.getMinBlockX(), baseZ = pos.getMinBlockZ();
        int cx = baseX + 4 + random.nextInt(8), cz = baseZ + 4 + random.nextInt(8);
        int minBX = baseX, maxBX = pos.getMaxBlockX();
        int minBZ = baseZ, maxBZ = pos.getMaxBlockZ();

        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        int floorY = Integer.MIN_VALUE;
        for (int wy = toY; wy >= fromY; wy--) {
            mp.setPos(cx, wy, cz);
            if (world.getBlockState(mp).getBlock() != Blocks.AIR) continue;
            mp.setPos(cx, wy - 1, cz);
            Block bot = world.getBlockState(mp).getBlock();
            if (bot != Blocks.AIR && bot != Blocks.LAVA && bot != Blocks.FLOWING_LAVA) {
                floorY = wy; break;
            }
        }
        if (floorY == Integer.MIN_VALUE || floorY - 1 <= BEDROCK_CEIL) return;

        int r = 2 + random.nextInt(2), r2 = r * r;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz > r2) continue;
                int bx = cx + dx, bz = cz + dz;
                if (bx < minBX || bx > maxBX || bz < minBZ || bz > maxBZ) continue;
                mp.setPos(bx, floorY - 1, bz);
                Block fb = world.getBlockState(mp).getBlock();
                if (fb == BlockInit.deepslate || fb == Blocks.STONE || fb == Blocks.DIRT) {
                    world.setBlockState(mp, CS_AIR,   2);
                    world.setBlockState(mp, CS_WATER, 3);
                } else {
                    mp.setPos(bx, floorY, bz);
                    if (world.getBlockState(mp).getBlock() == Blocks.AIR)
                        world.setBlockState(mp, CS_WATER, 3);
                }
            }
        }
    }

    private boolean airNeighbour(World w, BlockPos.MutableBlockPos n, int x, int y, int z) {
        n.setPos(x, y+1, z); if (w.isAirBlock(n)) return true;
        n.setPos(x, y-1, z); if (w.isAirBlock(n)) return true;
        n.setPos(x, y, z+1); if (w.isAirBlock(n)) return true;
        n.setPos(x, y, z-1); if (w.isAirBlock(n)) return true;
        n.setPos(x+1, y, z); if (w.isAirBlock(n)) return true;
        n.setPos(x-1, y, z); return w.isAirBlock(n);
    }
}
