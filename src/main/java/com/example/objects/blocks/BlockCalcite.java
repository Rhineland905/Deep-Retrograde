package com.example.objects.blocks;

import com.example.init.SoundInit;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCalcite extends BlockBase {

    public BlockCalcite(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.CALCITE_SOUNDS);
        setHardness(0.75F);
        setResistance(0.75F);
        this.setHarvestLevel("pickaxe", 0);
        setLightLevel(0.0F);
        setLightOpacity(255);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
}