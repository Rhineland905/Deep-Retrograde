package com.deepretrograde.objects.blocks;

import com.deepretrograde.init.SoundInit;
import net.minecraft.block.material.Material;

import net.minecraft.creativetab.CreativeTabs;



public class BlockTuffBricks extends BlockBase {

    public BlockTuffBricks(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.TUFF_BRICKS_SOUNDS);
        setHardness(1.5F);
        setResistance(6.0F);
        this.setHarvestLevel("pickaxe", 1);
        setLightLevel(0.0F);
        setLightOpacity(255);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

}
