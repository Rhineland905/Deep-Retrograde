package com.deepretrograde.objects.blocks;

import com.deepretrograde.init.SoundInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockSculk extends BlockBase {

    public BlockSculk(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.SCULK_SOUNDS);
        setHardness(0.2F);
        setResistance(0.2F);
        this.setHarvestLevel("hoe", 1);
        this.setHarvestLevel("pickaxe", -1);
        setLightLevel(0.0F);
        setLightOpacity(255);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }
    @Override
    public int quantityDropped(Random random) {
        return 0;
    }
    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        return 1 + (int)(Math.random() * 8);
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }
}
