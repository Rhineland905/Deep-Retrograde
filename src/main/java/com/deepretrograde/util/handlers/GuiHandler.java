package com.deepretrograde.util.handlers;

import com.deepretrograde.objects.GUI.StoneCutter.ContainerStoneCutter;
import com.deepretrograde.objects.GUI.StoneCutter.GuiStoneCutter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int STONECUTTER = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {

        if (ID == STONECUTTER) {
            return new ContainerStoneCutter(player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {

        if (ID == STONECUTTER) {
            return new GuiStoneCutter(player.inventory);
        }
        return null;
    }
}