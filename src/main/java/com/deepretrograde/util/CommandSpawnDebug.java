package com.deepretrograde.util;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;

/**
 * /drspawn — диагностика спавна враждебных мобов в точке игрока.
 * Повторяет проверки спавнера CubicChunks (CubeWorldEntitySpawner) по шагам и
 * считает, на каком из них отсеиваются позиции вокруг игрока.
 */
public class CommandSpawnDebug extends CommandBase {

    private static final int RADIUS = 24;

    @Override
    public String getName() { return "drspawn"; }

    @Override
    public String getUsage(ICommandSender sender) { return "/drspawn [x y z]"; }

    @Override
    public int getRequiredPermissionLevel() { return 2; }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldServer world;
        BlockPos p;
        if (args.length >= 3) {
            // Из консоли/RCON: /drspawn x y z — проверка без игрока (overworld)
            world = server.getWorld(0);
            p = parseBlockPos(sender, args, 0, false);
        } else {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            world = player.getServerWorld();
            p = new BlockPos(player);
        }

        Boolean hostileAllowed = null;
        try {
            hostileAllowed = ObfuscationReflectionHelper.getPrivateValue(
                    net.minecraft.world.World.class, world, "field_72985_G"); // spawnHostileMobs
        } catch (Exception ignored) { }

        send(sender, "§6--- drspawn @ " + p.getX() + " " + p.getY() + " " + p.getZ() + " dim " + world.provider.getDimension());
        send(sender, "difficulty=" + world.getDifficulty()
                + " doMobSpawning=" + world.getGameRules().getBoolean("doMobSpawning")
                + " spawnHostileMobs=" + hostileAllowed);
        send(sender, "world=" + world.getClass().getName());

        int height = world.getHeight(p.getX(), p.getZ());
        send(sender, "getHeight(x,z)=" + height + "  (спавнер пропускает кубы с minY > этого значения)");
        send(sender, "light: sky=" + world.getLightFor(EnumSkyBlock.SKY, p)
                + " block=" + world.getLightFor(EnumSkyBlock.BLOCK, p)
                + " fromNeighbors=" + world.getLightFromNeighbors(p)
                + " skySubtracted=" + world.getSkylightSubtracted());

        int monsters = world.countEntities(EnumCreatureType.MONSTER, true);
        send(sender, "loaded MONSTER count=" + monsters + " (cap ~" + EnumCreatureType.MONSTER.getMaxNumberOfCreature() + " * cubes/289)");

        Biome biome = world.getBiome(p);
        List<Biome.SpawnListEntry> list = world.getChunkProvider().getPossibleCreatures(EnumCreatureType.MONSTER, p);
        send(sender, "biome=" + biome.getRegistryName() + " monster spawn entries here=" + (list == null ? "null" : list.size())
                + " (biome list=" + biome.getSpawnableList(EnumCreatureType.MONSTER).size() + ")");

        // Перебор позиций вокруг игрока: на каком шаге отсеиваются
        int total = 0, aboveHeight = 0, solid = 0, placement = 0, light = 0, ok = 0;
        BlockPos firstOk = null;
        EntityLiving probe = new EntityZombie(world);
        for (int dx = -RADIUS; dx <= RADIUS; dx += 2) {
            for (int dz = -RADIUS; dz <= RADIUS; dz += 2) {
                int h = world.getHeight(p.getX() + dx, p.getZ() + dz);
                for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                    BlockPos q = p.add(dx, dy, dz);
                    total++;
                    if ((q.getY() & ~15) > h) { aboveHeight++; continue; }
                    if (world.getBlockState(q).isNormalCube()) { solid++; continue; }
                    if (!WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, world, q)) {
                        placement++; continue;
                    }
                    probe.setLocationAndAngles(q.getX() + 0.5, q.getY(), q.getZ() + 0.5, 0, 0);
                    if (world.getLightFor(EnumSkyBlock.SKY, q) > 7 && world.getLightFromNeighbors(q) > 7) { light++; continue; }
                    ok++;
                    if (firstOk == null) firstOk = q;
                }
            }
        }
        send(sender, "positions=" + total + " | выше getHeight=" + aboveHeight + " | в блоке=" + solid
                + " | нет опоры=" + placement + " | светло=" + light + " | §aгодны=" + ok);

        // Какие монстры загружены сейчас
        java.util.Map<String, Integer> byType = new java.util.TreeMap<>();
        for (net.minecraft.entity.Entity e : world.loadedEntityList) {
            if (e.isCreatureType(EnumCreatureType.MONSTER, false)) {
                String id = String.valueOf(net.minecraft.entity.EntityList.getKey(e));
                byType.merge(id, 1, Integer::sum);
            }
        }
        send(sender, "loaded monsters by type: " + byType);

        // Полная проверка как у спавнера для каждого моба из списка биома в первой годной точке
        if (firstOk != null && list != null) {
            send(sender, "test spawn at " + firstOk);
            for (Biome.SpawnListEntry entry : list) {
                String verdict;
                try {
                    EntityLiving mob = entry.newInstance(world);
                    mob.setLocationAndAngles(firstOk.getX() + 0.5, firstOk.getY(), firstOk.getZ() + 0.5, 0, 0);
                    net.minecraftforge.fml.common.eventhandler.Event.Result r = net.minecraftforge.event.ForgeEventFactory
                            .canEntitySpawn(mob, world, firstOk.getX() + 0.5f, firstOk.getY(), firstOk.getZ() + 0.5f, null);
                    boolean can = mob.getCanSpawnHere();
                    boolean notColliding = mob.isNotColliding();
                    verdict = "event=" + r + " canSpawnHere=" + can + " notColliding=" + notColliding;
                } catch (Throwable t) {
                    verdict = "ERROR " + t;
                }
                send(sender, "  " + entry.entityClass.getSimpleName() + " w=" + entry.itemWeight + ": " + verdict);
            }
            // Кто подписан на CheckSpawn
            try {
                net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn ev =
                        new net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn(new EntityZombie(world), world, 0, 0, 0, null);
                for (net.minecraftforge.fml.common.eventhandler.IEventListener l : ev.getListenerList().getListeners(0)) {
                    send(sender, "  CheckSpawn listener: " + l);
                }
            } catch (Throwable t) {
                send(sender, "  listeners: ERROR " + t);
            }
        }
    }

    private static void send(ICommandSender s, String msg) {
        s.sendMessage(new TextComponentString(msg));
    }
}
