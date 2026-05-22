package com.deepretrograde.objects.blocks;

import com.deepretrograde.init.SoundInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import java.util.Random;

public class BlockDripstone extends BlockBase {

    public BlockDripstone(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.DRIPSTONE_SOUNDS);
        setHardness(1.5F);
        setResistance(3.0F);
        this.setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // При разрушении выпадает сам блок
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    protected boolean canSilkHarvest() {
        // Позволяет собирать блок шелковым касанием (хотя он и так выпадает)
        return true;
    }
}