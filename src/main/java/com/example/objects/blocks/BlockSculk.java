package com.example.objects.blocks;

import com.example.init.SoundInit;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockSculk extends BlockBase {

    public BlockSculk(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.SCULK_SOUNDS);
        setHardness(0.6F);
        setResistance(0.6F);
        setHarvestLevel("hoe", 0);
        setLightLevel(0.0F);
        setLightOpacity(255);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
}
