package com.example.objects.blocks;

import com.example.init.BlockInit;
import com.example.init.SoundInit;
import com.example.util.IHasModel;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockDeepslate extends BlockBase implements IHasModel {

    public BlockDeepslate(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.DEEPSLATE_SOUNDS);
        setHardness(3.0F);
        setResistance(6.0F);
        this.setHarvestLevel("pickaxe", 1);
        setLightLevel(0.0F);
        setLightOpacity(255);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(BlockInit.cobbled_deepslate);
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(this);
    }
}